package org.qmik.qmikjson.out;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.BeanUtil;

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
	private final static Map<Class<?>, Method[]>	sg_methods	= new HashMap<Class<?>, Method[]>(1024);
	
	private static Bean2Text							instance		= new Bean2Text();
	
	private Bean2Text() {
	}
	
	public static Bean2Text getInstance() {
		return instance;
	}
	
	private static Method[] getMethods(Class<?> clazz) {
		Method[] methods = sg_methods.get(clazz);
		if (methods == null) {
			methods = clazz.getDeclaredMethods();
			List<Method> list = new ArrayList<Method>();
			for (Method method : methods) {
				if (!method.getName().startsWith("get")) {
					continue;
				}
				if (method.getParameterTypes().length > 0) {
					continue;
				}
				list.add(method);
			}
			methods = list.toArray(new Method[list.size()]);
			sg_methods.put(clazz, methods);
		}
		return methods;
	}
	
	public String toJSONString(Object bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	public String toJSONString(Object bean, DateFormat df) {
		CharWriter writer = gtl_writers.get();
		writer.clear();
		toStringWriter(writer, bean, df);
		return writer.toString();
	}
	
	@Override
	protected void toStringWriter(CharWriter writer, Object bean, DateFormat df) {
		try {
			Object value;
			boolean gtOne = false;
			writer.append('{');
			if (bean instanceof IBean) {
				IBean ib = (IBean) bean;
				List<String> keys = ib.$$$___keys();
				String name;
				for (int i = 0; i < keys.size(); i++) {
					name = keys.get(i);
					value = ib.$$$___getValue(name);
					if (value == null) {
						continue;
					}
					if (gtOne) {
						writer.append(',');
					}
					append(writer, bean, name, value, df);
					gtOne = true;
				}
			} else {
				String name;
				Method[] methods = getMethods(bean.getClass());
				for (int i = 0; i < methods.length; i++) {
					try {
						name = methods[i].getName();
						value = BeanUtil.invokeGet(bean, name);
						if (value == null) {
							continue;
						}
						if (gtOne) {
							writer.append(',');
						}
						append(writer, bean, name, value, df);
						gtOne = true;
					} catch (Exception e) {
					}
				}
			}
			writer.append('}');
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
