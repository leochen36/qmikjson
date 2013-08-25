package org.qmik.qmikjson.util;

import java.lang.reflect.Method;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;

public class BeanUtil {
	private static Object[]	NULLS	= new Object[] {};
	
	public static <T> T toIBean(T value) {
		if (value instanceof IBean) {
			return value;
		}
		T newValue = StrongBeanFactory.get(value.getClass(), IBean.class);
		IBean nv = (IBean) newValue;
		Method[] methods = value.getClass().getDeclaredMethods();
		String name;
		for (Method method : methods) {
			try {
				name = method.getName();
				if (name.startsWith("get")) {
					nv.$$$___setValue(MixUtil.indexLower(name.substring(3), 0), method.invoke(value, NULLS));
				}
			} catch (Exception e) {
			}
		}
		return newValue;
	}
}
