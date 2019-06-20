package com.loops.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Loops
 */
public class Chapter6 {
    /*更新最近联系人*/
    public void addUpdateContact(Jedis conn, String user, String contact) {
        String acList = "recent:" + user;
        // 准备执行原子操作
        Transaction trans = conn.multi();
        // 如果联系人已经存在，那么移除他
        trans.lrem(acList, 0, contact);
        // 将联系人推入列表的最前端
        trans.lpush(acList, contact);
        // 只保留列表里面的前100个联系人
        trans.ltrim(acList, 0, 99);
        // 实际地执行以上操作
        trans.exec();
    }

    /*移除联系人列表*/
    public void removeContact(Jedis conn, String user, String contact) {
        conn.lrem("recent:" + user, 0, contact);
    }

    /*最近联系人自动补全列表*/
    public List<String> fetchAutocompleteList(Jedis conn, String user, String prefix) {
        // 获取自动补全列表
        List<String> candidates = conn.lrange("recent:" + user, 0, -1);
        List<String> matches = new ArrayList<String>();
        // 检查每个候选联系人
        for (String candidate : candidates) {
            if (candidate.toLowerCase().startsWith(prefix)) {
                // 发现一个人匹配的联系人
                matches.add(candidate);
            }
        }
        // 返回所有的联系人
        return matches;
    }

    // 准备一个由已知字符组成的列表
    private static final String VALID_CHARACTERS = "`abcdefghijklmnopqrstuvwxyz{";

