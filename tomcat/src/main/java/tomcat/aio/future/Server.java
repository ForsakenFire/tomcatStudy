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
	 * �ڲ��࣬��������߳�
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
			//whileѭ����ȡ����
			while(channel.read(buffer).get() != -1) {
				//��дģʽ�µ���flip()֮��Buffer��дģʽ��ɶ�ģʽ��      
				buffer.flip();
				charBuffer = decoder.decode(buffer);
				String msg = charBuffer.toString().trim();
				System.out.println("��server���ͻ����������ϢΪ��"+msg);
				ByteBuffer outBuffer = ByteBuffer.wrap(("��server�����յ�����"+msg).getBytes());
				channel.write(outBuffer).get();
				if(buffer.hasRemaining()) {
					//��û�з��������ݸ��Ƶ� buffer�Ŀ�ʼλ�ã��´μ�������
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
		//����ExecutorService
		taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
		serverChannel = AsynchronousServerSocketChannel.open();
		if(serverChannel.isOpen()) {
			serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
			serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			serverChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
		}else {
			throw new RuntimeException("ͨ��δ��");
		}
	}
	
	public void start() {
		System.out.println("��server���ȴ��ͻ�������...");
		while(true) {
			Future<AsynchronousSocketChannel> future = serverChannel.accept();
			try {
				AsynchronousSocketChannel channel = future.get();
				//�ύ���̳߳ش���
				taskExecutor.submit(new Worker(channel));
			} catch (Exception e) {
				System.err.println("�������ر�");
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
