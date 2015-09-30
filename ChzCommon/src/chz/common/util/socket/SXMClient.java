package chz.common.util.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 客户端: 因为跟SXMSocket的功能一样,所以直接继承就可以了
 */
public class SXMClient extends SXMSocket {
	
	public SXMClient(Socket socket) throws IOException {
		super(socket);
	}
	
	public SXMClient(int port) throws UnknownHostException, IOException{
		super(port);
	}
	
	public SXMClient(String host, int port) throws UnknownHostException, IOException{
		super(host, port);
	}
	
}
