package chz.common.util.common;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ByteUtil {
	
	/*
	 * ����λ����������������: <<, >>, >>>
	 * << ����,��߲�0
	 * >> ����,���������, �ұ߲�0, ����Ǹ���, �ұ߲�1
	 * >>> ����,�ұ߲�0
	 * byteҲ������, byte��ֵ��int(��long,��������û���Թ�), int��̳�byte������(�ұ߲�0��1)
	 */
	
	//----------
	
	public static byte[] copyBytes(byte[] bytes)
	{
		return createBytes(bytes, 0, bytes.length);
	} 
	
	public static byte[] createBytes(byte[] src, int offset, int length)
	{
		byte[] dest = new byte[length];
		System.arraycopy(src, offset, dest, 0, length);
		return dest;
	}
	
	//----------
	
	public static byte[] intToBytes(int d){
		byte[] bytes = new byte[4];
		intToBytes(d, bytes);
		return bytes;
	}

	public static void intToBytes(int d, byte[] bytes){
		bytes[0] = (byte)(d >> 24);
		bytes[1] = (byte)(d >> 16);
		bytes[2] = (byte)(d >> 8);
		bytes[3] = (byte)(d >> 0);
	}
	
	public static void intToBytes(int d, byte[] bytes, int offset){
		bytes[offset + 0] = (byte)(d >> 24);
		bytes[offset + 1] = (byte)(d >> 16);
		bytes[offset + 2] = (byte)(d >> 8);
		bytes[offset + 3] = (byte)(d >> 0);
	}
	
	public static int bytesToInt(byte[] bytes){
		return bytesToInt(bytes, 0, bytes.length);
	}
	
	public static int bytesToInt(byte[] bytes, int offset, int length){
		int d = 0;
		for( int i=0; i<4 && i<length; i++ ){
			d += (bytes[offset+i] & 0xff) << ((3-i)*8);
		}
		return d;
	}
	
	//-----------
	
	public static byte[] longToBytes(long d){
		byte[] bytes = new byte[8];
		longToBytes(d, bytes);
		return bytes;
	}

	public static void longToBytes(long d, byte[] bytes){
		bytes[0] = (byte)(d >> 56);
		bytes[1] = (byte)(d >> 48);
		bytes[2] = (byte)(d >> 40);
		bytes[3] = (byte)(d >> 32);
		bytes[4] = (byte)(d >> 24);
		bytes[5] = (byte)(d >> 16);
		bytes[6] = (byte)(d >> 8);
		bytes[7] = (byte)(d >> 0);
	}
	
	public static void longToBytes(long d, byte[] bytes, int offset){
		bytes[offset + 0] = (byte)(d >> 56);
		bytes[offset + 1] = (byte)(d >> 48);
		bytes[offset + 2] = (byte)(d >> 40);
		bytes[offset + 3] = (byte)(d >> 32);
		bytes[offset + 4] = (byte)(d >> 24);
		bytes[offset + 5] = (byte)(d >> 16);
		bytes[offset + 6] = (byte)(d >> 8);
		bytes[offset + 7] = (byte)(d >> 0);
	}
	
	public static long bytesToLong(byte[] bytes){
		return bytesToLong(bytes, 0, bytes.length);
	}
	
	public static long bytesToLong(byte[] bytes, int offset, int length){
		long d = 0;
		for( int i=0; i<8 && i<length; i++ ){
			d += (bytes[offset+i] & 0xffL) << ((7-i)*8);
		}
		return d;
	}
	
	//---------
	
	/**
	 * ���ֽ���ת��Ϊbase64�ı���
	 */
	public static String bytesToBase64( byte[] b ){
		if (b == null) {
			return null;
		}
		return new BASE64Encoder().encode(b);
	}
	
	/**
	 * ��base64�ı���ת��Ϊ�ֽ���
	 */
	public static byte[] base64ToBytes( String s ) throws IOException {
		if (s == null) {
			return null;
		}
		return  new BASE64Decoder().decodeBuffer(s);
	}
	
	/**
	 * �ж�value��ĳһλ�Ƿ�Ϊһ
	 */
	public static boolean isMask(long value, long mask)
	{
		return (value & mask)>0; 
	}
	
	//---------
	
//	public static void main(String[] args) {
////		testInt();
////		testLong();
//		byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
//		for( int i=0; i<bytes.length; i+=8 ){
//			long d = bytesToLong(bytes, i, bytes.length-i);
//			System.out.println(StringUtil.byteToAscii(longToBytes(d)));
//		}
//	}
	
//	public static void testInt(){
//		int d = 0x7fedcba9;
//		System.out.println(d);
//		
//		byte[] bytes = intToBytes(d);
//		System.out.println(StringUtil.byteToAscii(bytes));
//		
//		int d2 = bytesToInt(bytes, 0, 4);
//		System.out.println(d2);
//		
//		byte[] bytes2 = intToBytes(d2);
//		System.out.println(StringUtil.byteToAscii(bytes2));
//	}
	
//	public static void testLong(){
//		long d = 0x7fedcba987654321L;
//		System.out.println(d);
//		
//		byte[] bytes = longToBytes(d);
//		System.out.println(StringUtil.byteToAscii(bytes));
//		
//		long d2 = bytesToLong(bytes, 0, 8);
//		System.out.println(d2);
//		
//		byte[] bytes2 = longToBytes(d2);
//		System.out.println(StringUtil.byteToAscii(bytes2));
//	}
	
}










