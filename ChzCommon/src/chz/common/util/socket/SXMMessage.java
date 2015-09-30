package chz.common.util.socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import chz.common.util.common.ByteUtil;
import chz.common.util.common.StreamUtil;

public class SXMMessage {

	private int length;
	private int crc;
	
	/*
	 * 头64个字节的安排是(下标从0开始):
	 * 0-3位是升序
	 * 4-7位是crc码
	 * 8-63位是保留扩展用途
	 * 64开始是body,长度由头0-3位组成的int数字表示
	 */
	private byte[] header = new byte[64];	// 头部一定是64个字节
	private byte[] body;					// body可以是任意的字节
	
	private Document doc;					// 由body转换成的xml对象
	
	public int getLength(){
		return this.length;
	}
	
	public int getCRC(){
		return this.crc;
	}
	
	public byte[] getHeader(){
		return this.header;
	}
	
	public byte[] getBody(){
		return this.body;
	}
	
	public Document toXMLDocument() throws DocumentException{
		if( this.doc==null ){
			this.doc = new SAXReader().read(new ByteArrayInputStream(body));
		}
		return doc;
	}
	
	//----------
	
	public static SXMMessage readMessage(InputStream in) throws IOException{
		SXMMessage message = new SXMMessage();
		// 读取header
		int readLength = StreamUtil.inputStreamToByteArray(in, message.header, 0, 64); // in.read(message.header, 0, 64);
		if( readLength < 64 ){
			throw new IOException("无法读取到足够长度的消息头数据, 可能对方Socket已经关闭! read length=["+readLength+"], need length=[64]");
		}
		// 读取长度
		message.length = ByteUtil.bytesToInt(message.header, 0, 4);
		// 读取crc
		message.crc = ByteUtil.bytesToInt(message.header, 4, 4);
		// 读取body
		message.body = new byte[message.length];
		readLength = StreamUtil.inputStreamToByteArray(in, message.body, 0, message.length);// in.read(message.body, 0, message.length);
		if( readLength < message.length ){
			throw new IOException("无法读取到足够长度的消息体数据, 可能对方Socket已经关闭! read length=["+readLength+"] need length=["+message.length+"]");
		}
		// 验证crc
		int crc = 0;
		for( int i=0;  i<message.length; i+=4 ){
			crc += ByteUtil.bytesToInt(message.body, i, message.length-i);
		}
		if( message.crc==crc ){
			return message;
		} else {
			throw new IOException("crc验证失败");
		}
	}
	
	public static SXMMessage toSXMMessage(byte[] bytes){
		SXMMessage message = new SXMMessage();
		// 填写长度
		message.length = bytes.length;
		ByteUtil.intToBytes(message.length, message.header, 0);
		// 填写crc
		message.crc = 0;
		for( int i=0;  i<bytes.length; i+=4 ){
			message.crc += ByteUtil.bytesToInt(bytes, i, bytes.length-i);
		}
		ByteUtil.intToBytes(message.crc, message.header, 4);
		// 填充内容
		message.body = bytes;
		return message;
	}
	
}
