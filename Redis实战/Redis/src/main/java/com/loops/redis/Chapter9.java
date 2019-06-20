package com.loops.redis;

import org.javatuples.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ZParams;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;

/**
 * @author Loops
 */
public class Chapter9 {
    private static final String[] COUNTRIES = (
            "ABW AFG AGO AIA ALA ALB AND ARE ARG ARM ASM ATA ATF ATG AUS AUT AZE BDI " +
                    "BEL BEN BES BFA BGD BGR BHR BHS BIH BLM BLR BLZ BMU BOL BRA BRB BRN BTN " +
                    "BVT BWA CAF CAN CCK CHE CHL CHN CIV CMR COD COG COK COL COM CPV CRI CUB " +
                    "CUW CXR CYM CYP CZE DEU DJI DMA DNK DOM DZA ECU EGY ERI ESH ESP EST ETH " +
                    "FIN FJI FLK FRA FRO FSM GAB GBR GEO GGY GHA GIB GIN GLP GMB GNB GNQ GRC " +
                    "GRD GRL GTM GUF GUM GUY HKG HMD HND HRV HTI HUN IDN IMN IND IOT IRL IRN " +
                    "IRQ ISL ISR ITA JAM JEY JOR JPN KAZ KEN KGZ KHM KIR KNA KOR KWT LAO LBN " +
                    "LBR LBY LCA LIE LKA LSO LTU LUX LVA MAC MAF MAR MCO MDA MDG MDV MEX MHL " +
                    "MKD MLI MLT MMR MNE MNG MNP MOZ MRT MSR MTQ MUS MWI MYS MYT NAM NCL NER " +
                    "NFK NGA NIC NIU NLD NOR NPL NRU NZL OMN PAK PAN PCN PER PHL PLW PNG POL " +
                    "PRI PRK PRT PRY PSE PYF QAT REU ROU RUS RWA SAU SDN SEN SGP SGS SHN SJM " +
                    "SLB SLE SLV SMR SOM SPM SRB SSD STP SUR SVK SVN SWE SWZ SXM SYC SYR TCA " +
                    "TCD TGO THA TJK TKL TKM TLS TON TTO TUN TUR TUV TWN TZA UGA UKR UMI URY " +
                    // 一个由ISO3国家（或地区）编码组成的字符串表格，调用split()函数会根据空白对这个字符串进行分割，
                    // 并将它转换为一个由国家（或地区）编码组成的列表
                    "USA UZB VAT VCT VEN VGB VIR VNM VUT WLF WSM YEM ZAF ZMB ZWE").split(" ");
    private static final Map<String, String[]> STATES = new HashMap<String, String[]>();

    static {
        // 加拿大的省信息和属地信息
        STATES.put("CAN", "AB BC MB NB NL NS NT NU ON PE QC SK YT".split(" "));
        STATES.put("USA", (
                "AA AE AK AL AP AR AS AZ CA CO CT DC DE FL FM GA GU HI IA ID IL IN " +
                        "KS KY LA MA MD ME MH MI MN MO MP MS MT NC ND NE NH NJ NM NV NY OH " +
                        // 美国各个州的信息
                        "OK OR PA PR PW RI SC SD TN TX UT VA VI VT WA WI WV WY").split(" "));
    }

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00");

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /*性能测试*/
    // 为了以不同的方式进行性能测试，函数需要对所有测试指标进行参数化处理
    public double longZiplistPerformance(Jedis conn, String key, int length, int passes, int psize) {
        // 删除指定的键，确保被测试数据的准确性
        conn.del(key);
        for (int i = 0; i < length; i++) {
            // 通过从右端推入指定数量的元素来对列表进行初始化
            conn.rpush(key, String.valueOf(i));
        }
        // 通过流水线来降低网络通信给测试带来的影响
        Pipeline pipeline = conn.pipelined();
        // 启动计时器
        long time = System.currentTimeMillis();
        // 根据passes参数来决定流水线操作的执行次数
        for (int p = 0; p < passes; p++) {
            // 每个流水线操作都包含了psize次RPOPLPUSH命令调用
            for (int pi = 0; pi < psize; pi++) {
                // 每个rpoplpush()函数调用都会将列表最右端的元素弹出，并将它推入同一个列表的左端
                pipeline.rpoplpush(key, key);
            }
            // 执行psize次RPOPLPUSH命令
            pipeline.sync();
        }
        // 计算每秒执行的RPOPLPUSH调用数量
        return (passes * psize) / (System.currentTimeMillis() - time);
    }

