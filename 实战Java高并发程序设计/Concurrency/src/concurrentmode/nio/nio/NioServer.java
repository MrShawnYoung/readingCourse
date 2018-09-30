package concurrentmode.nio.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NIO服务端
 * 
 * @author 杨弢
 * 
 */
public class NioServer {
	// 选择器
	private Selector selector;
	// 线程池
	private ExecutorService tp = Executors.newCachedThreadPool();
	// 给定大小的map
	public static Map<Socket, Long> time_stat = new HashMap<Socket, Long>(10240);

	public void startServer() throws Exception {
		// 由SelectorProvider返回一个创建者，并打开一个选择器
		selector = SelectorProvider.provider().openSelector();
		// 打开套接字通道
		ServerSocketChannel ssc = ServerSocketChannel.open();
		// 如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
		ssc.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(8000);
		// 获取与此通道关联的服务器套接字。将 ServerSocket绑定到特定地址（IP地址和端口号）
		ssc.socket().bind(isa);
		// 将通道注册到选择器
		SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		for (;;) {
			selector.select();// 消费阻塞
			Set readyKeys = selector.selectedKeys();// 获取已经准备好的keys
			Iterator i = readyKeys.iterator();// 迭代
			long e = 0;
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey) i.next();
				i.remove();// 必须消除，防止重复消费
				if (sk.isAcceptable()) {
					doAccept(sk);// 如果为接受状态，接受
				} else if (sk.isValid() && sk.isReadable()) {// 如果是可读
					if (!time_stat.containsKey(((SocketChannel) sk.channel()).socket())) {
						// 将socket方法如map
						time_stat.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());// 增加一个时间戳
						// 读取
						doRead(sk);
					}
				} else if (sk.isValid() && sk.isWritable()) {
					// 写
					doWrite(sk);
					e = System.currentTimeMillis();
					long b = time_stat.remove(((SocketChannel) sk.channel()).socket());
					System.out.println("spend:" + (e - b) + "ms");// 输入处理写入耗时
				}
			}
		}
	}

	/**
	 * 与客户端建立连接
	 * 
	 * @param sk
	 */
	private void doAccept(SelectionKey sk) {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel clientChannel;
		try {
			clientChannel = server.accept();// 生成一个channel表示与客户端通信
			clientChannel.configureBlocking(false);// 非阻塞模式
			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			EchoClient echoClient = new EchoClient();// 回复给客户端口的全部信息
			clientKey.attach(echoClient);// 附加实例，整个连接共享实例
			InetAddress clientAddress = clientChannel.socket().getInetAddress();
			System.out.println("Accepted connetion from " + clientAddress.getHostAddress() + ".");
		} catch (Exception e) {
			System.out.println("Failed to accept new client.");
			e.printStackTrace();
		}
	}

	/**
	 * 执行读的操作
	 * 
	 * @param sk
	 */
	private void doRead(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();
		ByteBuffer bb = ByteBuffer.allocate(8192);
		int len;
		try {
			len = channel.read(bb);
			if (len < 0) {
				// 这里如果不注释掉，读取不到之后直接关闭，在此读取就拋异常
				// disconnect(sk);
				return;
			}
		} catch (Exception e) {
			System.out.println("Failed to read from client.");
			e.printStackTrace();
			disconnect(sk);
			return;
		}
		bb.flip();
		tp.execute(new HandleMsg(sk, bb));
	}

	/**
	 * 执行写的操作
	 * 
	 * @param sk
	 */
	private void doWrite(SelectionKey sk) {
		SocketChannel channel = (SocketChannel) sk.channel();
		EchoClient echoClient = (EchoClient) sk.attachment();
		LinkedList<ByteBuffer> outq = echoClient.getOutputQueue();
		ByteBuffer bb = outq.getLast();
		try {
			int len = channel.write(bb);
			if (len == -1) {
				disconnect(sk);
				return;
			}
			if (bb.remaining() == 0) {
				// 已经完成
				outq.removeLast();
			}
		} catch (Exception e) {
			System.out.println("Failed to write to client.");
			e.printStackTrace();
			disconnect(sk);
		}
		if (outq.size() == 0) {// 很重要
			sk.interestOps(SelectionKey.OP_READ);
		}
	}

	private void disconnect(SelectionKey sk) {
		try {
			SocketChannel channel = (SocketChannel) sk.channel();
			channel.close();
			// sk.cancel();
			sk.selector().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class EchoClient {
		private LinkedList<ByteBuffer> outq;

		EchoClient() {
			outq = new LinkedList<ByteBuffer>();
		}

		public LinkedList<ByteBuffer> getOutputQueue() {
			return outq;
		}

		public void enqueue(ByteBuffer bb) {
			outq.addFirst(bb);
		}
	}

	// 将接受的数据压入EchoClient,需要处理业务在这里处理,处理完成之后重新注册事件op_write
	class HandleMsg implements Runnable {
		SelectionKey sk;
		ByteBuffer bb;

		public HandleMsg(SelectionKey sk, ByteBuffer bb) {
			this.sk = sk;
			this.bb = bb;
		}

		@Override
		public void run() {
			EchoClient echoClient = (EchoClient) sk.attachment();
			echoClient.enqueue(bb);
			sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			// 强迫selector立即返回
			selector.wakeup();
		}
	}

	public static void main(String[] args) throws Exception {
		NioServer nioServer = new NioServer();
		nioServer.startServer();
	}
}