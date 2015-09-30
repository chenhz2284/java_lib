package chz.common.util.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import chz.common.util.logger.LogFactory;

public class SXMSocketAccepter extends Thread {

	private SXMServer server;
	private SXMSocket socket;
	
	public SXMSocketAccepter(SXMServer server, Socket socket) {
		this.server = server;
		this.socket = new SXMSocket(socket);
	}
	
	public void accept() throws IOException{
		SXMMessage message = this.socket.accept();
		// 
		SXMMessageEvent event = new SXMMessageEvent();
		event.setServer(this.server);
		event.setSocket(this.socket);
		event.setMessage(message);
		//
		SXMMessageListener[] listenerArr = this.server.getSXMMessageListeners();
		for( SXMMessageListener listener : listenerArr ){
			try {
				listener.actionPerformed(event);
			} catch (Throwable e) {
				LogFactory.getLog(this.getClass()).error("", e);
			}
		}
	}
	
	public void run() {
		try {
			this.accept();
		} catch (SocketException e) {
			if( "Connection reset".equals(e.getMessage()) ){
				LogFactory.getLog(this.getClass()).error("对方的程序可能已经退出", e);
			} else {
				LogFactory.getLog(this.getClass()).error("", e);
			}
		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).error("", e);
		}
		try {
			this.socket.close();
		} catch (IOException e) {
			LogFactory.getLog(this.getClass()).error("", e);
		}
	}
}
