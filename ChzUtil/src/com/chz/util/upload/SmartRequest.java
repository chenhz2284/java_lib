package com.chz.util.upload;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SmartRequest {

	private Map<String, String> map = new HashMap<String, String>();
	
	public String putParameter(String key, String value){
		return map.put(key, value);
	}
	
	public String getParameter(String key){
		return map.get(key);
	}
	
	public Set<Entry<String, String>> entrySet(){
		return map.entrySet();
	}
	
	public void putAll(SmartRequest request){
		map.putAll(request.map);
	}
	
	public String toString(){
		return map.toString();
	}
}
