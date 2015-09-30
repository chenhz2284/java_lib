package com.chz.util.upload;

import javax.servlet.http.HttpSession;


public class SmartUploadContext {

	private SmartUploadContext(){
	}
	
	//***********************************
	
	public synchronized static SmartUploadContext getInstance(HttpSession session){
		SmartUploadContext instance = (SmartUploadContext)session.getAttribute("SmartUploadContext");
		if( instance==null ){
			instance = new SmartUploadContext();
			session.setAttribute("SmartUploadContext", instance);
		}
		return instance;
	}
	
	//***********************************
	
	private boolean isUpload = false;		// 表示当前是否正在上传
	private long totalCount = 0;			// 表示总共有多少个字节
	private long readCount = 0;				// 表示已经读取了多少个字节
	private double readPercent = 0;			// 表示已经读取的百分比
	
	//***********************************
	
	public synchronized void init(){
		isUpload = false;
		totalCount = 0;
		readCount = 0;
		readPercent = 0;
	}
	
	public synchronized long addReadCount( long n ){
		readCount += n;
		readPercent = (double)readCount / totalCount;
		//Logger.info( "addReadCount : "+n );
		//Logger.info( "totalCount : "+totalCount );
		//Logger.info( "readCount : "+readCount );
		//Logger.info( "readPercent : "+readPercent );
		return readCount;
	}
	
	//***********************************

	public synchronized boolean isUpload() {
		return isUpload;
	}
	public synchronized void setIsUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}
	public synchronized long getTotalCount() {
		return totalCount;
	}
	public synchronized void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public synchronized long getReadCount() {
		return readCount;
	}
	public synchronized double getReadPercent() {
		return readPercent;
	}
	public synchronized String getReadPercentString() {
		return (int)(readPercent*100)+"%";
	}
	
	//***********************************
	
}