    /**
     * 分片键
     *
     * @param base          基础散列的名字
     * @param key           将要被存储到分片散列里面的键
     * @param totalElements 预计的元素总数量
     * @param shardSize     请求的分片数量
     * @return
     */
    // 在调用shardKey()函数时，用户需要给定基础散列的名字、
    // 将要被存储到分片散列里面的键、预计的元素总数量以及请求的分片数量
    public String shardKey(String base, String key, long totalElements, int shardSize) {
        long shardId = 0;
        // 如果值是一个整数或者一个看上去像是整数的字符串，那么它将被直接用于计算分片ID
        if (isDigit(key)) {
            // 整数键将被程序假定为连续指派的ID，并基于这个整数ID的二进制位的高位来选择分片ID。
            // 此外，程序在进行整数转换的时候还使用了显示的基数（以及str()函数），使得键010可以被转换为10，而不是8
            shardId = Integer.parseInt(key, 10) / shardSize;
        } else {
            CRC32 crc = new CRC32();
            crc.update(key.getBytes());
            // 对于不是整数的键，程序将基于预计的元素总数量以及请求的分片数量，计算出实际所需的分片总数量
            long shards = 2 * totalElements / shardSize;
            // 在得知了分片的数量之后，程序就可以通过计算键的散列值与分片数量之间的模数来得到分片ID
            shardId = Math.abs(((int) crc.getValue()) % shards);
        }
        //最后，程序会把基础键和分片ID组合在一起，得出分片键
        return base + ':' + shardId;
    }

    /*分片式的HSET*/
    public Long shardHset(Jedis conn, String base, String key, String value, long totalElements, int shardSize) {
        // 计算出应该由哪个分片来存储值
        String shard = shardKey(base, key, totalElements, shardSize);
        // 将值存储到分片里面
        return conn.hset(shard, key, value);
    }

    /*分片式的HGET*/
    public String shardHget(Jedis conn, String base, String key, int totalElements, int shardSize) {
        // 计算出值可能被存储到了哪个分片里面
        String shard = shardKey(base, key, totalElements, shardSize);
        // 取得存储在分片里面的值
        return conn.hget(shard, key);
    }

    /*分片式SADD*/
    public Long shardSadd(Jedis conn, String base, String member, long totalElements, int shardSize) {
        // 计算成员应该被存储到哪个分片集合里面；因为成员并非连续ID，所以程序在计算成员所属的分片之前，会先将成员转换为字符串
        String shard = shardKey(base, "x" + member, totalElements, shardSize);
        // 将成员存储到分片里面
        return conn.sadd(shard, member);
    }

    // 为整数集合编码的集合预设一个典型的分片大小
    private int SHARD_SIZE = 512;

    /*记录每天唯一访客人数*/
    public void countVisit(Jedis conn, String sessionId) {
        // 取得当天的日期，并生成唯一访客计数器的键
        Calendar today = Calendar.getInstance();
        String key = "unique:" + ISO_FORMAT.format(today.getTime());
        // 获取或者计算当天的预计唯一访客人数
        long expected = getExpected(conn, key, today);
        // 根据128位的UUID，计算出一个56位的ID
        long id = Long.parseLong(sessionId.replace("-", "").substring(0, 15), 16);
        // 将ID添加到分片集合里面
        if (shardSadd(conn, key, String.valueOf(id), expected, SHARD_SIZE) != 0) {
            // 如果ID在分片集合里面并不存在，那么对唯一访客计数器执行加1操作
            conn.incr(key);
        }
    }

    // 这个初始的预计每日访客人数会设置得稍微比较高一些
    private long DAILY_EXPECTED = 1000000;
    // 在本地存储一份计算得出的预计访客人数副本
    private Map<String, Long> EXPECTED = new HashMap<String, Long>();

    /*计算预计唯一访客数量*/
    public long getExpected(Jedis conn, String key, Calendar today) {
        // 如果程序已经计算出或者获取到了当日的预计访客人数，那么直接使用已计算出的数字
        if (!EXPECTED.containsKey(key)) {
            String exkey = key + ":expected";
            // 如果其他客户端已经计算出了当日的预计访客人数，那么直接使用已计算出的数字
            String expectedStr = conn.get(exkey);
            long expected = 0;
            if (expectedStr == null) {
                // 获取昨天的唯一访客人数，如果该数值不存在就使用默认值100万
                Calendar yesterday = (Calendar) today.clone();
                yesterday.add(Calendar.DATE, -1);
                expectedStr = conn.get("unique:" + ISO_FORMAT.format(yesterday.getTime()));
                expected = expectedStr != null ? Long.parseLong(expectedStr) : DAILY_EXPECTED;
                // 基于“明天的访客人数至少会比今天的访客人数多50%”这一假设，
                // 给昨天的访客人数加上50%，然后向上舍入至下一个底数为2的幂
                expected = (long) Math.pow(2, (long) (Math.ceil(Math.log(expected * 1.5) / Math.log(2))));
                // 将计算出的预计访客人数写入Redis里面，以便其他程序在有需要时使用
                if (conn.setnx(exkey, String.valueOf(expected)) == 0) {
                    // 如果在我们之前，已经有其他客户端存储了当日的预计访客人数，那么直接使用已存储的数字
                    expectedStr = conn.get(exkey);
                    // 将当日的预计访客人数记录到本地副本里面，并将它返回给调用者
                    expected = Integer.parseInt(expectedStr);
                }
            } else {
                expected = Long.parseLong(expectedStr);
            }
            EXPECTED.put(key, expected);
        }
        return EXPECTED.get(key);
    }

