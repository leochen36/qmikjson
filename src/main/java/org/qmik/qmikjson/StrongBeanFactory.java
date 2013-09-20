package org.qmik.qmikjson;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.qmik.qmikjson.token.asm.IStrongBean;
import org.qmik.qmikjson.token.asm.NewInstanceException;
import org.qmik.qmikjson.token.asm.MakeStrongBean;
import org.qmik.qmikjson.token.asm.StrongBeanClump;

/**
 * 增强bean类
 * @author leo
 *
 */
public class StrongBeanFactory {
	
	private final static StrongBeanClump			clump	= new StrongBeanClump();
	private final static MakeStrongBean				bean	= new MakeStrongBean();
	private final static HashMap<String, String>	names	= new HashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz, Object target) {
		if (target instanceof IStrongBean) {
			return (T) target;
		}
		IStrongBean bean = get(superClazz);
		bean.$$$___setTarget(target);
		return (T) bean;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz) {
		T value = null;
		try {
			if (IStrongBean.class.isAssignableFrom(superClazz)) {
				return (T) superClazz.newInstance();
			}
			String key = (String) names.get(superClazz.getName());
			if (key == null) {
				key = superClazz.getName() + MakeStrongBean.suffix;
				names.put(superClazz.getName(), key);
			}
			Class<?> strongClass = null;
			
			if (clump.get(key) != null) {
				strongClass = clump.get(key);
			} else {
				synchronized (clump) {
					if (clump.get(key) == null) {
						strongClass = bean.makeClass(superClazz, IStrongBean.class);
						value = (T) strongClass.newInstance();
						IStrongBean bean = (IStrongBean) value;
						Field[] fields = superClazz.getDeclaredFields();
						String name = null;
						for (int i = 0; i < fields.length; i++) {
							name = fields[i].getName();
							//bean.$$$___keys().add(name);
							bean.$$$___fieldTypes().put(name, fields[i].getType());
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
