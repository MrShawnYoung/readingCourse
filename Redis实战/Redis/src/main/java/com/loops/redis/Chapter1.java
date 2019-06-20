package com.loops.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.*;

/**
 * @author Loops
 */
public class Chapter1 {
    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432;
    private static final int ARTICLES_PER_PAGE = 25;

    /*发布新文章*/
    public String postArticle(Jedis conn, String user, String title, String link) {
        // 生成一个新的文章ID
        String articleId = String.valueOf(conn.incr("article:"));
        String voted = "voted:" + articleId;
        // 将发布文章的用户添加到文章的已投票用户名单里面，然后将这个名单的过期时间设置为一周
        conn.sadd(voted, user);
        conn.expire(voted, ONE_WEEK_IN_SECONDS);
        long now = System.currentTimeMillis() / 1000;
        String article = "article:" + articleId;
        HashMap<String, String> articleData = new HashMap<String, String>();
        articleData.put("title", title);
        articleData.put("link", link);
        articleData.put("user", user);
        articleData.put("now", String.valueOf(now));
        articleData.put("votes", "1");
        // 将文章信息存储到一个散列里面
        conn.hmset(article, articleData);
        // 将文章添加到根据发布时间排序的有序集合和根据评分排序的有序集合里面
        conn.zadd("score:", now + VOTE_SCORE, article);
        conn.zadd("time:", now, article);
        return articleId;
    }

    /*投票*/
    public void articleVote(Jedis conn, String user, String article) {
        // 计算文章的投票截止时间
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        // 检查是否可以对文章进行投票
        if (conn.zscore("time:", article) < cutoff) {
            return;
        }
        // 取出文章ID
        String articleId = article.substring(article.indexOf(':') + 1);
        if (conn.sadd("voted:" + articleId, user) == 1) {
            conn.zincrby("score:", VOTE_SCORE, article);
            conn.hincrBy(article, "votes", 1);
        }
    }

    /*文章获取*/
    public List<Map<String, String>> getArticles(Jedis conn, int page, String order) {
        // 设置获取文章的起始索引和结束索引
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;
        // 获取多个文章ID
        Set<String> ids = conn.zrevrange(order, start, end);
        List<Map<String, String>> articles = new ArrayList<Map<String, String>>();
        // 根据文章ID获取文章的详细信息
        for (String id : ids) {
            Map<String, String> articleData = conn.hgetAll(id);
            articleData.put("id", id);
            articles.add(articleData);
        }
        return articles;
    }

    /*将文章添加到群组*/
    public void addGroups(Jedis conn, String articleId, String[] toAdd) {
        // 构建存储文章的键名
        String article = "article:" + articleId;
        for (String group : toAdd) {
            // 将文章添加到它所属的群组里面
            conn.sadd("group:" + group, article);
        }
    }

    /*获取一整页文章*/
    public List<Map<String, String>> getGroupArticles(Jedis conn, String group, int page, String order) {
        // 为每个群组的每种排列顺序都创建一个键
        String key = order + group;
        // 检查是否有已缓存的排序结果，如果没有的话就现在进行排序
        if (!conn.exists(key)) {
            ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
            // 根据评分或者发布时间，对群组文章进行排序
            conn.zinterstore(key, params, "group:" + group, order);
            // 让Redis在60秒之后自动删除这个有序集合
            conn.expire(key, 60);
        }
        // 调用之前定义的getArticles()函数来进行分页并获取文章数据
        return getArticles(conn, page, key);
    }
}