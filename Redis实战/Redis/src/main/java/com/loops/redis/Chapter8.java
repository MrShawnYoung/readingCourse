package com.loops.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Loops
 */
public class Chapter8 {
    //函数每次被调用时，最多只会将状态消息发送给1000个关注者
    private static int POSTS_PER_PASS = 1000;
    private static int HOME_TIMELINE_SIZE = 1000;

    public String acquireLockWithTimeout(Jedis conn, String lockName, int acquireTimeout, int lockTimeout) {
        String id = UUID.randomUUID().toString();
        lockName = "lock:" + lockName;
        long end = System.currentTimeMillis() + (acquireTimeout * 1000);
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(lockName, id) >= 1) {
                conn.expire(lockName, lockTimeout);
                return id;
            } else if (conn.ttl(lockName) <= 0) {
                conn.expire(lockName, lockTimeout);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.interrupted();
            }
        }
        return null;
    }

    public boolean releaseLock(Jedis conn, String lockName, String identifier) {
        lockName = "lock:" + lockName;
        while (true) {
            conn.watch(lockName);
            if (identifier.equals(conn.get(lockName))) {
                Transaction trans = conn.multi();
                trans.del(lockName);
                List<Object> result = trans.exec();
                if (result == null) {
                    continue;
                }
                return true;
            }
            conn.unwatch();
            break;
        }
        return false;
    }

    /*创建用户*/
    public long createUser(Jedis conn, String login, String name) {
        String llogin = login.toLowerCase();
        // 使用第6章定义的加锁函数尝试对小写的用户名进行加锁
        String lock = acquireLockWithTimeout(conn, "user:" + llogin, 10, 1);
        // 如果加锁不成功，那么说明给定的用户名已经被其他用户占用
        if (lock == null) {
            return -1;
        }
        // 程序使用了一个散列来存储小写的用户名以及用户ID之间的映射，
        // 如果给定的用户名已经被映射到了某个用户ID，那么程序就不会再将这个用户名分配给其他人
        if (conn.hget("users:", llogin) != null) {
            return -1;
        }
        // 每个用户都有一个独一无二的ID，这个ID是通过对计数器执行自增操作产生的
        long id = conn.incr("user:id:");
        Transaction trans = conn.multi();
        // 在散列里面将小写的用户名映射至用户ID
        trans.hset("users:", llogin, String.valueOf(id));
        // 将用户信息添加到用户对应的散列里面
        Map<String, String> values = new HashMap<String, String>();
        values.put("login", login);
        values.put("id", String.valueOf(id));
        values.put("name", name);
        values.put("followers", "0");
        values.put("following", "0");
        values.put("posts", "0");
        values.put("signup", String.valueOf(System.currentTimeMillis()));
        trans.hmset("user:" + id, values);
        trans.exec();
        // 释放之前对用户名加的锁
        releaseLock(conn, "user:" + llogin, lock);
        // 返回用户ID
        return id;
    }

    @SuppressWarnings("unchecked")
    /*关注操作*/
    public boolean followUser(Jedis conn, long uid, long otherUid) {
        // 把正在关注有序集合以及关注者有序集合的键名缓存起来
        String fkey1 = "following:" + uid;
        String fkey2 = "followers:" + otherUid;
        // 如果uid指定的用户已经关注了other_uid指定的用户，那么返回
        if (conn.zscore(fkey1, String.valueOf(otherUid)) != null) {
            return false;
        }
        long now = System.currentTimeMillis();
        Transaction trans = conn.multi();
        // 将两个用户的ID分别添加到相应的正在关注有序集合以及关注者有序集合里面
        trans.zadd(fkey1, now, String.valueOf(otherUid));
        trans.zadd(fkey2, now, String.valueOf(uid));
        trans.zcard(fkey1);
        trans.zcard(fkey2);
        // 从被关注用户的个人时间线里面获取HOME_TIMELINE_SIZE条最新的状态消息
        trans.zrevrangeWithScores("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);
        List<Object> response = trans.exec();
        long following = (Long) response.get(response.size() - 3);
        long followers = (Long) response.get(response.size() - 2);
        Set<Tuple> statuses = (Set<Tuple>) response.get(response.size() - 1);
        trans = conn.multi();
        // 修改两个用户的散列，更新他们各自的正在关注数量以及关注者数量
        trans.hset("user:" + uid, "following", String.valueOf(following));
        trans.hset("user:" + otherUid, "followers", String.valueOf(followers));
        if (statuses.size() > 0) {
            // 对执行关注操作的用户的主页时间线进行更新，并保留时间线上面的最新1000条状态消息
            for (Tuple status : statuses) {
                trans.zadd("home:" + uid, status.getScore(), status.getElement());
            }
        }
        trans.zremrangeByRank("home:" + uid, 0, 0 - HOME_TIMELINE_SIZE - 1);
        trans.exec();
        // 返回True表示关注操作已经成功执行
        return true;
    }

    @SuppressWarnings("unchecked")
    /*取消关注*/
    public boolean unfollowUser(Jedis conn, long uid, long otherUid) {
        // 把正在关注有序集合以及关注者有序集合的键名缓存起来
        String fkey1 = "following:" + uid;
        String fkey2 = "followers:" + otherUid;
        // 如果uid指定的用户并未关注other_uid指定的用户，那么函数直接返回
        if (conn.zscore(fkey1, String.valueOf(otherUid)) == null) {
            return false;
        }
        Transaction trans = conn.multi();
        // 从正在关注有序集合以及关注者有序集合里面移除双方的用户ID
        trans.zrem(fkey1, String.valueOf(otherUid));
        trans.zrem(fkey2, String.valueOf(uid));
        trans.zcard(fkey1);
        trans.zcard(fkey2);
        // 获取被取消关注的用户最近发布的HOME_TIMELINE_SIZE条状态消息
        trans.zrevrange("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);
        List<Object> response = trans.exec();
        long following = (Long) response.get(response.size() - 3);
        long followers = (Long) response.get(response.size() - 2);
        Set<String> statuses = (Set<String>) response.get(response.size() - 1);
        trans = conn.multi();
        // 对相应用户信息散列里面的正在关注数量以及关注者数量进行更新
        trans.hset("user:" + uid, "following", String.valueOf(following));
        trans.hset("user:" + otherUid, "followers", String.valueOf(followers));
        if (statuses.size() > 0) {
            for (String status : statuses) {
                // 对执行取消关注操作的用户的主页时间线进行更新，移除被取消关注的用户发布的所有状态消息
                trans.zrem("home:" + uid, status);
            }
        }
        trans.exec();
        // 返回True表示取消关注操作执行成功
        return true;
    }

    public long createStatus(Jedis conn, long uid, String message) {
        return createStatus(conn, uid, message, null);
    }

    /*创建状态消息*/
    public long createStatus(Jedis conn, long uid, String message, Map<String, String> data) {
        Transaction trans = conn.multi();
        // 根据用户ID获取用户的用户名
        trans.hget("user:" + uid, "login");
        // 为这条状态消息创建一个新的ID
        trans.incr("status:id:");
        List<Object> response = trans.exec();
        String login = (String) response.get(0);
        long id = (Long) response.get(1);
        // 在发布状态消息之前，先验证用户的账号是否存在
        if (login == null) {
            return -1;
        }
        if (data == null) {
            data = new HashMap<String, String>();
        }
        // 筹备并设置状态消息的各项信息
        data.put("message", message);
        data.put("posted", String.valueOf(System.currentTimeMillis()));
        data.put("id", String.valueOf(id));
        data.put("uid", String.valueOf(uid));
        data.put("login", login);
        trans = conn.multi();
        trans.hmset("status:" + id, data);
        // 更新用户的已发送状态消息数量
        trans.hincrBy("user:" + uid, "posts", 1);
        trans.exec();
        // 返回新创建的状态消息的ID
        return id;
    }

    public long postStatus(Jedis conn, long uid, String message) {
        return postStatus(conn, uid, message, null);
    }

    /*状态更新*/
    public long postStatus(Jedis conn, long uid, String message, Map<String, String> data) {
        // 使用之前介绍过的函数来创建一条新的状态消息
        long id = createStatus(conn, uid, message, data);
        // 如果创建状态消息失败，那么直接返回
        if (id == -1) {
            return -1;
        }
        // 获取消息的发布时间
        String postedString = conn.hget("status:" + id, "posted");
        // 如果程序未能顺利地获取消息地发布时间，那么直接返回
        if (postedString == null) {
            return -1;
        }
        long posted = Long.parseLong(postedString);
        // 将状态消息添加到用户的个人时间线里面
        conn.zadd("profile:" + uid, posted, String.valueOf(id));
        // 将状态消息推送给用户的关注者
        syndicateStatus(conn, uid, id, posted, 0);
        return id;
    }

    /*对关注者的主页时间线进行更新*/
    public void syndicateStatus(Jedis conn, long uid, long postId, long postTime, double start) {
        // 以上次被更新的最后一个关注者为起点，获取接下来的1000个关注者
        Set<Tuple> followers = conn.zrangeByScoreWithScores("followers:" + uid, String.valueOf(start), "inf", 0, POSTS_PER_PASS);
        Transaction trans = conn.multi();
        // 在遍历关注者的同时，对start变量的值进行更新，这个变量可以在有需要的时候传递给下一个syndicateStatus()调用
        for (Tuple tuple : followers) {
            String follower = tuple.getElement();
            start = tuple.getScore();
            // 将状态消息添加到所有被获取的关注者的主页时间线里面，并在有需要的时候对关注者的主页时间进行修剪，防止它超过限定的最大长度
            trans.zadd("home:" + follower, postTime, String.valueOf(postId));
            trans.zrange("home:" + follower, 0, -1);
            trans.zremrangeByRank("home:" + follower, 0, 0 - HOME_TIMELINE_SIZE - 1);
        }
        trans.exec();
        // 如果需要更新的关注者数量超过1000人，那么在延迟任务里面继续执行剩余的更新操作
        if (followers.size() >= POSTS_PER_PASS) {
            try {
                Method method = getClass().getDeclaredMethod("syndicateStatus", Jedis.class, Long.TYPE, Long.TYPE, Long.TYPE, Double.TYPE);
                executeLater("default", method, uid, postId, postTime, start);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*删除状态消息*/
    public boolean deleteStatus(Jedis conn, long uid, long statusId) {
        String key = "status:" + statusId;
        // 对指定的状态消息进行加锁，防止两个程序同时删除同一条状态消息的情况出现
        String lock = acquireLockWithTimeout(conn, key, 1, 10);
        // 如果加锁失败，那么直接返回
        if (lock == null) {
            return false;
        }
        try {
            // 如果uid指定的用户并非状态消息的发布人，那么函数直接返回
            if (!String.valueOf(uid).equals(conn.hget(key, "uid"))) {
                return false;
            }
            Transaction trans = conn.multi();
            // 删除指定的状态消息
            trans.del(key);
            // 从用户的个人时间线里面移除被删除状态消息的ID
            trans.zrem("profile:" + uid, String.valueOf(statusId));
            // 从用户的主页时间线里面移除被删除状态消息的ID
            trans.zrem("home:" + uid, String.valueOf(statusId));
            // 对存储着用户信息的散列进行更新，减少已发布状态消息的数量
            trans.hincrBy("user:" + uid, "posts", -1);
            trans.exec();
            return true;
        } finally {
            releaseLock(conn, key, lock);
        }
    }

    public List<Map<String, String>> getStatusMessages(Jedis conn, long uid) {
        return getStatusMessages(conn, uid, 1, 30);
    }

    @SuppressWarnings("unchecked")
    /*获取状态消息*/
    // 函数接受3个可选参数，它们分别用户指定函数要获取哪条时间线、要获取多少页时间线，以及每页要有多少条状态信息
    public List<Map<String, String>> getStatusMessages(Jedis conn, long uid, int page, int count) {
        // 获取时间线上面最新的状态消息ID
        Set<String> statusIds = conn.zrevrange("home:" + uid, (page - 1) * count, page * count - 1);
        Transaction trans = conn.multi();
        // 获取状态消息本身
        for (String id : statusIds) {
            trans.hgetAll("status:" + id);
        }
        List<Map<String, String>> statuses = new ArrayList<Map<String, String>>();
        for (Object result : trans.exec()) {
            Map<String, String> status = (Map<String, String>) result;
            if (status != null && status.size() > 0) {
                statuses.add(status);
            }
        }
        // 使用过滤器移除那些已经被删除了的状态消息
        return statuses;
    }

    public void executeLater(String queue, Method method, Object... args) {
        MethodThread thread = new MethodThread(this, method, args);
        thread.start();
    }

    public class MethodThread extends Thread {
        private Object instance;
        private Method method;
        private Object[] args;

        public MethodThread(Object instance, Method method, Object... args) {
            this.instance = instance;
            this.method = method;
            this.args = args;
        }

        public void run() {
            Jedis conn = new Jedis("localhost");
            conn.select(15);
            Object[] args = new Object[this.args.length + 1];
            System.arraycopy(this.args, 0, args, 1, this.args.length);
            args[0] = conn;
            try {
                method.invoke(instance, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}