package chz.common.util.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * �ͻ���: ��Ϊ��SXMSocket�Ĺ���һ��,����ֱ�Ӽ̳оͿ�����
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
