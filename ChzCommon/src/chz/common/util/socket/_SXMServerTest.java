package chz.common.util.socket;

import java.io.IOException;


public class _SXMServerTest {

	public static void main(String[] args) throws Exception {
		SXMServer server = new SXMServer(9987);
		server.addSXMMessageListener(new SXMMessageListener(){
			public void actionPerformed(SXMMessageEvent event) {
				try {
					System.out.println("SXMMessageListener.actionPerformed()");
					SXMMessage message = event.getMessage();
					SXMSocket socket = event.getSocket();
					SXMServer server = event.getServer();
					//
					System.out.println("["+new String(message.getBody())+"]");
					socket.send("I have receive your hello!".getBytes());
					//
					message = socket.accept();
					String msg = new String(message.getBody());
					System.out.println("["+msg+"]");
					if( "close".equals(msg) ){
						socket.close();
						server.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		server.accept();
		System.out.println("server end");
	}
}
