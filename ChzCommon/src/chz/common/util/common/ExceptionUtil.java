package chz.common.util.common;

import java.io.File;

import chz.common.constant.Constant;


public class ExceptionUtil {

	public static String toStackTraceString(Throwable e)
	{
		StringBuilder sb = new StringBuilder();
		toStackTraceString(sb, e);
		return sb.toString();
	}
	
	public static String toStackTraceString(StringBuilder sb, Throwable e){
		sb.append(e.getClass().getName()+": "+StringUtil.forNull(e.getMessage())+Constant.line_separator);
		StackTraceElement[] traces = e.getStackTrace();
		for( StackTraceElement trace : traces){
			sb.append("    at "+trace.getClassName()+"."+trace.getMethodName()+"("+trace.getFileName()+":"+trace.getLineNumber()+")"+Constant.line_separator);
		}
		Throwable cause = e.getCause();
		if( cause!=null ){
			toCauseStackTraceString(sb, cause);
		}
		return sb.toString();
	}
	
	public static String toCauseStackTraceString(Throwable e)
	{
		StringBuilder sb = new StringBuilder();
		toCauseStackTraceString(sb, e);
		return sb.toString();
	}
	
	private static void toCauseStackTraceString(StringBuilder sb, Throwable e){
		sb.append( "Caused by: "+e.getClass().getName()+": "+StringUtil.forNull(e.getMessage())+Constant.line_separator );
		StackTraceElement[] traces = e.getStackTrace();
		for( StackTraceElement trace : traces){
			sb.append("    at "+trace.getClassName()+"."+trace.getMethodName()+"("+trace.getFileName()+":"+trace.getLineNumber()+")"+Constant.line_separator);
		}
		Throwable cause = e.getCause();
		if( cause!=null ){
			toCauseStackTraceString(sb, cause);
		}
	}
	
}
