package chz.common.util.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapParameter implements Parameter{

	private Map map;
	
	public MapParameter(Map map){
		this.map = map;
	}
	
	public String getParameter(String key) {
		return (String)map.get(key);
	}

	public List<String> getParameters(String key) {
		String[] arr = (String[])map.get(key);
		if(arr==null || arr.length==0){
			return new ArrayList<String>();
		} else {
			List<String> result = new ArrayList<String>(arr.length);
			for( int i=0; i<arr.length; i++ ){
				result.add(arr[i]);
			}
			return result;
		}
	}

}
