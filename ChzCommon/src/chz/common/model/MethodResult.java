package chz.common.model;

import java.util.HashMap;
import java.util.Map;

import chz.common.util.json.JSONBean;

public class MethodResult implements JSONBean {

	public String status = "success";	// error, success
	public String message = "";
	public Exception exception = null;
	
	//***************
	
	public MethodResult(){
	}
	
	public MethodResult(String message){
		this.message = message;
	}
	
	public MethodResult(String status, String message){
		this.status = status;
		this.message = message;
	}
	
	//***************
	
	public Map<Object, Object> properties = new HashMap<Object, Object>();
	
	public Object put( Object key, Object value ){
		return properties.put(key, value);
	}
	
	public Object get( Object key ){
		return properties.get(key);
	}
	
	public String setProperty(String key, Object value){
		return (String)properties.put(key, value);
	}
	
	public String getProperty(String key){
		return (String)properties.get(key);
	}
	
}
