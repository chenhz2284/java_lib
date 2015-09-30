package chz.common.util.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chz.common.model.MethodResult;
import chz.common.util.bean.BeanUtil;

public class ObjectUtil {

	/*
	 * ���Ҽ��ݺ���ǩ���ķ���
	 */
	public static MethodResult getCompatibleMethod( Class<?> klass, String methodName, List<?> comeParameters ){
		// ��ȡ������ͬ,����������ͬ�ķ���
		List<Method> sameNameMethod = getSameNameMethod( klass, methodName, comeParameters );
		// �жϷ����Ĳ������͸�ʵ����Ҫ�Ĳ����������Ƿ����
		for( int i=0; i<sameNameMethod.size(); i++ ){
			Method queryMethod = sameNameMethod.get(i);
			Class<?>[] queryTypes = queryMethod.getParameterTypes();
			Object[] transformParameters = new Object[comeParameters.size()];	// ����ҵ����ݵķ���,˳�㽫�������ͽ���ת��
			boolean bAccept = true;
			for( int j=0; j<queryTypes.length; j++ ){
				// ʵ�������ǲ������͵�ʵ��
				if( queryTypes[j].isInstance(comeParameters.get(j)) ){
					transformParameters[j] = comeParameters.get(j);
					continue ;	// �ɽ���
				}
				// ����������String
				else if ( queryTypes[j]==String.class ) {
					transformParameters[j] = String.valueOf(comeParameters.get(j));
					continue ;	// �ɽ���
				}
				// ʵ��������Long, �������Ϳ�����long
				else if ( comeParameters.get(j).getClass()==Long.class && queryTypes[j]==long.class ) {
					transformParameters[j] = comeParameters.get(j);
					continue ;	// �ɽ���
				}
				// ʵ��������Long, �������Ϳ�����int��Integer
				else if ( comeParameters.get(j).getClass()==Long.class && ( queryTypes[j]==int.class || queryTypes[j]==Integer.class ) ) {
					transformParameters[j] = ((Number)comeParameters.get(j)).intValue();
					continue ;	// �ɽ���
				}
				// ʵ��������Double, �������Ϳ�����double
				else if ( comeParameters.get(j).getClass()==Double.class && queryTypes[j]==double.class ) {
					transformParameters[j] = comeParameters.get(j);
					continue ;	// �ɽ���
				}
				// ʵ��������Double, �������Ϳ�����float����Float
				else if ( comeParameters.get(j).getClass()==Double.class && ( queryTypes[j]==float.class || queryTypes[j]==Float.class ) ) {
					transformParameters[j] = ((Number)comeParameters.get(j)).floatValue();
					continue ;	// �ɽ���
				}
				// ʵ��������Map, �������Ϳ����Ǹ�Map���ݵ�JavaBean, map��key��JavaBean�����Զ�Ӧ
				else if ( comeParameters.get(j) instanceof Map ) {
					try {		// ������������һ��Mapʵ��, ��ô�����ܷ�ת����һ��JavaBean
						Object bean = BeanUtil.mapToBean( (Map<?, ?>)comeParameters.get(j), queryTypes[j]);
						transformParameters[j] = bean;
						continue ;	// �ɽ���
					} catch (Exception e) {
						bAccept = false;
						break;	// ������һ��method
					}
				}
				// �������ϵ����֮һ, ����������һ������
				else {
					bAccept = false;
					break;	// ������һ��method
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
	 * ��ȡ������ͬ,����������ͬ�ķ���
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
	 * �ж�һ�����Ƿ�����һ����ĸ���
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
	 * �ж�һ�����Ƿ�����һ����ı������
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