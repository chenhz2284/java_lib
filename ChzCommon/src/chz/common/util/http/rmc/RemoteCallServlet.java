package chz.common.util.http.rmc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import chz.common.exception.IllegalDataException;
import chz.common.util.common.StreamUtil;
import chz.common.util.json.JSONUtil;


public abstract class RemoteCallServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8870362733394290135L;
	
	//-----------------
	
	public abstract void debug(String msg);
	
	public abstract void printError(Throwable error);
	
	//-------------
	
	
	/**
	 * 
	 */
	public abstract boolean isLogin(HttpServletRequest request, HttpServletResponse response);
	
	//-----------------

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		long time1 = System.nanoTime();
		debug(""+request.getRequestURL());
		
		response.setContentType("text/plain; charset=GBK");
		PrintWriter out = response.getWriter();
		
		String rtString = "";
		boolean isLogin = isLogin(request, response); // request.getRemoteUser()!=null;
		try {
			request.setCharacterEncoding("GBK");
			// ��ȡrequest��
			byte[] bytes = StreamUtil.inputStreamToByteArray(request.getInputStream());
			String postString = new String( bytes, "utf-8" );
			debug("<RemoteCallServlet>.doPost() -> "+postString);
			
			// ����ȡ����json����ת��Ϊjson����
			JSONObject obj = (JSONObject)JSONValue.parse(postString);
			if( obj==null ){
				throw new IllegalDataException("�޷�����json�ַ���, json:["+postString+"]");
			}
			Boolean async = (Boolean)obj.get("async");
			String className = (String)obj.get("className");
			String methodName = (String)obj.get("methodName");
			JSONArray methodParameters = (JSONArray)obj.get("methodParameters");
			
			debug("[RemoteCall(async:"+async+")] "+className+":"+methodName+"(...)");
			
			Class<?> klass = null;
			Class<?>[] methodParameterTypes = null;
			Method method = null;
			try { //��ȡҪ���õĺ���
				klass = Class.forName(className);
				methodParameterTypes = new Class[methodParameters.size()];
				for( int i=0; i<methodParameterTypes.length; i++ ){
					methodParameterTypes[i] = methodParameters.get(i).getClass();
				}
				method = klass.getMethod(methodName, methodParameterTypes);
			} catch (NoSuchMethodException e) {
				// �Ҳ���Ҫ���õķ���, ����Һ���ǩ�����ݵķ���
				method = getCompatibleMethod( klass, methodName, methodParameters );
				if( method!=null ){
					debug( "���ҵ��ļ��ݷ����ĺ���ǩ��Ϊ: "+toMethodDescription(method) );
					// ���в���������ת��
					transferParameterTypes( method.getParameterTypes(), methodParameters );
				} else {
					debug( "�Ҳ�������ǩ�����ݵķ���:"+e.getMessage() );
					printError(e);
					rtString = JSONUtil.toJSONString(toExceptionReply(e));
					return ;
				}
			} catch (Exception e) {
				printError(e);
				rtString = JSONUtil.toJSONString(toExceptionReply(e));
				return ;
			}
			try { // ���ò��ҵ��ĺ���
				// �����������ܲ��ܱ�����
				if( !checkMethodCallable(method, isLogin) ){
					throw new RuntimeException("�÷��������¼ϵͳ�Ժ���ܵ���, method:["+toMethodDescription(method)+"]");
				}
				long time2 = System.nanoTime();
				debug("���ҷ���ʹ����["+((time2-time1)/1000000.0)+"]����");
				Object rtObj = method.invoke(klass.newInstance(), methodParameters.toArray()) ;
				rtString = JSONUtil.toJSONString(toSuccessReply(rtObj));
				return ;
			} catch (InvocationTargetException e) {
				// �����õ��������淢�����쳣
				Throwable targetException = e.getTargetException();
				printError(targetException);
				rtString = JSONUtil.toJSONString(toExceptionReply(e.getTargetException()));
				return ;
			} catch (Exception e) {
				// �����쳣
				printError(e);
				rtString = JSONUtil.toJSONString(toExceptionReply(e));
				return ;
			}
		} catch(Exception e) {
			printError(e);
			rtString = JSONUtil.toJSONString(toExceptionReply(e));
		} finally {
			out.print(rtString);
		}
	}
	
	/*
	 * ��ȡ������ͬ,����������ͬ�ķ���
	 */
	public static List<Method> getSameNameMethod( Class<?> klass, String methodName, JSONArray parameters ){
		Method[] methods = klass.getMethods();
		List<Method> sameNameMethod = new ArrayList<Method>();
		for( int i=0; i<methods.length; i++ ){
			Method method = methods[i];
			if( method.getName().equals(methodName) && method.getParameterTypes().length==parameters.size() ){
				sameNameMethod.add(method);
			}
		}
		return sameNameMethod;
	}
	
	/*
	 * ���Ҽ��ݺ���ǩ���ķ���
	 */
	public static Method getCompatibleMethod( Class<?> klass, String methodName, JSONArray parameters ){
		List<Method> sameNameMethod = getSameNameMethod( klass, methodName, parameters );
		loop1:for( int i=0; i<sameNameMethod.size(); i++ ){
			Method method = sameNameMethod.get(i);
			Class<?>[] types = method.getParameterTypes();
			for( int j=0; j<types.length; j++ ){
				if( types[j].isInstance(parameters.get(j)) ){
					continue ;	// �ɽ���
				} else if ( types[j]==String.class ) {
					continue ;	// �ɽ���
				} else if ( parameters.get(j).getClass()==Long.class && ( types[j]==long.class || types[j]==int.class ) ) {
					continue ;	// �ɽ���
				} else if ( parameters.get(j).getClass()==Double.class && ( types[j]==double.class || types[j]==float.class ) ) {
					continue ;	// �ɽ���
				} else {
					continue loop1;	// ������һ��method
				}
			}
			return method;
		}
		return null;
	}
	
	/*
	 * 
	 */
	public static void transferParameterTypes(Class<?>[] methodParameterTypes, List methodParameters){
		for( int i=0; i<methodParameterTypes.length; i++ ){
			if( methodParameters.get(i)!=null &&
				methodParameterTypes[i].isInstance(methodParameters.get(i))==false )
			{
				if( methodParameterTypes[i]==String.class ){
					methodParameters.set(i, String.valueOf(methodParameters.get(i)));
				} else if( methodParameterTypes[i]==int.class || methodParameterTypes[i]==Integer.class ){
					methodParameters.set(i, ((Number)methodParameters.get(i)).intValue());
				} else if( methodParameterTypes[i]==float.class || methodParameterTypes[i]==Float.class ){
					methodParameters.set(i, ((Number)methodParameters.get(i)).floatValue());
				}
			}
		}
	}
	
	/*
	 * �����������ܷ񱻵���
	 */
	public boolean checkMethodCallable(Method method, boolean isLogin){
		if( isLogin || (method.getAnnotation(CallWithoutLogin.class)!=null) ){
			return true;
		} else {
			return false;
		}
	}
	
	public static String toMethodDescription( Method method ){
		StringBuffer sb = new StringBuffer();
		sb.append(method.getReturnType().getName()+" "+method.getName()+"(");
		Class<?>[] parameterTypes = method.getParameterTypes();
		for( int i=0; i<parameterTypes.length-1; i++ ){
			sb.append( " "+parameterTypes[i].getName()+"," );
		}
		if( parameterTypes.length>0 ){
			sb.append( " "+parameterTypes[parameterTypes.length-1].getName()+" " );
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static JSONObject toSuccessReply(Object obj){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "success");
		jsonObject.put("result", obj);
		return jsonObject;
	}
	
	public static JSONObject toExceptionReply(Throwable e){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", "exception");
		jsonObject.put("exceptionClass", e.getClass().getName());
		jsonObject.put("exceptionMessage", e.getMessage());
		return jsonObject;
	}
	
}
