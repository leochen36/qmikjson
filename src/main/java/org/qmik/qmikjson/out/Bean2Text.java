package org.qmik.qmikjson.out;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.BeanUtil;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
public class Bean2Text extends Base2Text {
	private static ThreadLocal<CharWriter>			gtl_writers	= new ThreadLocal<CharWriter>() {
																					protected CharWriter initialValue() {
																						return new CharWriter(4098);
																					};
																				};
	private final static Map<String, String>		sg_methods	= new HashMap<String, String>(1024);
	private final static Map<Class<?>, Field[]>	sg_fields	= new HashMap<Class<?>, Field[]>(1024);
	
	private static Field[] getFields(Class<?> clazz) {
		Field[] fs = sg_fields.get(clazz);
		if (fs != null) {
			return fs;
		}
		fs = clazz.getDeclaredFields();
		sg_fields.put(clazz, fs);
		return fs;
	}
	
	private static String getMethodName(String field) {
		String methodName = sg_methods.get(field);
		if (methodName == null) {
			methodName = "get" + MixUtil.indexUpper(field, 0);
			sg_methods.put(field, methodName);
		}
		return methodName;
	}
	
	public static String toJSONString(Object bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	public static String toJSONString(Object bean, DateFormat df) {
		try {
			CharWriter writer = gtl_writers.get();
			writer.clear();
			IBean ib;
			Object value;
			boolean gtOne = false;
			writer.append('{');
			if (bean instanceof IBean) {
				ib = (IBean) bean;
				Map<String, char[]> keys = ib.$$$___keys();
				for (String name : keys.keySet()) {
					value = ib.$$$___getValue(name);
					if (value == null) {
						continue;
					}
					if (gtOne) {
						writer.append(',');
					}
					append(writer, name, value, df);
					gtOne = true;
				}
			} else {
				//ib = (IBean) BeanUtil.toIBean(bean);
				String name, methodName;
				Field[] fields = getFields(bean.getClass());
				for (Field field : fields) {
					try {
						name = field.getName();
						methodName = getMethodName(name);
						value = BeanUtil.invokeGet(bean, methodName);
						if (value == null) {
							continue;
						}
						if (gtOne) {
							writer.append(',');
						}
						append(writer, name, value, df);
						gtOne = true;
					} catch (Exception e) {
						
					}
				}
			}
			
			writer.append('}');
			return writer.toString();
		} catch (Exception e) {
		}
		return null;
	}
}
