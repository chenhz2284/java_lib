package chz.common.util.socket;


public class SXMMessageEvent {

	private SXMServer server;
	private SXMSocket socket;
	private SXMMessage message;
	
	public SXMServer getServer() {
		return server;
	}

	public void setServer(SXMServer server) {
		this.server = server;
	}
	
	public SXMSocket getSocket() {
		return socket;
	}

	public void setSocket(SXMSocket socket) {
		this.socket = socket;
	}
	
	public SXMMessage getMessage() {
		return message;
	}

	public void setMessage(SXMMessage message) {
		this.message = message;
	}

}