    // 设置每个分片的大小
    private long USERS_PER_SHARD = (long) Math.pow(2, 20);

    /*对用户的位置信息进行更新*/
    public void setLocation(Jedis conn, long userId, String country, String state) {
        // 取得用户所在位置的编码
        String code = getCode(country, state);
        // 查找分片ID以及用户在指定分片中的位置
        long shardId = userId / USERS_PER_SHARD;
        int position = (int) (userId % USERS_PER_SHARD);
        // 计算用户数据的偏移量
        int offset = position * 2;
        Pipeline pipe = conn.pipelined();
        // 将用户的位置信息存储到经过分片处理的位置表格里面
        pipe.setrange("location:" + shardId, offset, code);
        // 对记录目前已知最大用户ID的有序集合进行更新
        String tkey = UUID.randomUUID().toString();
        pipe.zadd(tkey, userId, "max");
        pipe.zunionstore("location:max", new ZParams().aggregate(ZParams.Aggregate.MAX), tkey, "location:max");
        pipe.del(tkey);
        pipe.sync();
    }

    /*对所有用户的位置信息进行聚合计算*/
    public Pair<Map<String, Long>, Map<String, Map<String, Long>>> aggregateLocation(Jedis conn) {
        // 初始化两个特殊结构，以便快速地对已存在的计数器以及缺失的计数器进行更新
        Map<String, Long> countries = new HashMap<String, Long>();
        Map<String, Map<String, Long>> states = new HashMap<String, Map<String, Long>>();
        // 获取目前已知的最大用户ID，并使用它来计算出程序需要访问的最大分片ID
        long maxId = conn.zscore("location:max", "max").longValue();
        long maxBlock = maxId;
        byte[] buffer = new byte[(int) Math.pow(2, 17)];
        // 一个接一个地处理每个分片……
        for (int shardId = 0; shardId <= maxBlock; shardId++) {
            InputStream in = new RedisInputStream(conn, "location:" + shardId);
            try {
                int read = 0;
                // ……读取分片中的每个块
                while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                    // 从块里面提取出各个编码，并根据编码查找原始的位置信息，然后对这些位置信息进行聚合计算
                    for (int offset = 0; offset < read - 1; offset += 2) {
                        String code = new String(buffer, offset, 2);
                        // 对聚合数据进行更新
                        updateAggregates(countries, states, code);
                    }
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } finally {
                try {
                    in.close();
                } catch (Exception e) {

                }
            }
        }
        return new Pair<Map<String, Long>, Map<String, Map<String, Long>>>(countries, states);
    }

    /*位置信息聚合计算*/
    public Pair<Map<String, Long>, Map<String, Map<String, Long>>> aggregateLocationList(Jedis conn, long[] userIds) {
        // 和之前一样，设置好几本的聚合数据
        Map<String, Long> countries = new HashMap<String, Long>();
        Map<String, Map<String, Long>> states = new HashMap<String, Map<String, Long>>();
        // 设置流水线，减少操作执行过程中与Redis的通信往返次数
        Pipeline pipe = conn.pipelined();
        for (int i = 0; i < userIds.length; i++) {
            long userId = userIds[i];
            // 查找用户位置信息所在分片的ID，以及信息在分片中的偏移量
            long shardId = userId / USERS_PER_SHARD;
            int position = (int) (userId % USERS_PER_SHARD);
            int offset = position * 2;
            // 发送另一个被流水线包裹的命令，获取用户的位置信息
            pipe.substr("location:" + shardId, offset, offset + 1);
            // 每处理1000个请求，程序就会调用之前定义的辅助函数对聚合数据进行一次更新
            if ((i + 1) % 1000 == 0) {
                updateAggregates(countries, states, pipe.syncAndReturnAll());
            }
        }
        // 对遍历余下的最后一批用户进行处理
        updateAggregates(countries, states, pipe.syncAndReturnAll());
        // 返回聚合数据
        return new Pair<Map<String, Long>, Map<String, Map<String, Long>>>(countries, states);
    }

