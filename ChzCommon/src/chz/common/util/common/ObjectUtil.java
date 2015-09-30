package chz.common.util.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chz.common.model.MethodResult;
import chz.common.util.bean.BeanUtil;

public class ObjectUtil {

	/*
	 * 查找兼容函数签名的方法
	 */
	public static MethodResult getCompatibleMethod( Class<?> klass, String methodName, List<?> comeParameters ){
		// 获取名称相同,参数个数相同的方法
		List<Method> sameNameMethod = getSameNameMethod( klass, methodName, comeParameters );
		// 判断方法的参数类型跟实际需要的参数的类型是否兼容
		for( int i=0; i<sameNameMethod.size(); i++ ){
			Method queryMethod = sameNameMethod.get(i);
			Class<?>[] queryTypes = queryMethod.getParameterTypes();
			Object[] transformParameters = new Object[comeParameters.size()];	// 如果找到兼容的方法,顺便将参数类型进行转换
			boolean bAccept = true;
			for( int j=0; j<queryTypes.length; j++ ){
				// 实际类型是参数类型的实例
				if( queryTypes[j].isInstance(comeParameters.get(j)) ){
					transformParameters[j] = comeParameters.get(j);
					continue ;	// 可接受
				}
				// 参数类型是String
				else if ( queryTypes[j]==String.class ) {
					transformParameters[j] = String.valueOf(comeParameters.get(j));
					continue ;	// 可接受
				}
				// 实际类型是Long, 参数类型可以是long
				else if ( comeParameters.get(j).getClass()==Long.class && queryTypes[j]==long.class ) {
					transformParameters[j] = comeParameters.get(j);
					continue ;	// 可接受
				}
				// 实际类型是Long, 参数类型可以是int或Integer
				else if ( comeParameters.get(j).getClass()==Long.class && ( queryTypes[j]==int.class || queryTypes[j]==Integer.class ) ) {
					transformParameters[j] = ((Number)comeParameters.get(j)).intValue();
					continue ;	// 可接受
				}
				// 实际类型是Double, 参数类型可以是double
				else if ( comeParameters.get(j).getClass()==Double.class && queryTypes[j]==double.class ) {
					transformParameters[j] = comeParameters.get(j);
					continue ;	// 可接受
				}
				// 实际类型是Double, 参数类型可以是float或者Float
				else if ( comeParameters.get(j).getClass()==Double.class && ( queryTypes[j]==float.class || queryTypes[j]==Float.class ) ) {
					transformParameters[j] = ((Number)comeParameters.get(j)).floatValue();
					continue ;	// 可接受
				}
				// 实际类型是Map, 参数类型可以是跟Map兼容的JavaBean, map的key和JavaBean的属性对应
				else if ( comeParameters.get(j) instanceof Map ) {
					try {		// 如果这个参数是一个Map实例, 那么看它能否转换成一个JavaBean
						Object bean = BeanUtil.mapToBean( (Map<?, ?>)comeParameters.get(j), queryTypes[j]);
						transformParameters[j] = bean;
						continue ;	// 可接受
					} catch (Exception e) {
						bAccept = false;
						break;	// 查找下一个method
					}
				}
				// 不是以上的情况之一, 继续查找下一个方法
				else {
					bAccept = false;
					break;	// 查找下一个method
				}
			}
			if( bAccept ){
				MethodResult result = new MethodResult();
				result.put("method", queryMethod);
				result.put("transformParameters", transformParameters);
				return result;
			} else {
				continue;
			}
		}
		return null;
	}
	
	/*
	 * 获取名称相同,参数个数相同的方法
	 */
	public static List<Method> getSameNameMethod( Class<?> klass, String methodName, List<?> parameters ){
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
	 * 判断一个类是否是另一个类的父类
	 */
	public static boolean isSuperClass( Class<?> superClass, Class<?> subClass ){
		if( superClass==null || subClass==null ){
			throw new NullPointerException();
		}
		Class<?> tempClass = subClass.getSuperclass();
		while( tempClass!=null ){
			if( superClass==tempClass ){
				return true;
			}
			tempClass = tempClass.getSuperclass();
		}
		return false;
	}
	
	/*
	 * 判断一个类是否是另一个类的本类或父类
	 */
	public static boolean isEqualOrSuperClass( Class<?> superClass, Class<?> subClass ){
		if( superClass==null || subClass==null ){
			throw new NullPointerException();
		}
		if( superClass==subClass ){
			return true;
		} else {
			return isSuperClass(superClass, subClass);
		}
	}
	
	public static void main(String[] args) {
		Float f = 11.1f;
		System.out.println( ((Number)f).intValue() );
	}
	
}