package org.qmik.qmikjson;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.qmik.qmikjson.error.ClassNoPublicException;
import org.qmik.qmikjson.token.asm.IStrongBean;
import org.qmik.qmikjson.token.asm.NewInstanceException;
import org.qmik.qmikjson.token.asm.MakeStrongBean;
import org.qmik.qmikjson.token.asm.StrongBeanClump;
import org.qmik.qmikjson.util.MixUtil;

/**
 * 增强bean类
 * @author leo
 *
 */
public class StrongBeanFactory {
	
	private final static StrongBeanClump					clump		= new StrongBeanClump();
	private final static MakeStrongBean						bean		= new MakeStrongBean();
	private final static HashMap<String, String>			names		= new HashMap<String, String>();
	private static Reference<Map<Object, IStrongBean>>	caches	= new SoftReference<Map<Object, IStrongBean>>(new HashMap<Object, IStrongBean>());
	
	private static Map<Object, IStrongBean> getMap() {
		Map<Object, IStrongBean> map = caches.get();
		if (map == null) {
			synchronized (clump) {
				if (map == null) {
					map = new HashMap<Object, IStrongBean>();
					caches = new SoftReference<Map<Object, IStrongBean>>(map);
				}
			}
		}
		return map;
	}
	
	private static IStrongBean getCache(Object target) {
		return getMap().get(target);
	}
	
	private static void setCache(Object target, IStrongBean ibean) {
		getMap().put(target, ibean);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz, Object target) {
		if (target == null || superClazz == null) {
			return null;
		}
		if (target instanceof IStrongBean) {
			return (T) target;
		}
		IStrongBean bean = getCache(target);
		if (bean == null) {
			bean = get(superClazz);
			bean.$$$___setTarget(target);
			setCache(target, bean);
		}
		return (T) bean;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> superClazz) {
		if (superClazz == null) {
			return null;
		}
		T value = null;
		try {
			///如果类访问级别不是共公的,返回空
			if (!Modifier.isPublic(superClazz.getModifiers())) {
				throw new ClassNoPublicException("class access is not public!");
			}
			//非法的类型
			if (MixUtil.isUnitType(superClazz) || Collection.class.isAssignableFrom(superClazz) || Map.class.isAssignableFrom(superClazz)) {
				throw new IllegalAccessError("superClazz type is Illegal");
			}
			if (IStrongBean.class.isAssignableFrom(superClazz)) {
				return (T) superClazz.newInstance();
			}
			String key = (String) names.get(superClazz.getName());
			if (key == null) {
				key = superClazz.getName() + MakeStrongBean.suffix;
				names.put(superClazz.getName(), key);
			}
			Class<?> strongClass = clump.get(key);
			
			if (strongClass == null) {
				
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
