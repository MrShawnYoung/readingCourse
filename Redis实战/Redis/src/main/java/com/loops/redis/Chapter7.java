package com.loops.redis;

import org.javatuples.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Loops
 */
public class Chapter7 {
    // 用于查找需要的单词、不需要的单词以及同义词的正则表达式
    private static final Pattern QUERY_RE = Pattern.compile("[+-]?[a-z']{2,}");
    // 根据定义提取单词的正则表达式
    private static final Pattern WORDS_RE = Pattern.compile("[a-z']{2,}");
    private static final Set<String> STOP_WORDS = new HashSet<String>();

    static {
        // 预先定义好从http://www.textfixer.com/resources/获取的非用词
        for (String word :
                ("able about across after all almost also am among " +
                        "an and any are as at be because been but by can " +
                        "cannot could dear did do does either else ever " +
                        "every for from get got had has have he her hers " +
                        "him his how however if in into is it its just " +
                        "least let like likely may me might most must my " +
                        "neither no nor not of off often on only or other " +
                        "our own rather said say says she should since so " +
                        "some than that the their them then there these " +
                        "they this tis to too twas us wants was we were " +
                        "what when where which while who whom why will " +
                        "with would yet you your").split(" ")) {
            STOP_WORDS.add(word);
        }
    }

