package chz.common.util.parameter;

import java.util.List;

public interface Parameter {

	public String getParameter(String key);
	
	public List<String> getParameters(String key);
}
