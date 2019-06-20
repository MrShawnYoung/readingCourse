package com.loops.redis;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Loops
 */
public class Chapter2 {
    /*检查登录cookies*/
    public String checkToken(Jedis conn, String token) {
        // 尝试获取并返回令牌对应的用户
        return conn.hget("login:", token);
    }

    /*更新令牌*/
    public void updateToken(Jedis conn, String token, String user, String item) {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        // 维持令牌与已登录用户之间的映射
        conn.hset("login:", token, user);
        // 记录令牌最后一次出现时间
        conn.zadd("recent:", timestamp, token);
        if (item != null) {
            // 记录用户浏览过的商品
            conn.zadd("viewed:" + token, timestamp, item);
            // 移除旧的记录，只保留用户最近浏览的25个商品
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            // 这行代码是新添加的
            conn.zincrby("viewed:", -1, item);
        }
    }

    /*更新购物车*/
    public void addToCart(Jedis conn, String session, String item, int count) {
        if (count <= 0) {
            // 从购物车里面移除指定的商品
            conn.hdel("cart:" + session, item);
        } else {
            // 将指定的商品添加到购物车
            conn.hset("cart:" + session, item, String.valueOf(count));
        }
    }

    /*调度缓存*/
    public void scheduleRowCache(Jedis conn, String rowId, int delay) {
        // 先设置数据行的延迟值
        conn.zadd("delay:", delay, rowId);
        // 立即对需要缓存的数据行进行调度
        conn.zadd("schedule:", System.currentTimeMillis() / 1000, rowId);
    }

    /*缓存函数*/
    public String cacheRequest(Jedis conn, String request, Callback callback) {
        // 对于不能被缓存的请求，直接调用回调函数
        if (!canCache(conn, request)) {
            return callback != null ? callback.call(request) : null;
        }
        // 将请求转换成一个简单的字符串键，方便之后进行查找
        String pageKey = "cache:" + hashRequest(request);
        // 尝试查找被缓存的页面
        String content = conn.get(pageKey);
        if (content == null && callback != null) {
            // 如果页面还没有被缓存，那么生成页面
            content = callback.call(request);
            // 将新生成的页面放到缓存里面
            conn.setex(pageKey, 300, content);
        }
        // 返回页面
        return content;
    }

    public boolean canCache(Jedis conn, String request) {
        try {
            URL url = new URL(request);
            HashMap<String, String> params = new HashMap<String, String>();
            if (url.getQuery() != null) {
                for (String param : url.getQuery().split("&")) {
                    String[] pair = param.split("=", 2);
                    params.put(pair[0], pair.length == 2 ? pair[1] : null);
                }
            }
            String itemId = extractItemId(params);
            if (itemId == null || isDynamic(params)) {
                return false;
            }
            Long rank = conn.zrank("viewed:", itemId);
            return rank != null && rank < 10000;
        } catch (MalformedURLException mue) {
            return false;
        }
    }

    public boolean isDynamic(Map<String, String> params) {
        return params.containsKey("_");
    }

    public String extractItemId(Map<String, String> params) {
        return params.get("item");
    }

    public String hashRequest(String request) {
        return String.valueOf(request.hashCode());
    }

    public interface Callback {
        public String call(String request);
    }

    /*清理旧回会话程序*/
    public class CleanSessionsThread extends Thread {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                // 找出目前已有令牌的数量
                long size = conn.zcard("recent:");
                // 令牌数量未超过限制，休眠并在之后重新检查
                if (size <= limit) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                // 获取需要移除的令牌ID
                long endIndex = Math.min(size - limit, 100);
                Set<String> tokenSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);
                // 为那些将要被删除的令牌构建键名
                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String token : tokens) {
                    sessionKeys.add("viewed:" + token);
                }
                // 移除最旧的那些令牌
                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                conn.hdel("login:", tokens);
                conn.zrem("recent:", tokens);
            }
        }
    }

    // 清理所有会话
    public class CleanFullSessionsThread extends Thread {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanFullSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                long size = conn.zcard("recent:");
                if (size <= limit) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                long endIndex = Math.min(size - limit, 100);
                Set<String> sessionSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] sessions = sessionSet.toArray(new String[sessionSet.size()]);
                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String sess : sessions) {
                    sessionKeys.add("viewed:" + sess);
                    // 新增加的这行代码用于删除旧会话对应用户的购物车
                    sessionKeys.add("cart:" + sess);
                }
                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                conn.hdel("login:", sessions);
                conn.zrem("recent:", sessions);
            }
        }
    }

    /*守护线程函数*/
    public class CacheRowsThread extends Thread {
        private Jedis conn;
        private boolean quit;

        public CacheRowsThread() {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            Gson gson = new Gson();
            while (!quit) {
                // 尝试获取下一个需要被缓存的数据行以及该行的调度时间戳，命令会返回一个包含零个或一个元组（tuple）的列表
                Set<Tuple> range = conn.zrangeWithScores("schedule:", 0, 0);
                Tuple next = range.size() > 0 ? range.iterator().next() : null;
                long now = System.currentTimeMillis() / 1000;
                if (next == null || next.getScore() > now) {
                    try {
                        // 暂时没有行需要被缓存，休眠50毫秒后重试
                        sleep(50);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                String rowId = next.getElement();
                // 提前获取下一次调度的延迟时间
                double delay = conn.zscore("delay:", rowId);
                if (delay <= 0) {
                    // 不必再缓存这个行，将它从缓存中移除
                    conn.zrem("delay:", rowId);
                    conn.zrem("schedule:", rowId);
                    conn.del("inv:" + rowId);
                    continue;
                }
                // 读取数据行
                Inventory row = Inventory.get(rowId);
                // 更新调度时间并设置缓存值
                conn.zadd("schedule:", now + delay, rowId);
                conn.set("inv:" + rowId, gson.toJson(row));
            }
        }
    }

    public static class Inventory {
        private String id;
        private String data;
        private long time;

        private Inventory(String id) {
            this.id = id;
            this.data = "data to cache...";
            this.time = System.currentTimeMillis() / 1000;
        }

        public static Inventory get(String id) {
            return new Inventory(id);
        }
    }
}