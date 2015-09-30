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
	private byte[] lineFeed;	// 回车
	
	private BufferedInputStream inStream ;
	
	private boolean isEOF = false;			// 输入流读到-1,表明对方关闭的输入流
	private boolean isInputEnd = false;		// 结束的分隔符标记
	
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
			getSeperator(inStream);		// 获取分隔符,和换行符
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
		byte[] bytes = readline(inStream);	// 读一行
		// 去除未尾的回车
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
		// 获取回车
		lineFeed = new byte[bos.size()];
		byte[] temp = bos.toByteArray();	// 需要反转才能使用
		for( int i=0; i<lineFeed.length; i++ ){
			lineFeed[i] = temp[temp.length-1-i];	// 反转之后就是回车
		}
		// 获取分隔符
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
	 * 从一个流中读取一行
	 * windows中"\r\n"表示换行
	 * unix中"\n"表示换行
	 * mac中"\r"表示换行
	 * @param in: 输入流参数要支持mark()方法,可以使用BufferedIndexInput包装
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
				break ;			// 确定是一行了
			}
			if( d=='\r' ){
				in.mark(1);
				int d2 = in.read();
				in.reset();
				if( d2==-1 || d2=='\n' ){
					continue;		// 读完下一个才能确定是一行
				} else {
					break ;		// 确定是一行了
				}
			}
		}
		uploadContext.addReadCount(readCount);
	}
	
	private void handleContent(InputStream in) throws IOException{
		byte[] line1Bytes = readline(in);		// 读第一行
		Map<String, String> map = handleLine1(line1Bytes);
		if( map.get("filename")!=null )
		{	// 这是文件字段
			smartRequest.putParameter(map.get("name"), map.get("filename"));
			//
			byte[] line2Bytes = readline(in);	// 读第二行
			map.putAll( handleLine2(line2Bytes) );
			readline(in);						// 与内容之间空行
			// 保存文件
			if( !"".trim().equals(map.get("filename")) )
			{	// 文件名不为空
				SmartFile smartFile = new SmartFile();
				smartFile.setFromFilePath(map.get("filename"));
				smartFile.setSavedDir(savePath);
				smartFile.setContentType(map.get("Content-Type"));
				smartFiles.addFile(smartFile);
				// System.out.println("写文件 = "+smartFile.getSavedFilePath());
				BufferedOutputStream fos = null;
				try {
					fos =  new BufferedOutputStream(new FileOutputStream(smartFile.getSavedFile()));
					writeContent(in, fos);				// 写文件
				} finally {
					if( fos!=null ){
						fos.close();
					}
				}
			} else {
				// 文件名为空,给一个没用的输出流让它去写
				writeContent(in, new TrashyOutputStream());
			}
		} 
		else 
		{	// 普通字段
			readline(in);						// 与内容之间空行
			// 保存内容
			// System.out.println("写内容 = "+map.get("name"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(10240);
			writeContent(in, bos);
			smartRequest.putParameter(map.get("name"), new String(bos.toByteArray()));
		}
		if( !isEOF && !isInputEnd ){
			handleContent(in);
		}
	}
	
	/*
	 * 第一行的内容通常如下:
	 * Content-Disposition: form-data; name="file1"; filename="C:\Documents and Settings\chenhz.SUNTEKTECH\桌面\302.JPG"
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
	 * 第二行的内容通常如下:
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
		// 如下的内容是读取文件体,如果是不是文件的话,会直接读到分隔符
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
			{	// 读到分隔符
				writeContentLastLine(out, lastBytes);
				break;
			} 
			else if( comp_result==COMP_RESULT_INPUT_END )
			{	// 流的结尾
				isInputEnd = true;
				writeContentLastLine(out, lastBytes);
				break;
			} 
			else 
			{	// 普通内容
				if( lastBytes!=null ){
					out.write(lastBytes);
				}
				lastBytes = tempBytes;
				if( isEOF ){	// 流已经结束
					if( lastBytes.length!=0 ){
						out.write(lastBytes);
					}
					break;
				}
			}
		}
	}
	
	/*
	 * 写最后一行
	 */
	private void writeContentLastLine(OutputStream out, byte[] bytes) throws IOException{
		out.write(bytes, 0, bytes.length-lineFeed.length);	// 减去回车的长度
	}
	
	//*******************************************************
	
	private static final int COMP_RESULT_NOTEQUAL = 1;		// 不相等
	private static final int COMP_RESULT_EQUAL = 2;			// 相等
	private static final int COMP_RESULT_STARTSWITH = 3;	// 开始部份相同
	private static final int COMP_RESULT_INPUT_END = 4;		// 结束符
	
	private int compareSeperator( byte[] bytes ){
		if( bytes.length<seperatorByte.length ){
			return COMP_RESULT_NOTEQUAL;		// 不是分隔符
		}
		for( int i=0; i<seperatorByte.length; i++ ){
			if( bytes[i]!=seperatorByte[i] ){
				return COMP_RESULT_NOTEQUAL;	// 不是分隔符
			}
		}
		if( bytes.length>=seperatorByte.length+2 && bytes[seperatorByte.length]=='-' && bytes[seperatorByte.length+1]=='-' ){	
			// 以结束符做为开始,后续位必须都为\r或者\n
			for( int i=seperatorByte.length+2; i<bytes.length; i++ ){
				if( bytes[i]!='\r' && bytes[i]!='\n' ){
					return COMP_RESULT_STARTSWITH;	// 以分隔符做为开始
				}
			}
			return COMP_RESULT_INPUT_END;				// 结束符
		}
		if( bytes.length>=seperatorByte.length ){
			// 以分隔符做为开始,后续位必须都为\r或者\n
			for( int i=seperatorByte.length; i<bytes.length; i++ ){
				if( bytes[i]!='\r' && bytes[i]!='\n' ){
					return COMP_RESULT_STARTSWITH;	// 以分隔符做为开始
				}
			}
			return COMP_RESULT_EQUAL;			// 是分隔符
		}
		return COMP_RESULT_STARTSWITH;			// 以分隔符做为开始
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
