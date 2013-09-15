package org.qmik.qmikjson.token.asm;

import java.lang.reflect.Field;
import java.util.HashMap;
import org.qmik.qmikjson.token.IBean;

/**
 * 增强bean类
 * @author leo
 *
 */
public class StrongBeanFactory {
	
	private final static StrongBeanClump			clump	= new StrongBeanClump();
	private final static StrongBean					bean	= new StrongBean();
	private final static HashMap<String, String>	names	= new HashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz, Object target) {
		if (target instanceof IBean) {
			return (T) target;
		}
		IBean bean = get(superClazz);
		bean.$$$___setTarget(target);
		return (T) bean;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz) {
		T value = null;
		try {
			if (IBean.class.isAssignableFrom(superClazz)) {
				return (T) superClazz.newInstance();
			}
			String key = (String) names.get(superClazz.getName());
			if (key == null) {
				key = superClazz.getName() + StrongBean.suffix;
				names.put(superClazz.getName(), key);
			}
			Class<?> strongClass = null;
			
			if (clump.get(key) != null) {
				strongClass = clump.get(key);
			} else {
				synchronized (clump) {
					if (clump.get(key) == null) {
						strongClass = bean.makeClass(superClazz, IBean.class);
						value = (T) strongClass.newInstance();
						IBean bean = (IBean) value;
						Field[] fields = superClazz.getDeclaredFields();
						for (int i = 0; i < fields.length; i++) {
							bean.$$$___keys().add(fields[i].getName());
							bean.$$$___addFieldType(fields[i].getName(), fields[i].getType());
						}
						clump.add(key, strongClass);
					}
				}
				return value;
			}
			value = (T) strongClass.newInstance();
		} catch (Exception e) {
			throw new NewInstanceException(e);
		}
		return value;
	}
}
