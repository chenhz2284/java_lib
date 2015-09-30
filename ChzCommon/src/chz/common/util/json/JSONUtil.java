package chz.common.util.json;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import chz.common.util.common.DateUtil;
import chz.common.util.common.StringUtil;

public class JSONUtil {

	public static String toJSONString( Object obj ){
		// null
		if( obj==null ){
			return "null";
		}
		// 原生类型
		if( obj instanceof Integer ||
			obj instanceof Long ||
			obj instanceof Short ||
			obj instanceof Byte ||
			obj instanceof Double ||
			obj instanceof Float ||
			obj instanceof Boolean ||
			obj instanceof Character ||
			obj instanceof String )
		{
			return JSONValue.toJSONString(obj);
		}
		// Class
		if( obj instanceof Class )
		{
			return classToJSONString((Class)obj);
		}
		// Date
		if( obj instanceof Date )
		{
			return dateToJSONString((Date)obj);
		}
		// 异常
		if( obj instanceof Throwable ){
			return exceptionToJSONString( (Throwable)obj );
		}
		// 数组
		if( obj.getClass().isArray() ){
			return arrayToJSONString(obj);
		}
		// map
		if( obj instanceof Map )
		{
			return mapToJSONString( (Map<Object, Object>)obj );
		}
		// 迭代器
		if( obj instanceof Iterable )
		{
			return iterableToJSONString( (Iterable)obj );
		}
		// OfflineResultSet
		if( obj instanceof JSONWare )
		{
			return ((JSONWare)obj).toJSONString();
		}
		// JavaBean对象
		if( obj instanceof JSONBean ){
			return beanToJSONString(obj);
		}
		throw new RuntimeException("不能转换为json字符, class:["+(obj.getClass())+"]");
	}
	
	// 异常
	public static String exceptionToJSONString( Throwable e ){
		JSONObject obj = new JSONObject();
		obj.put("exceptionClass", e.getClass().getName());
		obj.put("exceptionMessage", e.getMessage());
		return obj.toJSONString();
	}
	// Date
	public static String dateToJSONString( Date date ){
		return "\""+DateUtil.dateToString(date)+"\"";
	}
	// Class
	public static String classToJSONString( Class klass ){
		return "\""+klass.getName()+"\"";
	}
	// JavaBean
	public static String beanToJSONString( Object obj ){
		try {
			List<String> result = new ArrayList<String>();
			Class klass = obj.getClass();
			// 找public 属性
			Field[] fields = klass.getFields();
			for( int i=0; i<fields.length; i++ ){
				Field field = fields[i];
				if( !Modifier.isStatic(field.getModifiers()) ){
					result.add( toJSONString(fields[i].getName())+":"+toJSONString(fields[i].get(obj)) );
				}
			}
			// 找private属性对应的方法
			BeanInfo beanInfo = Introspector.getBeanInfo(klass);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for( int i=0; i<propertyDescriptors.length; i++ ){
				PropertyDescriptor property = propertyDescriptors[i];
				Method method = property.getReadMethod();
				if( method!=null ){
					result.add( toJSONString(property.getName())+":"+toJSONString(method.invoke(obj)) );
				}
			}
			return new StringBuffer("{").append(StringUtil.join(result)).append("}").toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	// map
	public static String mapToJSONString( Map<Object, Object> map ){
		List<String> result = new ArrayList<String>();
		Set<Entry<Object, Object>> entrySet = map.entrySet();
		for( Iterator<Entry<Object, Object>> it=entrySet.iterator(); it.hasNext(); ){
			Entry<Object, Object> entry = it.next();
			result.add( toJSONString(entry.getKey())+":"+toJSONString(entry.getValue()) );
		}
		return new StringBuffer("{").append(StringUtil.join(result)).append("}").toString();
	}
	// 迭代器
	public static String iterableToJSONString( Iterable<Object> iterable ){
		List<String> result = new ArrayList<String>();
		for( Iterator<Object> it=iterable.iterator(); it.hasNext(); ){
			result.add( toJSONString(it.next()) );
		}
		return new StringBuffer("[").append(StringUtil.join(result)).append("]").toString();
	}
	// 数组
	public static String arrayToJSONString( Object arr ){
		List<String> result = new ArrayList<String>();
		int len = Array.getLength(arr);
		for( int i=0; i<len; i++ ){
			result.add(toJSONString(Array.get(arr, i)));
		}
		return new StringBuffer("[").append(StringUtil.join(result)).append("]").toString();
	}
	
	//--------
	
	public static JSONObject toJSONObject(String jsongString)
	{
		return (JSONObject)JSONValue.parse(jsongString);
	}
	
	public static JSONObject toJSONObject(Reader reader)
	{
		return (JSONObject)JSONValue.parse(reader);
	}
	
	public static JSONObject toJSONObject(byte[] bytes) throws UnsupportedEncodingException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		InputStreamReader reader = new InputStreamReader(bis, "utf-8");
		return (JSONObject)JSONValue.parse(reader);
	}
	
	public static JSONObject toJSONObject(byte[] bytes, String charsetName) throws UnsupportedEncodingException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		InputStreamReader reader = new InputStreamReader(bis, charsetName);
		return (JSONObject)JSONValue.parse(reader);
	}
	
}
