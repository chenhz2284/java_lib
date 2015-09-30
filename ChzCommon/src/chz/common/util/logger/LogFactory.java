package chz.common.util.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

public class LogFactory {

	public static Log getLog(Class clazz) throws LogConfigurationException {
		return new LogImpl(clazz);
	}

	public static Log getLog(String name) throws LogConfigurationException {
		return new LogImpl(name);
	}

}
