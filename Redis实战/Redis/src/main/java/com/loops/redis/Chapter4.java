package com.loops.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Loops
 */
public class Chapter4 {
    /*将商品放到市场上销售*/
    public boolean listItem(Jedis conn, String itemId, String sellerId, double price) {
        String inventory = "inventory:" + sellerId;
        String item = itemId + '.' + sellerId;
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end) {
            // 监视用户包裹发生的变化
            conn.watch(inventory);
            // 检查用户是否仍然持有将要被销售的商品
            if (!conn.sismember(inventory, itemId)) {
                // 如果指定的商品不在用户的包裹里面，那么停止对包裹键的监视并返回一个空值
                conn.unwatch();
                return false;
            }
            // 把被销售的商品添加到商品买卖市场里面
            Transaction trans = conn.multi();
            trans.zadd("market:", price, item);
            trans.srem(inventory, itemId);
            // 如果执行execute方法没有引发WatchError异常，那么说明事务执行成功，并且对包裹键的监视也已经结束
            List<Object> results = trans.exec();
            // 用户的包裹已经发生了变化，重试
            if (results == null) {
                continue;
            }
            return true;
        }
        return false;
    }

    /*购买商品*/
    public boolean purchaseItem(Jedis conn, String buyerId, String itemId, String sellerId, double lprice) {
        String buyer = "users:" + buyerId;
        String seller = "users:" + sellerId;
        String item = itemId + '.' + sellerId;
        String inventory = "inventory:" + buyerId;
        long end = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < end) {
            // 对商品买卖市场以及买家的个人信息进行监视
            conn.watch("market:", buyer);
            // 检查买家想要购买的商品的价格是否出现了变化，以及买家是否有足够的钱来购买这件商品
            double price = conn.zscore("market:", item);
            double funds = Double.parseDouble(conn.hget(buyer, "funds"));
            if (price != lprice || price > funds) {
                conn.unwatch();
                return false;
            }
            // 先将买家支付的钱转移给卖家，然后将被购买的商品移交给买家
            Transaction trans = conn.multi();
            trans.hincrBy(seller, "funds", (int) price);
            trans.hincrBy(buyer, "funds", (int) -price);
            trans.sadd(inventory, itemId);
            trans.zrem("market:", item);
            List<Object> results = trans.exec();
            // 如果买家的个人信息或者商品买卖市场在交易的过程中出现了变化，那么进行重试
            if (results == null) {
                continue;
            }
            return true;
        }
        return false;
    }

    /*性能测试*/
    public void benchmarkUpdateToken(Jedis conn, int duration) {
        try {
            @SuppressWarnings("rawtypes")
            Class[] args = new Class[]{Jedis.class, String.class, String.class, String.class};
            Method[] methods = new Method[]{this.getClass().getDeclaredMethod("updateToken", args), this.getClass().getDeclaredMethod("updateTokenPipeline", args),};
            // 测试会分别执行updateToken函数和updateTokenPipeline函数
            for (Method method : methods) {
                // 测试计数器以及测试结果的条件
                int count = 0;
                long start = System.currentTimeMillis();
                long end = start + (duration * 1000);
                while (System.currentTimeMillis() < end) {
                    count++;
                    // 调用两个函数的其中一个
                    method.invoke(this, conn, "token", "user", "item");
                }
                // 计算函数的执行时长
                long delta = System.currentTimeMillis() - start;
                // 打印测试结果
                System.out.println(method.getName() + ' ' + count + ' ' + (delta / 1000) + ' ' + (count / (delta / 1000)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*更新令牌*/
    public void updateToken(Jedis conn, String token, String user, String item) {
        // 获取时间戳
        long timestamp = System.currentTimeMillis() / 1000;
        // 创建令牌与已登录用户之间的映射
        conn.hset("login:", token, user);
        // 记录令牌最后一次出现时间
        conn.zadd("recent:", timestamp, token);
        if (item != null) {
            // 把用户浏览过的商品记录起来
            conn.zadd("viewed:" + token, timestamp, item);
            // 移除旧商品，只记录最新浏览的25件商品
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            // 更新给定商品的被浏览次数
            conn.zincrby("viewed:", -1, item);
        }
    }

    /*非事务型流水线*/
    public void updateTokenPipeline(Jedis conn, String token, String user, String item) {
        long timestamp = System.currentTimeMillis() / 1000;
        // 设置流水线
        Pipeline pipe = conn.pipelined();
        pipe.multi();
        pipe.hset("login:", token, user);
        pipe.zadd("recent:", timestamp, token);
        if (item != null) {
            pipe.zadd("viewed:" + token, timestamp, item);
            pipe.zremrangeByRank("viewed:" + token, 0, -26);
            pipe.zincrby("viewed:", -1, item);
        }
        // 执行那些被流水线包裹的命令
        pipe.exec();
    }
}