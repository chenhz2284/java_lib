package chz.common.util.parameter;

import java.util.ArrayList;
import java.util.List;

public class NullParameter implements Parameter {

	public String getParameter(String key){
		return null;
	}
	
	public List<String> getParameters(String key){
		return new ArrayList<String>();
	}
	
}
