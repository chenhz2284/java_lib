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
			// 获取request流
			byte[] bytes = StreamUtil.inputStreamToByteArray(request.getInputStream());
			String postString = new String( bytes, "utf-8" );
			debug("<RemoteCallServlet>.doPost() -> "+postString);
			
			// 将获取到的json数据转换为json对象
			JSONObject obj = (JSONObject)JSONValue.parse(postString);
			if( obj==null ){
				throw new IllegalDataException("无法处理json字符串, json:["+postString+"]");
			}
			Boolean async = (Boolean)obj.get("async");
			String className = (String)obj.get("className");
			String methodName = (String)obj.get("methodName");
			JSONArray methodParameters = (JSONArray)obj.get("methodParameters");
			
			debug("[RemoteCall(async:"+async+")] "+className+":"+methodName+"(...)");
			
			Class<?> klass = null;
			Class<?>[] methodParameterTypes = null;
			Method method = null;
			try { //获取要调用的函数
				klass = Class.forName(className);
				methodParameterTypes = new Class[methodParameters.size()];
				for( int i=0; i<methodParameterTypes.length; i++ ){
					methodParameterTypes[i] = methodParameters.get(i).getClass();
				}
				method = klass.getMethod(methodName, methodParameterTypes);
			} catch (NoSuchMethodException e) {
				// 找不到要调用的方法, 则查找函数签名兼容的方法
				method = getCompatibleMethod( klass, methodName, methodParameters );
				if( method!=null ){
					debug( "查找到的兼容方法的函数签名为: "+toMethodDescription(method) );
					// 进行参数的类型转换
					transferParameterTypes( method.getParameterTypes(), methodParameters );
				} else {
					debug( "找不到函数签名兼容的方法:"+e.getMessage() );
					printError(e);
					rtString = JSONUtil.toJSONString(toExceptionReply(e));
					return ;
				}
			} catch (Exception e) {
				printError(e);
				rtString = JSONUtil.toJSONString(toExceptionReply(e));
				return ;
			}
			try { // 调用查找到的函数
				// 检测这个方法能不能被调用
				if( !checkMethodCallable(method, isLogin) ){
					throw new RuntimeException("该方法必须登录系统以后才能调用, method:["+toMethodDescription(method)+"]");
				}
				long time2 = System.nanoTime();
				debug("查找方法使用了["+((time2-time1)/1000000.0)+"]毫秒");
				Object rtObj = method.invoke(klass.newInstance(), methodParameters.toArray()) ;
				rtString = JSONUtil.toJSONString(toSuccessReply(rtObj));
				return ;
			} catch (InvocationTargetException e) {
				// 被调用的数据里面发生了异常
				Throwable targetException = e.getTargetException();
				printError(targetException);
				rtString = JSONUtil.toJSONString(toExceptionReply(e.getTargetException()));
				return ;
			} catch (Exception e) {
				// 其它异常
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
	 * 获取名称相同,参数个数相同的方法
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
	 * 查找兼容函数签名的方法
	 */
	public static Method getCompatibleMethod( Class<?> klass, String methodName, JSONArray parameters ){
		List<Method> sameNameMethod = getSameNameMethod( klass, methodName, parameters );
		loop1:for( int i=0; i<sameNameMethod.size(); i++ ){
			Method method = sameNameMethod.get(i);
			Class<?>[] types = method.getParameterTypes();
			for( int j=0; j<types.length; j++ ){
				if( types[j].isInstance(parameters.get(j)) ){
					continue ;	// 可接受
				} else if ( types[j]==String.class ) {
					continue ;	// 可接受
				} else if ( parameters.get(j).getClass()==Long.class && ( types[j]==long.class || types[j]==int.class ) ) {
					continue ;	// 可接受
				} else if ( parameters.get(j).getClass()==Double.class && ( types[j]==double.class || types[j]==float.class ) ) {
					continue ;	// 可接受
				} else {
					continue loop1;	// 查找下一个method
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
	 * 检测这个方法能否被调用
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
