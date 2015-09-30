package chz.common.util.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class BeanUtil<T> {

	/*
	 * ��mapת��Ϊbean
	 */
	public static Object mapToBean(Map<?, ?> map, Class<?> klass) throws IntrospectionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		// ��public ����
		Field[] fields = klass.getFields();
		for( int i=0; i<fields.length; i++ ){
			Field field = fields[i];
			if( !Modifier.isStatic(field.getModifiers()) ){
				// public���Է�һ��Field
				propertyMap.put(fields[i].getName(), field);
			}
		}
		// ��private���Զ�Ӧ�ķ���
		BeanInfo beanInfo = Introspector.getBeanInfo(klass);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for( int i=0; i<propertyDescriptors.length; i++ ){
			PropertyDescriptor property = propertyDescriptors[i];
			Method writeMethod = property.getWriteMethod();
			if( writeMethod!=null ){
				// ��public���Է�һ��writeMethod
				propertyMap.put(property.getName(), writeMethod);
			}
		}
		// ��ʵ����һ��JavaBean��ʵ��
		Object bean = klass.newInstance();
		Iterator<?> iter = map.entrySet().iterator();
		while( iter.hasNext() ){
			Entry<?,?> entry = (Entry<?,?>)iter.next();
			String key = ""+entry.getKey();
			Object value = entry.getValue();
			// ����������
			if( "".equals(key) ){	
				return null;
			}
			Object obj = propertyMap.get(key);
			// û�ж�Ӧ������
			if( obj==null ){		
				return null;
			}
			// ��Ӧ������Field
			else if( obj instanceof Field ){
				((Field)obj).set(bean, value);
			}
			// ��Ӧ������Method
			else if( obj instanceof Method ){
				Method writeMethod = ((Method)obj);
				Class<?> methodType = writeMethod.getParameterTypes()[0];
				// Class<?> valueType = value.getClass();
				// Method�Ĳ���������int
				if( (value instanceof Number) && (methodType==int.class || methodType==Integer.class) ){
					writeMethod.invoke(bean, ((Number)value).intValue());
				}
				// Method�Ĳ���������float
				else if( (value instanceof Number) && (methodType==float.class || methodType==Float.class) ){
					writeMethod.invoke(bean, ((Number)value).floatValue());
				}
				// Method�Ĳ�������������������
				else {
					writeMethod.invoke(bean, value);
				}
			}
		}
		return bean;
	}
	
	/*
	 * 
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> klass) throws IntrospectionException{
		Map<String, PropertyDescriptor> resultMap = new TreeMap<String, PropertyDescriptor>();
		BeanInfo beanInfo = Introspector.getBeanInfo(klass);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for( int i=0; i<propertyDescriptors.length; i++ ){
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			resultMap.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		return resultMap;
	}
	
	/*
	 * 
	 */
	public static Object getPropertyValue(PropertyDescriptor propertyDescriptor, Object instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method readMethod = propertyDescriptor.getReadMethod();
		if( readMethod!=null ){
			return readMethod.invoke(instance);
		} else {
			return null;
		}
	}
	
	/**
	 * ��JavaBean���б�����
	 * @param klass: JavaBean��Class
	 * @param list: JavaBean��list
	 * @param sortProperties: ��Ҫ�����JavaBean������
	 */
	public static <T> void sortBeanList(Class<T> klass, List<T> list, final SortProperty[] sortProperties) throws Exception{
		final Method[] propertyReadMethods = new Method[sortProperties.length];
		// ��ȡÿһ�����Ե�read method
		Map<String, PropertyDescriptor> propertiesMap = getPropertyDescriptorMap(klass);
		for( int i=0; i<sortProperties.length; i++ ){
			String propertyName = sortProperties[i].getProperty();
			PropertyDescriptor propertyDescriptor = propertiesMap.get(propertyName);
			if( propertyDescriptor==null ){
				throw new RuntimeException("not property ["+propertyName+"]");
			}
			Method readMethod = propertyDescriptor.getReadMethod();
			if( readMethod==null ){
				throw new RuntimeException("not property read method ["+propertyName+"]");
			}
			propertyReadMethods[i] = readMethod;
		}
		// ����
		Collections.sort(list, new Comparator<T>(){
			public int compare(Object o1, Object o2) {
				try {
					if( o1==null ){
						if( o2==null ){
							return 0;
						} else {
							return 1;	// o1==null,o2!=null,��λ,��null�ں���
						}
					}
					if( o2==null ){
						return -1;		// o1!=null,o2==null,����λ
					}
					for( int i=0; i<sortProperties.length; i++){
						SortProperty sortProperty = sortProperties[i];
						Method readMethod = propertyReadMethods[i];
						Comparable propertyValue1 = (Comparable<?>)readMethod.invoke(o1);
						Comparable propertyValue2 = (Comparable<?>)readMethod.invoke(o2);
						if( propertyValue1==null ){
							if( propertyValue2==null ){
								return 0;
							} else {
								return 1;	// null�ں���,��null��
							}
						}
						if( propertyValue2==null ){	// propertyValue1�϶���Ϊnull
							return -1;
						}
						int compValue = propertyValue1.compareTo(propertyValue2);
						if( compValue!=0 ){
							return compValue * sortProperty.getSortType();
						} else {
							continue;
						}
					}
					return 0;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}); 
	}
	
}
