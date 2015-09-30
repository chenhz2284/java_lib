package chz.common.util.logger;

import org.apache.commons.logging.Log;

import chz.common.constant.Constant;
import chz.common.util.common.ExceptionUtil;

public class LogImpl implements Log{
	
	//----------
	
	protected String name;
	protected Log logger;
	
	public LogImpl(String name){
		this.name = name;
		this.logger = org.apache.commons.logging.LogFactory.getLog(name);
	}
	
	public LogImpl(Class clazz){
		this.name = clazz.getName();
		this.logger = org.apache.commons.logging.LogFactory.getLog(clazz);
	}
	
	//----------

	public void debug(Object obj) {
		logger.debug(obj);
	}

	public void debug(Object obj, Throwable throwable) {
		logger.debug(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}

	public void info(Object obj) {
		logger.info(obj);
	}

	public void info(Object obj, Throwable throwable) {
		logger.info(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}
	
	public void warn(Object obj) {
		logger.warn(obj);
	}

	public void warn(Object obj, Throwable throwable) {
		logger.warn(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}
	
	public void error(Object obj) {
		logger.error(obj);
	}

	public void error(Object obj, Throwable throwable) {
		logger.error(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}

	public void fatal(Object obj) {
		logger.fatal(obj);
	}

	public void fatal(Object obj, Throwable throwable) {
		logger.fatal(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}
	
	public void trace(Object obj) {
		logger.trace(obj);
	}

	public void trace(Object obj, Throwable throwable) {
		logger.trace(obj + Constant.line_separator + ExceptionUtil.toStackTraceString(throwable));
	}
	
	//-------------
	
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return logger.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}
	
	//--------
	
	public String toString(){
		return this.getClass().toString()+"; logger:["+logger.toString()+"]; name=["+this.name+"]";
	}
	
}
