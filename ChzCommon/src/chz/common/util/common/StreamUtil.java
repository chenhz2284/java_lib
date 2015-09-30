package chz.common.util.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class StreamUtil {

	/**
	 * 将一个注入流转化为字节数组
	 */
	public static byte[] inputStreamToByteArray( InputStream in ) throws IOException{
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		byte[] buf = new byte[1024*64];
		while(true){
			int len = in.read(buf);
			if(len==-1){
				break;
			}
			bos.write(buf, 0, len);
		}
		return bos.toByteArray();
	}
	
	/**
	 * 将一个输入流转化为字节数组
	 */
	public static byte[] inputStreamToByteArray( InputStream in, int contentLength ) throws IOException{
		if( contentLength<0 ){	// 不知道长度
			return inputStreamToByteArray(in);
		} else {				// 知道长度
			byte[] buf = new byte[1024*64];
			byte[] result = new byte[contentLength];
			int wPoint = 0;
			int remain = result.length;
			try {
				while( true ){
					if( remain<=0 ){
						break;
					}
					int len = in.read(buf, 0, Math.min(remain, buf.length));
					if( len==-1 ){
						break;
					}
					System.arraycopy(buf, 0, result, wPoint, len);
					wPoint += len;
					remain -= len;
				}
				if( result.length==wPoint ){	// 长度已经读满了
					return result;
				} else {	// 长度没有读满
					byte[] result2 = new byte[wPoint];
					System.arraycopy(result, 0, result2, 0, wPoint);
					return result2;
				}
			} catch (IOException e) {
				byte[] result2 = new byte[wPoint];
				System.arraycopy(result, 0, result2, 0, wPoint);
				throw e;
			}
		}
	}
	
	/*
	 * 将从in读取的字节填充到bytes中,填充的位置由offset指定,填充的长度由length指定
	 * 返回向bytes写入的字节数
	 */
	public static int inputStreamToByteArray(InputStream in, byte[] bytes, int offset, int length) throws IOException{
		byte[] buf = new byte[2];
		int wPoint = offset;
		int remain = length;
		while( true ){
			if( remain<=0 ){
				break;
			}
			int len = in.read(buf, 0, Math.min(remain, buf.length));
			if( len==-1 ){
				break;
			}
			System.arraycopy(buf, 0, bytes, wPoint, len);
			wPoint += len;
			remain -= len;
		}
		return wPoint-offset;
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public static int writeInputStreamToOutputStream(InputStream in, OutputStream out) throws IOException{
		int count = 0;
		byte[] buf = new byte[1024*64];
		while(true){
			int len = in.read(buf);
			if( len==-1 ){
				break;
			}
			count += len;
			out.write(buf, 0, len);
		}
		return count;
	}
	
	/**
	 * 将一个输入流写入到一个文件当中
	 * @throws IOException 
	 */
	public static void writeInputStreamToFile( InputStream in, File f ) throws IOException{
		FileOutputStream fos = new FileOutputStream(f);
		try {
			byte[] b = new byte[1024];
			while(true){
				int len = in.read(b);
				if(len==-1){
					break;
				}
				fos.write(b, 0, len);
			}
		} finally {
			fos.close();
		}
	}
	
	/**
	 * 将一个字节数组写入到一个文件当中
	 * @throws IOException 
	 */
	public static void writeByteArrayToFile( byte[] bytes, File f ) throws IOException{
		if( bytes==null ){
			throw new IllegalArgumentException("bytes is null");
		}
		FileOutputStream fos = new FileOutputStream(f);
		try {
			fos.write(bytes);
		} finally {
			fos.close();
		}
	}
	
	/**
	 * 从一个文件取出流
	 */
	public static InputStream getInputStreamFromFile(File f) throws FileNotFoundException{
		FileInputStream fis = new FileInputStream(f);
		return fis;
	}
	
	/**
	 * 从一个文件取出流
	 */
	public static InputStream getInputStreamFromFile(String fileName) throws FileNotFoundException{
		return getInputStreamFromFile(new File(fileName));
	}
	
	/**
	 * 
	 */
	public static InputStream getInputStreamFromByteArray(byte[] bytes){
		return new ByteArrayInputStream(bytes);
	}
	
	/**
	 * 
	 */
	public static InputStream getInputStreamFromString(String string, String encoding) throws UnsupportedEncodingException{
		byte[] bytes = string.getBytes(encoding);
		return new ByteArrayInputStream(bytes);
	}
	
	public static void main(String[] args) throws IOException {
		InputStream in = new ByteArrayInputStream("12345678901234567890".getBytes());
		byte[] bytes = new byte[10];
		byte[] bytes2 = new byte[10];
		byte[] bytes3 = new byte[10];
		inputStreamToByteArray(in, bytes, 0, 7);
		inputStreamToByteArray(in, bytes2, 0, 7);
		inputStreamToByteArray(in, bytes3, 0, 7);
		System.out.println(StringUtil.byteToPrintString(bytes));
		System.out.println(StringUtil.byteToPrintString(bytes2));
		System.out.println(StringUtil.byteToPrintString(bytes3));
	}
	
}
