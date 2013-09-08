package org.qmik.qmikjson.util;

import java.lang.reflect.Method;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;

public class BeanUtil {
	public final static Object[]		NULLS			= new Object[] {};
	public final static Class<?>[]	NULLS_CLASS	= new Class[] {};
	
	public static Object invoke(Object target, Method mothod) {
		try {
			return mothod.invoke(target, NULLS);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Object invokeGet(Object target, String mothodName) {
		try {
			
			return invoke(target, target.getClass().getDeclaredMethod(mothodName, NULLS_CLASS));
		} catch (Exception e) {
		}
		return null;
	}
	
	public static <T> T toIBean(T value) {
		if (value instanceof IBean) {
			return value;
		}
		T newValue = StrongBeanFactory.get(value.getClass());
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