    /*标记化处理*/
    public Set<String> tokenize(String content) {
        // 将文档中包含的单词存储到Set集合里面
        Set<String> words = new HashSet<String>();
        Matcher matcher = WORDS_RE.matcher(content);
        // 遍历文档中包含的所有单词
        while (matcher.find()) {
            // 剔除所有位于单词前面或后面的单引号
            String word = matcher.group().trim();
            // 保留那些至少有两个字符长的单词
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                words.add(word);
            }
        }
        // 返回一个集合，集合里面包含了所有被保留的、不是非用词的单词
        return words;
    }

    /*创建索引*/
    public int indexDocument(Jedis conn, String docid, String content) {
        // 对内容进行标记化处理，并取得处理产生的单词
        Set<String> words = tokenize(content);
        Transaction trans = conn.multi();
        // 将文档添加到正确的反向索引集合里面
        for (String word : words) {
            trans.sadd("idx:" + word, docid);
        }
        // 计算一下，程序为这个文档添加了多少个独一无二的、不是非用词的单词
        return trans.exec().size();
    }

    /*对集合计算*/
    private String setCommon(Transaction trans, String method, int ttl, String... items) {
        String[] keys = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            keys[i] = "idx:" + items[i];
        }
        // 创建一个新的临时标识符
        String id = UUID.randomUUID().toString();
        try {
            trans.getClass()
                    // 给每个单词加上'idx:'前缀
                    .getDeclaredMethod(method, String.class, String[].class)
                    // 为将要执行的集合操作设置相应的参数
                    .invoke(trans, "idx:" + id, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 吩咐Redis在将来自动删除这个集合
        trans.expire("idx:" + id, ttl);
        // 将结果集合的ID返回给调用者，以便做进一步的处理
        return id;
    }

    /*执行交集计算*/
    public String intersect(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sinterstore", ttl, items);
    }

    /*执行并集计算*/
    public String union(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sunionstore", ttl, items);
    }

    /*执行差集计算*/
    public String difference(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sdiffstore", ttl, items);
    }

    /*对有序集合执行交集和并集计算*/
    private String zsetCommon(Transaction trans, String method, int ttl, ZParams params, String... sets) {
        String[] keys = new String[sets.length];
        // 给每个单词加上'idx:'前缀
        for (int i = 0; i < sets.length; i++) {
            keys[i] = "idx:" + sets[i];
        }
        // 创建一个新的临时标识符
        String id = UUID.randomUUID().toString();
        try {
            trans.getClass()
                    .getDeclaredMethod(method, String.class, ZParams.class, String[].class)
                    // 为将要执行的集合操作设置相应的参数
                    .invoke(trans, "idx:" + id, params, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 为计算结果有序集合设置过期时间
        trans.expire("idx:" + id, ttl);
        // 将计算结果的ID返回给调用者，以便做进一步的处理
        return id;
    }

    /*对有序集合执行交集计算*/
    public String zintersect(Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zinterstore", ttl, params, sets);
    }

    /*对有序集合执行并集计算*/
    public String zunion(Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zunionstore", ttl, params, sets);
    }

    /*搜索查询语句的语法分析函数*/
    public Query parse(String queryString) {
        Query query = new Query();
        // 这个集合将用于存储目前已经发现的同义词
        Set<String> current = new HashSet<String>();
        Matcher matcher = QUERY_RE.matcher(queryString.toLowerCase());
        // 遍历搜索查询语句中的所有单词
        while (matcher.find()) {
            // 检查单词是否带有加号前缀或者减号前缀，如果有的话
            String word = matcher.group().trim();
            char prefix = word.charAt(0);
            if (prefix == '+' || prefix == '-') {
                // 删除所有位于单词前面或者后面的单引号，并略过所有非用词
                word = word.substring(1);
            }
            if (word.length() < 2 || STOP_WORDS.contains(word)) {
                continue;
            }
            // 如果这是一个不需要的单词，那么将它添加到存储不需要单词的集合里面
            if (prefix == '-') {
                query.unwanted.add(word);
                continue;
            }
            // 如果在同义词集合非空的情况下，遇到了一个不带+号前缀的单词，那么创建一个新的同义词集合
            if (!current.isEmpty() && prefix != '+') {
                query.all.add(new ArrayList<String>(current));
                current.clear();
            }
            // 将正在处理的单词添加到同义词集合里面
            current.add(word);
        }
        // 把所有剩余的单词都放到最后的交集计算里面进行处理
        if (!current.isEmpty()) {
            query.all.add(new ArrayList<String>(current));
        }
        return query;
    }

    /*分析查询语句并搜索文档*/
    public String parseAndSearch(Jedis conn, String queryString, int ttl) {
        // 对查询语句进行语法分析
        Query query = parse(queryString);
        // 如果查询语句只包含非用词，那么这次搜索将没有任何结果
        if (query.all.isEmpty()) {
            return null;
        }
        List<String> toIntersect = new ArrayList<String>();
        // 遍历各个同义词列表
        for (List<String> syn : query.all) {
            // 如果同义词列表包含的单词不止一个，那么执行并集计算
            if (syn.size() > 1) {
                Transaction trans = conn.multi();
                toIntersect.add(union(trans, ttl, syn.toArray(new String[syn.size()])));
                trans.exec();
            } else {
                // 如果同义词列表只包含一次单词，那么直接使用这个单词
                toIntersect.add(syn.get(0));
            }
        }
        String intersectResult = null;
        // 如果单词（或者并集计算的结果）不止一个，那么执行交集计算
        if (toIntersect.size() > 1) {
            Transaction trans = conn.multi();
            intersectResult = intersect(trans, ttl, toIntersect.toArray(new String[toIntersect.size()]));
            trans.exec();
        } else {
            // 如果单词（或者并集计算的结果）只有一个，那么将它用作交集计算的结果
            intersectResult = toIntersect.get(0);
        }
        // 如果用户给定了不需要的单词，那么从交集计算结果里面移除包含这些单词的文档，然后返回搜索结果
        if (!query.unwanted.isEmpty()) {
            String[] keys = query.unwanted.toArray(new String[query.unwanted.size() + 1]);
            keys[keys.length - 1] = intersectResult;
            Transaction trans = conn.multi();
            intersectResult = difference(trans, ttl, keys);
            trans.exec();
        }
        // 如果用户没有给定不需要的单词，那么直接返回交集计算的结果作为搜索的结果
        return intersectResult;
    }

    @SuppressWarnings("unchecked")
    /*搜索并排序*/
    // 用户可以通过可选的参数来传入已有的搜索结果、指定搜索结果的排序方式，并对结果进行分页
    public SearchResult searchAndSort(Jedis conn, String queryString, String sort) {
        // 决定基于文档的哪个属性进行排序，以及是进行升序排序还是降序排序
        boolean desc = sort.startsWith("-");
        if (desc) {
            sort = sort.substring(1);
        }
        // 告知Redis，排序是以数值方式还是进行字母方式进行
        boolean alpha = !"updated".equals(sort) && !"id".equals(sort);
        // 如果用户给定了已有的搜索结果，并且这个结果仍然存在的话，那么延长它的生存时间
        String by = "kb:doc:*->" + sort;
        // 如果用户没有给定已有的搜索结果，或者给定的搜索结果已经过期，那么执行一次新的搜索操作
        String id = parseAndSearch(conn, queryString, 300);
        Transaction trans = conn.multi();
        // 获取结果集合的元素数量
        trans.scard("idx:" + id);
        SortingParams params = new SortingParams();
        if (desc) {
            params.desc();
        }
        if (alpha) {
            params.alpha();
        }
        params.by(by);
        params.limit(0, 20);
        // 根据指定属性对结果进行排序，并且只获取用户指定的那一部分结果
        trans.sort("idx:" + id, params);
        List<Object> results = trans.exec();
        // 返回搜索结果包含的元素数量、搜索结果本身以及搜索结果的ID，其中搜索结果的ID可以用于在之后再次获取本次搜索的结果
        return new SearchResult(id, ((Long) results.get(0)).longValue(), (List<String>) results.get(1));
    }

    @SuppressWarnings("unchecked")
    /*有序集合进行搜索和排序*/
    // 和之前一样，函数接受一个已有搜索结果的ID作为可选参数，以便在结果仍然可用的情况下，对其进行分页
    public SearchResult searchAndZsort(Jedis conn, String queryString, boolean desc, Map<String, Integer> weights) {
        int ttl = 300;
        int start = 0;
        int num = 20;
        // 如果传入的结果已经过期，或者这是函数第一次进行搜索，那么执行标准的集合搜索操作
        String id = parseAndSearch(conn, queryString, ttl);
        int updateWeight = weights.containsKey("update") ? weights.get("update") : 1;
        int voteWeight = weights.containsKey("vote") ? weights.get("vote") : 0;
        // 对文章评分进行调整以平衡更新时间和投票数量。根据待排序数据的需要，投票数量可以被调整为1、10、100，甚至更高
        String[] keys = new String[]{id, "sort:update", "sort:votes"};
        Transaction trans = conn.multi();
        // 使用辅助函数进行交集运算
        id = zintersect(trans, ttl, new ZParams().weights(0, updateWeight, voteWeight), keys);
        // 获取结果有序集合的大小
        trans.zcard("idx:" + id);
        // 从搜索结果里面取出一页（page）
        if (desc) {
            trans.zrevrange("idx:" + id, start, start + num - 1);
        } else {
            trans.zrange("idx:" + id, start, start + num - 1);
        }
        List<Object> results = trans.exec();
        // 返回搜索结果，以及分页用的ID值
        return new SearchResult(id, ((Long) results.get(results.size() - 2)).longValue(), new ArrayList<String>((Set<String>) results.get(results.size() - 1)));
    }

    /*将字符串转换为数字分值*/
    public long stringToScore(String string, boolean ignoreCase) {
        // 用户可以通过参数来决定是否以大小写无关的方式建立前缀索引
        if (ignoreCase) {
            string = string.toLowerCase();
        }
        // 将字符串的前6个字符转换为相应的数字值，比如把空字节转换为0、制表符（tab）转换为9、大写A转换为65，诸如此类
        List<Integer> pieces = new ArrayList<Integer>();
        for (int i = 0; i < Math.min(string.length(), 6); i++) {
            pieces.add((int) string.charAt(i));
        }
        // 为长度不足6个字符的字符串添加占位符，以此来表示这是一个短字符串
        while (pieces.size() < 6) {
            pieces.add(-1);
        }
        long score = 0;
        // 对字符串进行转换得出的每个值都会被计算到分值里面，并且程序会以不同的方式处理空字节和占位符
        for (int piece : pieces) {
            score = score * 257 + piece + 1;
        }
        // 通过多使用一个二进制位，程序可以表明字符串是否正好为6个字符长，
        // 这样它就可以区分出“robber”和“robbers”，尽管这对于区分“robber”和“robbers”并无帮助
        return score * 2 + (string.length() > 6 ? 1 : 0);
    }

    private Map<Ecpm, Double> AVERAGE_PER_1K = new HashMap<Ecpm, Double>();

    /*对广告进行索引*/
    public void indexAd(Jedis conn, String id, String[] locations, String content, Ecpm type, double value) {
        // 设置流水线，使得程序可以在一次通信往返里面完成整个索引操作
        Transaction trans = conn.multi();
        for (String location : locations) {
            // 为了进行定向操作，把广告ID添加到所有相关的位置集合里面
            trans.sadd("idx:req:" + location, id);
        }
        Set<String> words = tokenize(content);
        // 对广告包含的单词进行索引
        for (String word : tokenize(content)) {
            trans.zadd("idx:" + word, 0, id);
        }
        double avg = AVERAGE_PER_1K.containsKey(type) ? AVERAGE_PER_1K.get(type) : 1;
        // 为了评估新广告的效果，程序会使用字典来存储广告每1000次展示的平均点击次数或平均动作执行次数
        double rvalue = toEcpm(type, 1000, avg, value);
        // 记录这个广告的类型
        trans.hset("type:", id, type.name().toLowerCase());
        // 将广告的eCPM添加到一个记录了所有广告的eCPM的有序集合里面
        trans.zadd("idx:ad:value:", rvalue, id);
        // 将广告的基本价格（base value）添加到一个记录了所有广告的基本价格的有序集合里面
        trans.zadd("ad:base_value:", value, id);
        for (String word : words) {
            // 把能够对广告进行定向的单词全部记录起来
            trans.sadd("terms:" + id, word);
        }
        trans.exec();
    }

    public double toEcpm(Ecpm type, double views, double avg, double value) {
        switch (type) {
            case CPC:
            case CPA:
                return 1000. * value * avg / views;
            case CPM:
                return value;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    /*执行广告定向操作*/
    public Pair<Long, String> targetAds(Jedis conn, String[] locations, String content) {
        Transaction trans = conn.multi();
        // 根据用户传入的位置定向参数，找到所有匹配该位置的广告，以及这些广告的eCPM
        String matchedAds = matchLocation(trans, locations);
        String baseEcpm = zintersect(trans, 30, new ZParams().weights(0, 1), matchedAds, "ad:value:");
        // 基于匹配的内容计算附加值
        // 获取一个ID，它可以用于汇报并记录这个被定向的广告
        Pair<Set<String>, String> result = finishScoring(trans, matchedAds, baseEcpm, content);
        trans.incr("ads:served:");
        // 找到eCPM最高的广告，并获取这个广告的ID
        trans.zrevrange("idx:" + result.getValue1(), 0, 0);
        List<Object> response = trans.exec();
        long targetId = (Long) response.get(response.size() - 2);
        Set<String> targetedAds = (Set<String>) response.get(response.size() - 1);
        // 如果没有任何广告与目标位置相匹配，那么返回空值
        if (targetedAds.size() == 0) {
            return new Pair<Long, String>(null, null);
        }
        String adId = targetedAds.iterator().next();
        // 记录一系列定向操作的执行结果，作为学习用户行为的其中一个步骤
        recordTargetingResult(conn, targetId, adId, result.getValue0());
        // 向调用者返回记录本次定向操作相关信息的ID，以及被选中的广告的ID
        return new Pair<Long, String>(targetId, adId);
    }

    /*基于位置执行广告定向操作*/
    public String matchLocation(Transaction trans, String[] locations) {
        // 根据所有给定的位置，找出需要执行并集操作的集合键
        String[] required = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            required[i] = "req:" + locations[i];
        }
        // 找出与指定地区相匹配的广告，并将它们存储到集合里面
        // 找到存储着所有被匹配广告的集合，以及存储着所有被匹配广告的基本eCPM的有序集合，然后返回它们的ID
        return union(trans, 300, required);
    }

    /*计算包含了内容匹配附加值的广告eCPM*/
    public Pair<Set<String>, String> finishScoring(Transaction trans, String matched, String base, String content) {
        Map<String, Integer> bonusEcpm = new HashMap<String, Integer>();
        // 对内容进行标记化处理，以便与广告进行匹配
        Set<String> words = tokenize(content);
        for (String word : words) {
            // 找出那些既位于定向位置之内，又拥有页面内容其中一个单词的广告
            String wordBonus = zintersect(trans, 30, new ZParams().weights(0, 1), matched, word);
            bonusEcpm.put(wordBonus, 1);
        }
        if (bonusEcpm.size() > 0) {
            String[] keys = new String[bonusEcpm.size()];
            double[] weights = new double[bonusEcpm.size()];
            int index = 0;
            for (Map.Entry<String, Integer> bonus : bonusEcpm.entrySet()) {
                keys[index] = bonus.getKey();
                weights[index] = bonus.getValue();
                index++;
            }
            // 计算每个广告的最小eCPM附加值和最大eCPM附加值
            ZParams minParams = new ZParams().aggregate(ZParams.Aggregate.MIN).weights(weights);
            String minimum = zunion(trans, 30, minParams, keys);
            ZParams maxParams = new ZParams().aggregate(ZParams.Aggregate.MAX).weights(weights);
            String maximum = zunion(trans, 30, maxParams, keys);
            String result = zunion(trans, 30, new ZParams().weights(2, 1, 1), base, minimum, maximum);
            // 将广告的基本价格、最小eCPM附加值的一半以及最大eCPM附加值的一半这三者相加起来
            return new Pair<Set<String>, String>(words, result);
        }
        // 如果页面内容中没有出现任何可匹配的单词，那么返回广告的基本eCPM
        return new Pair<Set<String>, String>(words, base);
    }

    /*记录执行结果*/
    public void recordTargetingResult(Jedis conn, long targetId, String adId, Set<String> words) {
        // 找出内容与广告之间相匹配的那些单词
        Set<String> terms = conn.smembers("terms:" + adId);
        String type = conn.hget("type:", adId);
        Transaction trans = conn.multi();
        terms.addAll(words);
        if (terms.size() > 0) {
            String matchedKey = "terms:matched:" + targetId;
            // 如果有匹配的单词出现，就记录它们，并设置15分钟的生存时间
            for (String term : terms) {
                trans.sadd(matchedKey, term);
            }
            trans.expire(matchedKey, 900);
        }
        // 为每种类型的广告分别记录它们的展示次数
        trans.incr("type:" + type + ":views:");
        // 记录广告以及广告包含的单词的展示信息
        for (String term : terms) {
            trans.zincrby("views:" + adId, 1, term);
        }
        trans.zincrby("views:" + adId, 1, "");
        List<Object> response = trans.exec();
        double views = (Double) response.get(response.size() - 1);
        // 广告每展示100次就更新一次他的eCPM
        if ((views % 100) == 0) {
            updateCpms(conn, adId);
        }
    }

    @SuppressWarnings("unchecked")
    /*更新eCPM*/
    public void updateCpms(Jedis conn, String adId) {
        Transaction trans = conn.multi();
        // 获取广告的类型和价格，以及广告包含的所有单词
        trans.hget("type:", adId);
        trans.zscore("ad:base_value:", adId);
        trans.smembers("terms:" + adId);
        List<Object> response = trans.exec();
        String type = (String) response.get(0);
        Double baseValue = (Double) response.get(1);
        Set<String> words = (Set<String>) response.get(2);
        // 判断广告的eCPM应该基于点击次数进行计算还是基于动作执行次数进行计算
        String which = "clicks";
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
        if (Ecpm.CPA.equals(ecpm)) {
            which = "actions";
        }
        trans = conn.multi();
        trans.get("type:" + type + ":views:");
        trans.get("type:" + type + ':' + which);
        response = trans.exec();
        String typeViews = (String) response.get(0);
        String typeClicks = (String) response.get(1);
        // 将广告的点击率或动作执行率重新写入全局字典里面
        AVERAGE_PER_1K.put(ecpm, 1000. * Integer.valueOf(typeClicks != null ? typeClicks : "1") / Integer.valueOf(typeViews != null ? typeViews : "1"));
        // 如果正在处理的是一个CPM广告，那么它的eCPM已经更新完毕，无需再做其他处理
        if (Ecpm.CPM.equals(ecpm)) {
            return;
        }
        String viewKey = "views:" + adId;
        String clickKey = which + ':' + adId;
        trans = conn.multi();
        // 获取每个广告的展示次数和点击次数（或者动作执行次数）
        trans.zscore(viewKey, "");
        trans.zscore(clickKey, "");
        response = trans.exec();
        Double adViews = (Double) response.get(0);
        Double adClicks = (Double) response.get(1);
        double adEcpm = 0;
        // 如果广告还没有被点击过，那么使用已有的eCPM
        if (adClicks == null || adClicks < 1) {
            Double score = conn.zscore("idx:ad:value:", adId);
            adEcpm = score != null ? score.doubleValue() : 0;
        } else {
            // 计算广告的eCPM并更新它的价格
            adEcpm = toEcpm(ecpm, adViews != null ? adViews.doubleValue() : 1, adClicks != null ? adClicks.doubleValue() : 0, baseValue);
            conn.zadd("idx:ad:value:", adEcpm, adId);
        }
        for (String word : words) {
            trans = conn.multi();
            // 获取单词的展示次数和点击次数（或者动作执行次数）
            trans.zscore(viewKey, word);
            trans.zscore(clickKey, word);
            response = trans.exec();
            Double views = (Double) response.get(0);
            Double clicks = (Double) response.get(1);
            // 如果广告还未被点击过，那么不对eCPM进行更新
            if (clicks == null || clicks < 1) {
                continue;
            }
            // 计算单词的eCPM
            double wordEcpm = toEcpm(ecpm, views != null ? views.doubleValue() : 1, clicks != null ? clicks.doubleValue() : 0, baseValue);
            // 计算单词的附加值
            double bonus = wordEcpm - adEcpm;
            // 将单词的附加值重新写入为广告包含的每个单词分别记录附加值的有序集合里面
            conn.zadd("idx:" + word, bonus, adId);
        }
    }

    /*记录点击信息*/
    public void recordClick(Jedis conn, long targetId, String adId, boolean action) {
        String type = conn.hget("type:", adId);
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
        String clickKey = "clicks:" + adId;
        String matchKey = "terms:matched:" + targetId;
        Set<String> matched = conn.smembers(matchKey);
        matched.add("");
        Transaction trans = conn.multi();
        // 如果这是一个按动作计费的广告，并且被匹配的单词仍然存在，那么刷新这些单词的过期时间
        if (Ecpm.CPA.equals(ecpm)) {
            trans.expire(matchKey, 900);
            if (action) {
                // 记录动作信息，而不是点击信息
                clickKey = "actions:" + adId;
            }
        }
        if (action && Ecpm.CPA.equals(ecpm)) {
            trans.incr("type:" + type + ":actions:");
        } else {
            // 根据广告的类型，维持一个全局的点击/动作计数器
            trans.incr("type:" + type + ":clicks:");
        }
        // 为广告以及所有被定向至该广告的单词记录本次点击（或动作）
        for (String word : matched) {
            trans.zincrby(clickKey, 1, word);
        }
        trans.exec();
        // 对广告中出现的所有单词的eCPM进行更新
        updateCpms(conn, adId);
    }

    /*创建一个新的职位*/
    public void addJob(Jedis conn, String jobId, String... requiredSkills) {
        // 把职位所需的技能全部加到职位对应的集合里面
        conn.sadd("job:" + jobId, requiredSkills);
    }

    @SuppressWarnings("unchecked")
    /*检查一组给定的技能是否满足某个职位*/
    public boolean isQualified(Jedis conn, String jobId, String... candidateSkills) {
        String temp = UUID.randomUUID().toString();
        Transaction trans = conn.multi();
        // 把求职者拥有的技能全部添加到一个临时集合里面，并设置过期时间
        for (String skill : candidateSkills) {
            trans.sadd(temp, skill);
        }
        trans.expire(temp, 5);
        // 找出职位所需技能中，求职者不具备的那些技能，并将它们记录到结果集合里面
        trans.sdiff("job:" + jobId, temp);
        List<Object> response = trans.exec();
        Set<String> diff = (Set<String>) response.get(response.size() - 1);
        // 如果求职者具备职位所需的全部技能，那么返回True
        return diff.size() == 0;
    }

    /*对职位及其所需的技能进行索引*/
    public void indexJob(Jedis conn, String jobId, String... skills) {
        Transaction trans = conn.multi();
        Set<String> unique = new HashSet<String>();
        for (String skill : skills) {
            // 将职位ID添加到相应的技能集合面
            trans.sadd("idx:skill:" + skill, jobId);
            unique.add(skill);
        }
        // 将职位所需技能的数量添加到记录了所有职位所需技能数量的有序集合里面
        trans.zadd("idx:jobs:req", unique.size(), jobId);
        trans.exec();
    }

    /*找出求职者能够胜任的所有工作*/
    public Set<String> findJobs(Jedis conn, String... candidateSkills) {
        // 设置好用于计算职位得分的字典
        String[] keys = new String[candidateSkills.length];
        double[] weights = new double[candidateSkills.length];
        for (int i = 0; i < candidateSkills.length; i++) {
            keys[i] = "skill:" + candidateSkills[i];
            weights[i] = 1;
        }
        Transaction trans = conn.multi();
        // 计算求职者对于每个职位的得分
        String jobScores = zunion(trans, 30, new ZParams().weights(weights), keys);
        // 计算出求职者能够胜任以及不能够胜任的职位
        String finalResult = zintersect(trans, 30, new ZParams().weights(-1, 1), jobScores, "jobs:req");
        trans.exec();
        // 返回求职者能够胜任的那些职位
        return conn.zrangeByScore("idx:" + finalResult, 0, 0);
    }

    public class Query {
        // 这个列表将用于存储需要执行交集计算的单词
        public final List<List<String>> all = new ArrayList<List<String>>();
        // 这个集合将用于存储不需要的单词
        public final Set<String> unwanted = new HashSet<String>();
    }

    public class SearchResult {
        public final String id;
        public final long total;
        public final List<String> results;

        public SearchResult(String id, long total, List<String> results) {
            this.id = id;
            this.total = total;
            this.results = results;
        }
    }

    public enum Ecpm {
        CPC, CPA, CPM
    }
}