    /*给定前缀生成查找范围函数*/
    public String[] findPrefixRange(String prefix) {
        // 在字符列表中查找前缀字符所处的位置
        int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1));
        // 找到前驱字符
        char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0);
        String start = prefix.substring(0, prefix.length() - 1) + suffix + '{';
        String end = prefix + '{';
        // 返回范围
        return new String[]{start, end};
    }

    /*加入公会*/
    public void joinGuild(Jedis conn, String guild, String user) {
        conn.zadd("members:" + guild, 0, user);
    }

    /*离开公会*/
    public void leaveGuild(Jedis conn, String guild, String user) {
        conn.zrem("members:" + guild, user);
    }

    @SuppressWarnings("unchecked")
    /*自动补全*/
    public Set<String> autocompleteOnPrefix(Jedis conn, String guild, String prefix) {
        // 根据给定的前缀计算出查找范围的起点和终点
        String[] range = findPrefixRange(prefix);
        String start = range[0];
        String end = range[1];
        String identifier = UUID.randomUUID().toString();
        start += identifier;
        end += identifier;
        String zsetName = "members:" + guild;
        // 将范围的起始元素和结束元素添加到有序集合里面
        conn.zadd(zsetName, 0, start);
        conn.zadd(zsetName, 0, end);
        Set<String> items = null;
        while (true) {
            conn.watch(zsetName);
            // 找到两个被插入元素在有序集合中的排名
            int sindex = conn.zrank(zsetName, start).intValue();
            int eindex = conn.zrank(zsetName, end).intValue();
            int erange = Math.min(sindex + 9, eindex - 2);
            Transaction trans = conn.multi();
            // 获取范围内的值，然后删除之前插入的起始元素和结束元素
            trans.zrem(zsetName, start);
            trans.zrem(zsetName, end);
            trans.zrange(zsetName, sindex, erange);
            List<Object> results = trans.exec();
            if (results != null) {
                items = (Set<String>) results.get(results.size() - 1);
                break;
            }
        }
        for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ) {
            if (iterator.next().indexOf('{') != -1) {
                iterator.remove();
            }
        }
        // 如果有其他自动补全操作正在执行，那么从获取到的元素里移除起始元素和结束元素
        return items;
    }

    public String acquireLock(Jedis conn, String lockName) {
        return acquireLock(conn, lockName, 10000);
    }

    /*Redis锁*/
    public String acquireLock(Jedis conn, String lockName, long acquireTimeout) {
        // 128位随机标识符
        String identifier = UUID.randomUUID().toString();
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            // 尝试取得锁
            if (conn.setnx("lock:" + lockName, identifier) == 1) {
                return identifier;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    /*锁超时*/
    public String acquireLockWithTimeout(Jedis conn, String lockName, long acquireTimeout, long lockTimeout) {
        // 128位随机标识符
        String identifier = UUID.randomUUID().toString();
        String lockKey = "lock:" + lockName;
        // 确保传给EXPIRE的都是整数
        int lockExpire = (int) (lockTimeout / 1000);
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            // 获取锁并设置过期时间
            if (conn.setnx(lockKey, identifier) == 1) {
                conn.expire(lockKey, lockExpire);
                return identifier;
            }
            // 检查过期时间，并在有需要时对其进行更新
            if (conn.ttl(lockKey) == -1) {
                conn.expire(lockKey, lockExpire);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    /*锁释放*/
    public boolean releaseLock(Jedis conn, String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        while (true) {
            // 检查进程是否仍然持有锁
            conn.watch(lockKey);
            if (identifier.equals(conn.get(lockKey))) {
                // 释放锁
                Transaction trans = conn.multi();
                trans.del(lockKey);
                List<Object> results = trans.exec();
                if (results == null) {
                    continue;
                }
                return true;
            }
            conn.unwatch();
            break;
        }
        // 进程已经失去了锁
        return false;
    }

    /*公平信号量获取*/
    public String acquireFairSemaphore(Jedis conn, String semname, int limit, long timeout) {
        // 128位随机标识符
        String identifier = UUID.randomUUID().toString();
        String czset = semname + ":owner";
        String ctr = semname + ":counter";
        long now = System.currentTimeMillis();
        Transaction trans = conn.multi();
        // 清理过期的信号量持有者
        // 删除超时的信号量
        trans.zremrangeByScore(semname.getBytes(), "-inf".getBytes(), String.valueOf(now - timeout).getBytes());
        ZParams params = new ZParams();
        params.weights(1, 0);
        trans.zinterstore(czset, params, czset, semname);
        // 对计数器执行自增操作，并获取计数器在执行自增操作之后的值
        trans.incr(ctr);
        List<Object> results = trans.exec();
        int counter = ((Long) results.get(results.size() - 1)).intValue();
        trans = conn.multi();
        // 尝试获取信号量
        trans.zadd(semname, now, identifier);
        trans.zadd(czset, counter, identifier);
        // 检查是否成功取得了信号量
        // 通过检查排名来判断客户端是否取得了信号量
        trans.zrank(czset, identifier);
        results = trans.exec();
        int result = ((Long) results.get(results.size() - 1)).intValue();
        if (result < limit) {
            // 客户端成功取得了信号量
            return identifier;
        }
        trans = conn.multi();
        // 获取信号量失败，删除之前添加的标识符
        // 客户端未能取得信号量，清理无用数据
        trans.zrem(semname, identifier);
        trans.zrem(czset, identifier);
        trans.exec();
        return null;
    }

    /*公平信号量释放*/
    public boolean releaseFairSemaphore(Jedis conn, String semname, String identifier) {
        Transaction trans = conn.multi();
        // 如果信号量已经被正确地释放，那么返回True；返回False则表示该信号量已经因为过期而被删除了
        trans.zrem(semname, identifier);
        trans.zrem(semname + ":owner", identifier);
        List<Object> results = trans.exec();
        // 返回True表示信号量已被正确地释放，返回False则表示想要释放地信号量已经因为超时而被删除了
        return (Long) results.get(results.size() - 1) == 1;
    }

    /*创建延迟任务*/
    public String executeLater(Jedis conn, String queue, String name, List<String> args, long delay) {
        Gson gson = new Gson();
        // 生成唯一标识符
        String identifier = UUID.randomUUID().toString();
        String itemArgs = gson.toJson(args);
        // 准备好需要入队的任务
        String item = gson.toJson(new String[]{identifier, queue, name, itemArgs});
        if (delay > 0) {
            // 延迟执行这个任务
            conn.zadd("delayed:", System.currentTimeMillis() + delay, item);
        } else {
            // 立即执行这个任务
            conn.rpush("queue:" + queue, item);
        }
        // 返回标识符
        return identifier;
    }

    public String createChat(Jedis conn, String sender, Set<String> recipients, String message) {
        // 获取新的群组ID
        String chatId = String.valueOf(conn.incr("ids:chat:"));
        return createChat(conn, sender, recipients, message, chatId);
    }

    /*创建新群组*/
    public String createChat(Jedis conn, String sender, Set<String> recipients, String message, String chatId) {
        // 创建一个由用户和分值组成的字典，字典里面的信息将被添加到有序集合里面
        recipients.add(sender);
        Transaction trans = conn.multi();
        for (String recipient : recipients) {
            // 将所有参与群聊的用户添加到有序集合里面
            trans.zadd("chat:" + chatId, 0, recipient);
            // 初始化已读有序集合
            trans.zadd("seen:" + recipient, 0, chatId);
        }
        trans.exec();
        // 发送消息
        return sendMessage(conn, chatId, sender, message);
    }

    /*发送消息*/
    public String sendMessage(Jedis conn, String chatId, String sender, String message) {
        String identifier = acquireLock(conn, "chat:" + chatId);
        if (identifier == null) {
            throw new RuntimeException("Couldn't get the lock");
        }
        try {
            // 筹备待发送的消息
            long messageId = conn.incr("ids:" + chatId);
            HashMap<String, Object> values = new HashMap<String, Object>();
            values.put("id", messageId);
            values.put("ts", System.currentTimeMillis());
            values.put("sender", sender);
            values.put("message", message);
            String packed = new Gson().toJson(values);
            // 将消息发送至群组
            conn.zadd("msgs:" + chatId, messageId, packed);
        } finally {
            releaseLock(conn, "chat:" + chatId, identifier);
        }
        return chatId;
    }

    @SuppressWarnings("unchecked")
    /*消息获取*/
    public List<ChatMessages> fetchPendingMessages(Jedis conn, String recipient) {
        // 获取最后接收到的消息的ID
        Set<Tuple> seenSet = conn.zrangeWithScores("seen:" + recipient, 0, -1);
        List<Tuple> seenList = new ArrayList<Tuple>(seenSet);
        Transaction trans = conn.multi();
        // 获取所有未读消息
        for (Tuple tuple : seenList) {
            String chatId = tuple.getElement();
            int seenId = (int) tuple.getScore();
            trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId + 1), "inf");
        }
        List<Object> results = trans.exec();
        Gson gson = new Gson();
        Iterator<Tuple> seenIterator = seenList.iterator();
        Iterator<Object> resultsIterator = results.iterator();
        List<ChatMessages> chatMessages = new ArrayList<ChatMessages>();
        List<Object[]> seenUpdates = new ArrayList<Object[]>();
        List<Object[]> msgRemoves = new ArrayList<Object[]>();
        while (seenIterator.hasNext()) {
            Tuple seen = seenIterator.next();
            Set<String> messageStrings = (Set<String>) resultsIterator.next();
            if (messageStrings.size() == 0) {
                continue;
            }
            int seenId = 0;
            String chatId = seen.getElement();
            List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
            for (String messageJson : messageStrings) {
                Map<String, Object> message = (Map<String, Object>) gson.fromJson(messageJson, new TypeToken<Map<String, Object>>() {
                }.getType());
                int messageId = ((Double) message.get("id")).intValue();
                if (messageId > seenId) {
                    seenId = messageId;
                }
                message.put("id", messageId);
                messages.add(message);
            }
            // 使用最新收到的消息来更新群组有序集合
            conn.zadd("chat:" + chatId, seenId, recipient);
            // 更新已读消息有序集合
            seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});
            // 找出那些所有人都已经阅读过的消息
            Set<Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
            if (minIdSet.size() > 0) {
                // 清除那些已经被所有人阅读过的消息
                msgRemoves.add(new Object[]{"msgs:" + chatId, minIdSet.iterator().next().getScore()});
            }
            chatMessages.add(new ChatMessages(chatId, messages));
        }
        trans = conn.multi();
        for (Object[] seenUpdate : seenUpdates) {
            trans.zadd((String) seenUpdate[0], (Integer) seenUpdate[1], (String) seenUpdate[2]);
        }
        for (Object[] msgRemove : msgRemoves) {
            trans.zremrangeByScore((String) msgRemove[0], 0, ((Double) msgRemove[1]).intValue());
        }
        trans.exec();
        return chatMessages;
    }

    /*接收日志文件*/
    public void processLogsFromRedis(Jedis conn, String id, Callback callback) throws InterruptedException, IOException {
        while (true) {
            // 获取文件列表
            List<ChatMessages> fdata = fetchPendingMessages(conn, id);
            for (ChatMessages messages : fdata) {
                for (Map<String, Object> message : messages.messages) {
                    String logFile = (String) message.get("message");
                    // 所有日志行已经处理完毕
                    if (":done".equals(logFile)) {
                        return;
                    }
                    if (logFile == null || logFile.length() == 0) {
                        continue;
                    }
                    // 选择一个块读取器（block reader）
                    InputStream in = new RedisInputStream(conn, messages.chatId + logFile);
                    if (logFile.endsWith(".gz")) {
                        in = new GZIPInputStream(in);
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    try {
                        String line = null;
                        // 遍历日志行
                        while ((line = reader.readLine()) != null) {
                            // 将日志行传递给回调函数
                            callback.callback(line);
                        }
                        // 强制刷新聚合数据缓存
                        callback.callback(null);
                    } finally {
                        reader.close();
                    }
                    // 日志已经处理完毕，向文件发送者报告这一信息
                    conn.incr(messages.chatId + logFile + ":done");
                }
            }
            if (fdata.size() == 0) {
                Thread.sleep(100);
            }
        }
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

    public interface Callback {
        void callback(String line);
    }

    public class ChatMessages {
        public String chatId;
        public List<Map<String, Object>> messages;

        public ChatMessages(String chatId, List<Map<String, Object>> messages) {
            this.chatId = chatId;
            this.messages = messages;
        }

        public boolean equals(Object other) {
            if (!(other instanceof ChatMessages)) {
                return false;
            }
            ChatMessages otherCm = (ChatMessages) other;
            return chatId.equals(otherCm.chatId) && messages.equals(otherCm.messages);
        }
    }

    /*可执行任务*/
    public class PollQueueThread extends Thread {
        private Jedis conn;
        private boolean quit;
        private Gson gson = new Gson();

        public PollQueueThread() {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                // 获取队列中的第一个任务
                Set<Tuple> items = conn.zrangeWithScores("delayed:", 0, 0);
                Tuple item = items.size() > 0 ? items.iterator().next() : null;
                // 队列没有包含任何任务，或者任务的执行时间未到
                if (item == null || item.getScore() > System.currentTimeMillis()) {
                    try {
                        sleep(10);
                    } catch (InterruptedException ie) {
                        Thread.interrupted();
                    }
                    continue;
                }
                // 解码要被执行的任务，弄清楚它应该被推入哪个任务队列里面
                String json = item.getElement();
                String[] values = gson.fromJson(json, String[].class);
                String identifier = values[0];
                String queue = values[1];
                // 为了对任务进行移动，尝试获取锁
                String locked = acquireLock(conn, identifier);
                // 获取锁失败，跳过后续步骤并重试
                if (locked == null) {
                    continue;
                }
                // 将任务推入适当的任务队列里面
                if (conn.zrem("delayed:", json) == 1) {
                    conn.rpush("queue:" + queue, json);
                }
                // 释放锁
                releaseLock(conn, identifier, locked);
            }
        }
    }

    /*复制日志文件*/
    public class CopyLogsThread extends Thread {
        private Jedis conn;
        private File path;
        private String channel;
        private int count;
        private long limit;

        public CopyLogsThread(File path, String channel, int count, long limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.path = path;
            this.channel = channel;
            this.count = count;
            this.limit = limit;
        }

        public void run() {
            //这个类接口已过时
            Deque<File> waiting = new ArrayDeque<File>();
            long bytesInRedis = 0;
            Set<String> recipients = new HashSet<String>();
            for (int i = 0; i < count; i++) {
                recipients.add(String.valueOf(i));
            }
            // 创建用于向客户端发送消息的群组
            createChat(conn, "source", recipients, "", channel);
            File[] logFiles = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("temp_redis");
                }
            });
            Arrays.sort(logFiles);
            // 遍历所有日志文件
            for (File logFile : logFiles) {
                long fsize = logFile.length();
                // 如果程序需要更多空间，那么清除已经处理完毕的文件
                while ((bytesInRedis + fsize) > limit) {
                    long cleaned = clean(waiting, count);
                    if (cleaned != 0) {
                        bytesInRedis -= cleaned;
                    } else {
                        try {
                            sleep(250);
                        } catch (InterruptedException ie) {
                            Thread.interrupted();
                        }
                    }
                }
                BufferedInputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(logFile));
                    int read = 0;
                    byte[] buffer = new byte[8192];
                    // 将文件上传至Redis
                    while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                        if (buffer.length != read) {
                            byte[] bytes = new byte[read];
                            System.arraycopy(buffer, 0, bytes, 0, read);
                            conn.append((channel + logFile).getBytes(), bytes);
                        } else {
                            conn.append((channel + logFile).getBytes(), buffer);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new RuntimeException(ioe);
                } finally {
                    try {
                        in.close();
                    } catch (Exception ignore) {
                    }
                }
                // 提醒监听者，文件已经准备就绪
                sendMessage(conn, channel, "source", logFile.toString());
                // 对本地记录的Redis内存占用量相关信息进行更新
                bytesInRedis += fsize;
                waiting.addLast(logFile);
            }
            // 所有日志文件已经处理完毕，向监听者报告此事
            sendMessage(conn, channel, "source", ":done");
            // 在工作完成之后，清理无用的日志文件
            while (waiting.size() > 0) {
                long cleaned = clean(waiting, count);
                if (cleaned != 0) {
                    bytesInRedis -= cleaned;
                } else {
                    try {
                        sleep(250);
                    } catch (InterruptedException ie) {
                        Thread.interrupted();
                    }
                }
            }
        }

        /*对Redis进行清理的详细步骤*/
        private long clean(Deque<File> waiting, int count) {
            if (waiting.size() == 0) {
                return 0;
            }
            File w0 = waiting.getFirst();
            if (String.valueOf(count).equals(conn.get(channel + w0 + ":done"))) {
                conn.del(channel + w0, channel + w0 + ":done");
                return waiting.removeFirst().length();
            }
            return 0;
        }
    }
}