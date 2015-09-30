package chz.common.util.parameter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class RequestParameter implements Parameter{

	private HttpServletRequest request;
	
	public RequestParameter(HttpServletRequest request){
		this.request = request;
	}
	
	public String getParameter(String key){
		return request.getParameter(key);
	}
	
	public List<String> getParameters(String key){
		String[] arr = request.getParameterValues(key);
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
