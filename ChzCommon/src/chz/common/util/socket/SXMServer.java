package chz.common.util.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * SXM: Socket XML Message
 * ·þÎñ¶Ë
 */
public class SXMServer {

	private int port;
	private ServerSocket server;
	private List<SXMMessageListener> msgListenerList = new ArrayList<SXMMessageListener>();
	private boolean isClose = false;
	
	public SXMServer(int port) throws IOException{
		this.port = port;
		server = new ServerSocket(port);
	}
	
	public void accept() throws IOException{
		while( true ){
			if( isClose==true ){
				break;
			}
			try {
				Socket socket = server.accept();
				SXMSocketAccepter accepter = new SXMSocketAccepter(this, socket);
				accepter.start();
			} catch (SocketException e) {
				if( "socket closed".equals(e.getMessage()) ){
					System.out.println("SXMServer allready closed!");
				} else {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addSXMMessageListener(SXMMessageListener listener){
		synchronized (msgListenerList) {
			msgListenerList.add(listener);
		}
	}
	
	public void removeSXMMessageListener(SXMMessageListener listener){
		synchronized (msgListenerList) {
			msgListenerList.remove(listener);
		}
	}
	
	public SXMMessageListener[] getSXMMessageListeners(){
		synchronized (msgListenerList) {
			return msgListenerList.toArray(new SXMMessageListener[0]);
		}
	}
	
	public void close() throws IOException{
		this.isClose = true;
		this.server.close();
	}
	
	public boolean isClose(){
		return this.isClose;
	}
	
	
}
