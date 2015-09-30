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
	 * ͷ64���ֽڵİ�����(�±��0��ʼ):
	 * 0-3λ������
	 * 4-7λ��crc��
	 * 8-63λ�Ǳ�����չ��;
	 * 64��ʼ��body,������ͷ0-3λ��ɵ�int���ֱ�ʾ
	 */
	private byte[] header = new byte[64];	// ͷ��һ����64���ֽ�
	private byte[] body;					// body������������ֽ�
	
	private Document doc;					// ��bodyת���ɵ�xml����
	
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
		// ��ȡheader
		int readLength = StreamUtil.inputStreamToByteArray(in, message.header, 0, 64); // in.read(message.header, 0, 64);
		if( readLength < 64 ){
			throw new IOException("�޷���ȡ���㹻���ȵ���Ϣͷ����, ���ܶԷ�Socket�Ѿ��ر�! read length=["+readLength+"], need length=[64]");
		}
		// ��ȡ����
		message.length = ByteUtil.bytesToInt(message.header, 0, 4);
		// ��ȡcrc
		message.crc = ByteUtil.bytesToInt(message.header, 4, 4);
		// ��ȡbody
		message.body = new byte[message.length];
		readLength = StreamUtil.inputStreamToByteArray(in, message.body, 0, message.length);// in.read(message.body, 0, message.length);
		if( readLength < message.length ){
			throw new IOException("�޷���ȡ���㹻���ȵ���Ϣ������, ���ܶԷ�Socket�Ѿ��ر�! read length=["+readLength+"] need length=["+message.length+"]");
		}
		// ��֤crc
		int crc = 0;
		for( int i=0;  i<message.length; i+=4 ){
			crc += ByteUtil.bytesToInt(message.body, i, message.length-i);
		}
		if( message.crc==crc ){
			return message;
		} else {
			throw new IOException("crc��֤ʧ��");
		}
	}
	
	public static SXMMessage toSXMMessage(byte[] bytes){
		SXMMessage message = new SXMMessage();
		// ��д����
		message.length = bytes.length;
		ByteUtil.intToBytes(message.length, message.header, 0);
		// ��дcrc
		message.crc = 0;
		for( int i=0;  i<bytes.length; i+=4 ){
			message.crc += ByteUtil.bytesToInt(bytes, i, bytes.length-i);
		}
		ByteUtil.intToBytes(message.crc, message.header, 4);
		// �������
		message.body = bytes;
		return message;
	}
	
}
