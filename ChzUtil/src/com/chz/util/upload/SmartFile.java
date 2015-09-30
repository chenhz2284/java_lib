package com.chz.util.upload;

import java.io.File;
import java.io.IOException;

import chz.common.util.common.StringUtil;

public class SmartFile {

	private File fromFile;
	private File saveFile;
	private String contentType;
	
	//*********
	
	public void setFromFilePath(String absolutePath) throws IOException{
		fromFile = new File(absolutePath).getCanonicalFile();
	}
	
	public String getFromFileName(){
		return fromFile.getName();
	}
	
	public String getFromDir(){
		return this.fromFile.getParent();
	}
	
	public String getFromFilePath(){
		return this.fromFile.getAbsolutePath();
	}
	
	//*********
	
	public void setSavedDir(String savedPath) throws IOException{
		saveFile = new File( savedPath, StringUtil.generateUUID3()+"_"+getFromFileName() ).getCanonicalFile();
	}
	
	public String getSavedDir(){
		return saveFile.getParent();
	}
	
	public String getSavedFilePath(){
		return saveFile.getAbsolutePath();
	}
	
	public File getSavedFile(){
		return saveFile;
	}
	
	public String getSavedFileName(){
		return saveFile.getName();
	}
	
	//*******

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
