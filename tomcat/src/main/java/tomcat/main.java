package tomcat;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;


public class main {
	public static void main(String[] args) throws IOException {
		
	}
	
	
	private static void m1() throws IOException {
		Selector selector = Selector.open();
		SocketChannel channel = SocketChannel.open();
		channel.register(selector, SelectionKey.OP_ACCEPT);
	}
}
