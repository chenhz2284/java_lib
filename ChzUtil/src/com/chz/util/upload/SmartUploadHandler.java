package com.chz.util.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class SmartUploadHandler {

	private HttpServletRequest request;
	
	private SmartUploadContext uploadContext;
	
	private SmartRequest smartRequest;
	
	private SmartFiles smartFiles;
	private String savePath ;
	
	private int contentLength = 0;
	private File tempFile;
	
	private byte[] seperatorByte;
	private byte[] lineFeed;	// �س�
	
	private BufferedInputStream inStream ;
	
	private boolean isEOF = false;			// ����������-1,�����Է��رյ�������
	private boolean isInputEnd = false;		// �����ķָ������
	
	public void init(HttpServletRequest request){
		this.request = request;
		contentLength = request.getContentLength();
		smartFiles = new SmartFiles();
		smartRequest = new SmartRequest();
		//
		uploadContext = SmartUploadContext.getInstance(request.getSession());
		uploadContext.init();
		uploadContext.setIsUpload(true);
		uploadContext.setTotalCount(contentLength);
	}
	
	private InputStream getInputStream() throws IOException{
		if( inStream==null ){
			inStream = new BufferedInputStream(request.getInputStream());
		}
		return inStream;
	}
	
	public void setSavePath(String savePath){
		this.savePath = savePath;
		new File(savePath).mkdirs();
	}
	
	private void check(){
		if( contentLength==0 ){
			throw new RuntimeException("content length is zero");
		}
		if( savePath==null ){
			throw new RuntimeException("save path is null");
		}
		if( smartFiles==null ){
			throw new RuntimeException("smartFiles is null");
		}
		if( smartRequest==null ){
			throw new RuntimeException("smartRequest is null");
		}
	}
	
	public int startUpload() throws IOException{
		check();
		try {
			getInputStream();
			getSeperator(inStream);		// ��ȡ�ָ���,�ͻ��з�
			handleContent(inStream);
		} catch (IOException e) {
			if( "reach isEOF".equals(e.getMessage()) || "reach isInputEnd".equals(e.getMessage()) ){
				System.out.println(e.getMessage());
			} else {
				e.printStackTrace();
			}
		} finally {
			inStream.close();
			if( tempFile!=null && tempFile.isFile() ){
				tempFile.delete();
			}
		}
		return smartFiles.size();
	}
	
	private byte[] getSeperator(InputStream in) throws IOException{
		byte[] bytes = readline(inStream);	// ��һ��
		// ȥ��δβ�Ļس�
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int lastIndex = bytes.length-1;
		for( ; lastIndex>=0; lastIndex--){
			char c = (char)bytes[lastIndex];
			if( c=='\r' || c=='\n' ){
				bos.write(c);
			} else {
				break;
			}
		}
		// ��ȡ�س�
		lineFeed = new byte[bos.size()];
		byte[] temp = bos.toByteArray();	// ��Ҫ��ת����ʹ��
		for( int i=0; i<lineFeed.length; i++ ){
			lineFeed[i] = temp[temp.length-1-i];	// ��ת֮����ǻس�
		}
		// ��ȡ�ָ���
		seperatorByte = new byte[bytes.length-lineFeed.length];
		System.arraycopy(bytes, 0, seperatorByte, 0, seperatorByte.length);
		return seperatorByte;
	}
	
	private byte[] readline(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10240);
		readline( in, bos );
		return bos.toByteArray();
	}
	
	/**
	 * ��һ�����ж�ȡһ��
	 * windows��"\r\n"��ʾ����
	 * unix��"\n"��ʾ����
	 * mac��"\r"��ʾ����
	 * @param in: ����������Ҫ֧��mark()����,����ʹ��BufferedIndexInput��װ
	 * @throws IOException 
	 */
	private void readline(InputStream in, OutputStream out) throws IOException {
		int readCount = 0;
		int d = -1;
		while( true ){
			d=in.read();
			if( d==-1 ){
				isEOF = true;
				break ;
			}
			readCount++;
			out.write(d);
			if( d=='\n' ){
				break ;			// ȷ����һ����
			}
			if( d=='\r' ){
				in.mark(1);
				int d2 = in.read();
				in.reset();
				if( d2==-1 || d2=='\n' ){
					continue;		// ������һ������ȷ����һ��
				} else {
					break ;		// ȷ����һ����
				}
			}
		}
		uploadContext.addReadCount(readCount);
	}
	
	private void handleContent(InputStream in) throws IOException{
		byte[] line1Bytes = readline(in);		// ����һ��
		Map<String, String> map = handleLine1(line1Bytes);
		if( map.get("filename")!=null )
		{	// �����ļ��ֶ�
			smartRequest.putParameter(map.get("name"), map.get("filename"));
			//
			byte[] line2Bytes = readline(in);	// ���ڶ���
			map.putAll( handleLine2(line2Bytes) );
			readline(in);						// ������֮�����
			// �����ļ�
			if( !"".trim().equals(map.get("filename")) )
			{	// �ļ�����Ϊ��
				SmartFile smartFile = new SmartFile();
				smartFile.setFromFilePath(map.get("filename"));
				smartFile.setSavedDir(savePath);
				smartFile.setContentType(map.get("Content-Type"));
				smartFiles.addFile(smartFile);
				// System.out.println("д�ļ� = "+smartFile.getSavedFilePath());
				BufferedOutputStream fos = null;
				try {
					fos =  new BufferedOutputStream(new FileOutputStream(smartFile.getSavedFile()));
					writeContent(in, fos);				// д�ļ�
				} finally {
					if( fos!=null ){
						fos.close();
					}
				}
			} else {
				// �ļ���Ϊ��,��һ��û�õ����������ȥд
				writeContent(in, new TrashyOutputStream());
			}
		} 
		else 
		{	// ��ͨ�ֶ�
			readline(in);						// ������֮�����
			// ��������
			// System.out.println("д���� = "+map.get("name"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(10240);
			writeContent(in, bos);
			smartRequest.putParameter(map.get("name"), new String(bos.toByteArray()));
		}
		if( !isEOF && !isInputEnd ){
			handleContent(in);
		}
	}
	
	/*
	 * ��һ�е�����ͨ������:
	 * Content-Disposition: form-data; name="file1"; filename="C:\Documents and Settings\chenhz.SUNTEKTECH\����\302.JPG"
	 */
	private Map<String, String> handleLine1(byte[] bytes){
		Map<String, String> map = new HashMap<String, String>();
		String line = new String(bytes);
		String[] lineSegments = line.split(";");
		for( String segment : lineSegments ){
			segment = segment.trim();
			if( segment.startsWith("Content-Disposition:") ){
				String key = "Content-Disposition";
				String value = segment.substring(key.length()+1).trim();
				map.put(key, value);
			} else {
				String[] arr = segment.split("=");
				String key = arr[0].trim();
				String value = arr[1].trim();
				map.put(key, value.substring(1, value.length()-1));
			}
		}
		return map;
	}
	
	/*
	 * �ڶ��е�����ͨ������:
	 * Content-Type: image/pjpeg
	 */
	private Map<String, String> handleLine2(byte[] bytes){
		Map<String, String> map = new HashMap<String, String>();
		String line = new String(bytes);
		String[] arr = line.split(":");
		String key = arr[0].trim();
		String value = arr[1].trim();
		map.put(key, value);
		return map;
	}
	
	private void writeContent(InputStream in, OutputStream out) throws IOException{
		// ���µ������Ƕ�ȡ�ļ���,����ǲ����ļ��Ļ�,��ֱ�Ӷ����ָ���
		byte[] lastBytes = null;
		byte[] tempBytes = null;
		while( true ){
			if( isEOF ){
				System.out.println("isEOF");
				throw new IOException("reach isEOF");
			}
			if( isInputEnd ){
				System.out.println("isInputEnd");
				throw new IOException("reach isInputEnd");
			}
			tempBytes = readline(inStream);
			int comp_result = compareSeperator(tempBytes);
			if( comp_result==COMP_RESULT_EQUAL )
			{	// �����ָ���
				writeContentLastLine(out, lastBytes);
				break;
			} 
			else if( comp_result==COMP_RESULT_INPUT_END )
			{	// ���Ľ�β
				isInputEnd = true;
				writeContentLastLine(out, lastBytes);
				break;
			} 
			else 
			{	// ��ͨ����
				if( lastBytes!=null ){
					out.write(lastBytes);
				}
				lastBytes = tempBytes;
				if( isEOF ){	// ���Ѿ�����
					if( lastBytes.length!=0 ){
						out.write(lastBytes);
					}
					break;
				}
			}
		}
	}
	
	/*
	 * д���һ��
	 */
	private void writeContentLastLine(OutputStream out, byte[] bytes) throws IOException{
		out.write(bytes, 0, bytes.length-lineFeed.length);	// ��ȥ�س��ĳ���
	}
	
	//*******************************************************
	
	private static final int COMP_RESULT_NOTEQUAL = 1;		// �����
	private static final int COMP_RESULT_EQUAL = 2;			// ���
	private static final int COMP_RESULT_STARTSWITH = 3;	// ��ʼ������ͬ
	private static final int COMP_RESULT_INPUT_END = 4;		// ������
	
	private int compareSeperator( byte[] bytes ){
		if( bytes.length<seperatorByte.length ){
			return COMP_RESULT_NOTEQUAL;		// ���Ƿָ���
		}
		for( int i=0; i<seperatorByte.length; i++ ){
			if( bytes[i]!=seperatorByte[i] ){
				return COMP_RESULT_NOTEQUAL;	// ���Ƿָ���
			}
		}
		if( bytes.length>=seperatorByte.length+2 && bytes[seperatorByte.length]=='-' && bytes[seperatorByte.length+1]=='-' ){	
			// �Խ�������Ϊ��ʼ,����λ���붼Ϊ\r����\n
			for( int i=seperatorByte.length+2; i<bytes.length; i++ ){
				if( bytes[i]!='\r' && bytes[i]!='\n' ){
					return COMP_RESULT_STARTSWITH;	// �Էָ�����Ϊ��ʼ
				}
			}
			return COMP_RESULT_INPUT_END;				// ������
		}
		if( bytes.length>=seperatorByte.length ){
			// �Էָ�����Ϊ��ʼ,����λ���붼Ϊ\r����\n
			for( int i=seperatorByte.length; i<bytes.length; i++ ){
				if( bytes[i]!='\r' && bytes[i]!='\n' ){
					return COMP_RESULT_STARTSWITH;	// �Էָ�����Ϊ��ʼ
				}
			}
			return COMP_RESULT_EQUAL;			// �Ƿָ���
		}
		return COMP_RESULT_STARTSWITH;			// �Էָ�����Ϊ��ʼ
	}
	
	//***********************************
	
	public SmartFiles getFiles(){
		return this.smartFiles;
	}
	
	public SmartRequest getRequest(){
		return this.smartRequest;
	}
	
	//***********************
	
}
