package chz.common.util.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;

import chz.common.util.common.XMLUtil;

public class SXMSocket {

	private String host;
	private int port;
	
	private Socket socket;
	
	private int soTimeout = 30000;	// 30s
	
	public SXMSocket(Socket socket) {
		this.socket = socket;
	}
	
	public SXMSocket(int port){
		this("127.0.0.1", port);
	}
	
	public SXMSocket(String host, int port){
		this.host = host;
		this.port = port;
		this.socket = new Socket();
	}
	
	//--------------
	
	private void setSocketParameterBeforeConnect() throws SocketException {
		this.socket.setSoTimeout(soTimeout);
	}
	
	public void connect() throws IOException {
		setSocketParameterBeforeConnect();
		this.socket.connect( new InetSocketAddress(host, port) );
	}
	
	public void connect(int timeout) throws IOException {
		setSocketParameterBeforeConnect();
		this.socket.connect( new InetSocketAddress(host, port), timeout );
	}
	
	//---------------
	
	public void send(Document doc) throws IOException{
		send(XMLUtil.xmlToByteArray(doc));
	}
	
	public void send(Document doc, OutputFormat format) throws IOException{
		send(XMLUtil.xmlToByteArray(doc, format));
	}
	
	public void send(byte[] bytes) throws IOException{
		SXMMessage message = SXMMessage.toSXMMessage(bytes);
		send(message);
	}
	
	public void send(SXMMessage message) throws IOException{
		OutputStream out = socket.getOutputStream();
		out.write(message.getHeader());
		out.write(message.getBody());
	}
	
	//-------------
	
	public SXMMessage accept() throws IOException{
		SXMMessage message = SXMMessage.readMessage(this.socket.getInputStream());
		return message;
	}
	
	//-------------
	
	public void close() throws IOException{
		this.socket.close();
	}
	
	//----------
	
	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}
	
}
