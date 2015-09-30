package com.chz.util.upload;

import java.util.ArrayList;
import java.util.List;

public class SmartFiles {

	private List<SmartFile> fileList = new ArrayList<SmartFile>();
	
	public SmartFile getFile(int index){
		return fileList.get(index);
	}
	
	public void addFile(SmartFile file){
		fileList.add(file);
	}
	
	public int size(){
		return fileList.size();
	}
	
}