    public void updateAggregates(Map<String, Long> countries, Map<String, Map<String, Long>> states, List<Object> codes) {
        for (Object code : codes) {
            updateAggregates(countries, states, (String) code);
        }
    }

    /*聚合数据进行更新*/
    public void updateAggregates(Map<String, Long> countries, Map<String, Map<String, Long>> states, String code) {
        // 只对合法的编码进行查找
        if (code.length() != 2) {
            return;
        }
        // 计算出国家（或地区）和州在查找表格中的实际偏移量
        int countryIdx = (int) code.charAt(0) - 1;
        int stateIdx = (int) code.charAt(1) - 1;
        // 如果国家（或地区）所处的偏移量不在合法范围之内，那么跳过这个编码
        if (countryIdx < 0 || countryIdx >= COUNTRIES.length) {
            return;
        }
        // 获取ISO3国家（或地区）编码
        String country = COUNTRIES[countryIdx];
        Long countryAgg = countries.get(country);
        if (countryAgg == null) {
            countryAgg = Long.valueOf(0);
        }
        // 在对国家（或地区）信息进行编码之后，把用户计入这个国家对应的计数器里面
        countries.put(country, countryAgg + 1);
        // 如果程序没有找到指定的州信息，或者查找州信息时的偏移量不在合法的范围之内，那么跳过这个编码
        if (!STATES.containsKey(country)) {
            return;
        }
        if (stateIdx < 0 || stateIdx >= STATES.get(country).length) {
            return;
        }
        // 根据编码获取州名
        String state = STATES.get(country)[stateIdx];
        Map<String, Long> stateAggs = states.get(country);
        if (stateAggs == null) {
            stateAggs = new HashMap<String, Long>();
            states.put(country, stateAggs);
        }
        Long stateAgg = stateAggs.get(state);
        if (stateAgg == null) {
            stateAgg = Long.valueOf(0);
        }
        // 对州计时器执行加1操作
        stateAggs.put(state, stateAgg + 1);
    }

    /*转换编码*/
    public String getCode(String country, String state) {
        // 寻找国家（或地区）对应的偏移量
        int cindex = bisectLeft(COUNTRIES, country);
        // 没有找到指定的国家（或地区）时，将其索引设置为-1
        if (cindex > COUNTRIES.length || !country.equals(COUNTRIES[cindex])) {
            cindex = -1;
        }
        // 因为Redis里面的未初始化数据在返回时会被转换为空值，
        // 所以我们要将“未找到指定国家”时的返回值改为0，并将第一个国家（或地区）的索引变为1，
        // 以尝试取出国家（或地区）对应的州信息
        cindex++;
        int sindex = -1;
        if (state != null && STATES.containsKey(country)) {
            // 像处理“未找到指定国家”时的情况一样，处理“未找到指定州”的情况
            String[] states = STATES.get(country);
            // 寻找州对应的偏移量
            sindex = bisectLeft(states, state);
            if (sindex > states.length || !state.equals(states[sindex])) {
                sindex--;
            }
        }
        // chr()函数会将介于0至255之间的整数值转换为对应的ASCII字符
        sindex++;
        // 如果没有找到指定的州，那么索引为0，如果找到了指定的州，那么索引大于0
        return new String(new char[]{(char) cindex, (char) sindex});
    }

    private int bisectLeft(String[] values, String key) {
        int index = Arrays.binarySearch(values, key);
        return index < 0 ? Math.abs(index) - 1 : index;
    }

    private boolean isDigit(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public class RedisInputStream extends InputStream {
        private Jedis conn;
        private String key;
        private int pos;

        public RedisInputStream(Jedis conn, String key) {
            this.conn = conn;
            this.key = key;
        }

        @Override
        public int available() throws IOException {
            long len = conn.strlen(key);
            return (int) (len - pos);
        }

        @Override
        public int read() throws IOException {
            byte[] block = conn.substr(key.getBytes(), pos, pos);
            if (block == null || block.length == 0) {
                return -1;
            }
            pos++;
            return (int) (block[0] & 0xff);
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            byte[] block = conn.substr(key.getBytes(), pos, pos + (len - off - 1));
            if (block == null || block.length == 0) {
                return -1;
            }
            System.arraycopy(block, 0, buf, off, block.length);
            pos += block.length;
            return block.length;
        }

        @Override
        public void close() {

        }
    }
}