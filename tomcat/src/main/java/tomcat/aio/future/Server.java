package tomcat.aio.future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {

	private ExecutorService taskExecutor;
	private AsynchronousServerSocketChannel serverChannel;
	
	/**
	 * 内部类，类比于子线程
	 * @author TD
	 *
	 */
	class Worker implements Callable<String>{
		private CharBuffer charBuffer;
		private CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
		private AsynchronousSocketChannel channel;
		public Worker(AsynchronousSocketChannel channel) {
			this.channel = channel;
		}
		@Override
		public String call() throws Exception {
			final ByteBuffer buffer = ByteBuffer.allocate(1024);
			//while循环读取请求
			while(channel.read(buffer).get() != -1) {
				//在写模式下调用flip()之后，Buffer从写模式变成读模式。      
				buffer.flip();
				charBuffer = decoder.decode(buffer);
				String msg = charBuffer.toString().trim();
				System.out.println("【server】客户端请求的信息为："+msg);
				ByteBuffer outBuffer = ByteBuffer.wrap(("【server】已收到请求"+msg).getBytes());
				channel.write(outBuffer).get();
				if(buffer.hasRemaining()) {
					//将没有发出的数据复制到 buffer的开始位置，下次继续发送
					buffer.compact();
				}else {
					buffer.clear();
				}
			}
			channel.close();
			return null;
		}
		
	};
	
	public void init() throws IOException {
		//创建ExecutorService
		taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
		serverChannel = AsynchronousServerSocketChannel.open();
		if(serverChannel.isOpen()) {
			serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
			serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			serverChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
		}else {
			throw new RuntimeException("通道未打开");
		}
	}
	
	public void start() {
		System.out.println("【server】等待客户端请求...");
		while(true) {
			Future<AsynchronousSocketChannel> future = serverChannel.accept();
			try {
				AsynchronousSocketChannel channel = future.get();
				//提交到线程池处理
				taskExecutor.submit(new Worker(channel));
			} catch (Exception e) {
				System.err.println("服务器关闭");
				taskExecutor.shutdown();
				while(!taskExecutor.isTerminated()) {
				}
				break;
			}
		}
	}

	
	public static void main(String[] args) throws IOException {
		Server serve = new Server();
		serve.init();
		serve.start();
	}
	
}
