package chz.common.util.socket;

import java.io.IOException;
import java.net.UnknownHostException;

public class _SXMClientTest {

	public static void main(String[] args) throws UnknownHostException, IOException {
		SXMClient client = new SXMClient("127.0.0.1", 9987);
		client.connect();
		client.send("hello world!".getBytes());
		SXMMessage message = client.accept();
		System.out.println("["+new String(message.getBody())+"]");
		client.send("close".getBytes());
		client.close();
		System.out.println("client end");
	}
}
