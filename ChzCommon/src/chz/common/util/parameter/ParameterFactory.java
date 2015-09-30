package chz.common.util.parameter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ParameterFactory {

	public static Parameter createRequestParameter(HttpServletRequest request){
		return new RequestParameter(request);
	}
	
	public static Parameter createMapParameter(Map map){
		return new MapParameter(map);
	}
}
