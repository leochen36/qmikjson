package org.qmik.qmikjson.out;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
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
public class Bean2Text {
	private static ThreadLocal<CharWriter>		gtl_writers	= new ThreadLocal<CharWriter>() {
																				protected CharWriter initialValue() {
																					return new CharWriter(4098);
																				};
																			};
	private final static Map<String, String>	getMethods	= new HashMap<String, String>(1024);
	
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
				Field[] fields = bean.getClass().getDeclaredFields();
				for (Field field : fields) {
					try {
						name = field.getName();
						methodName = getMethods.get(name);
						if (methodName == null) {
							methodName = "get" + MixUtil.indexUpper(name, 0);
							getMethods.put(name, methodName);
						}
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
	
	private static void append(CharWriter writer, String name, Object value, DateFormat df) throws IOException {
		if (MixUtil.isPrimitive(value.getClass())) {
			writer.append('"').append(name).append("\":").append(value.toString());
		} else {
			if (value instanceof Date) {
				if (df == null) {
					writer.append('"').append(name).append("\":").append(((Date) value).getTime() + "");
				} else {
					writer.append('"').append(name).append("\":\"").append(df.format(value)).append("\"");
				}
			} else {
				writer.append('"').append(name).append("\":\"").append(value.toString()).append("\"");
			}
		}
	}
}
