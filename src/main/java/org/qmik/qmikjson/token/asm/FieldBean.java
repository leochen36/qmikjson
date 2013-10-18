package org.qmik.qmikjson.token.asm;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.qmik.qmikjson.out.CharWriter;
import org.qmik.qmikjson.util.MixUtil;

public class FieldBean implements IStrongBean {
	private final static String								lock			= new String("lock");
	private Map<String, Object>								values		= new HashMap<String, Object>();
	private final static Map<String, Class<?>>			classs		= new HashMap<String, Class<?>>();
	private int														hash			= values.hashCode();
	public Map<Object, CharWriter>							outer			= new HashMap<Object, CharWriter>();
	public Map<Object, CharWriter>							outerMulMix	= new HashMap<Object, CharWriter>();
	private boolean												mulMix		= false;
	private Object													target;
	
	private static Reference<Map<Object, FieldBean>>	caches		= new SoftReference<Map<Object, FieldBean>>(new HashMap<Object, FieldBean>());
	
	private static Map<Object, FieldBean> getMap() {
		Map<Object, FieldBean> cache = caches.get();
		if (cache == null) {
			synchronized (lock) {
				if (cache == null) {
					cache = new HashMap<Object, FieldBean>();
					caches = new SoftReference<Map<Object, FieldBean>>(cache);
				}
			}
		}
		return cache;
	}
	
	private static FieldBean getCache(Object obj) {
		return getMap().get(obj);
	}
	
	private static void setCache(Object target, FieldBean infos) {
		getMap().put(target, infos);
	}
	
	private FieldBean(Object obj) {
		$$$___setTarget(obj);
	}
	
	public static FieldBean getInstance(Object obj) {
		FieldBean bean = getCache(obj);
		if (bean == null) {
			bean = new FieldBean(obj);
			setCache(obj, bean);
		}
		return bean;
	}
	
	public Object getValue(String name) {
		return values.get(name);
	}
	
	public void setValue(String name, Object value) {
		values.put(name, value);
		if (!MixUtil.isUnitType(value.getClass())) {
			mulMix = true;
		}
	}
	
	@Override
	public int $$$___hash() {
		return hash;
	}
	
	@Override
	public void $$$___setValue(String name, Object value) {
		if (value == null) {
			return;
		}
		hash++;
		values.put(name, value);
	}
	
	@Override
	public Object $$$___getValue(String name) {
		return values.get(name);
	}
	
	@Override
	public List<String> $$$___keys() {
		return new ArrayList<String>(values.keySet());
	}
	
	@Override
	public Map<String, Class<?>> $$$___fieldTypes() {
		return classs;
	}
	
	@Override
	public void $$$___setTarget(Object target) {
		this.target = target;
		Class<?> clazz = target.getClass();
		Field[] fields = clazz.getDeclaredFields();
		addValue(fields);
		fields = clazz.getFields();
		addValue(fields);
	}
	
	private void addValue(Field[] fields) {
		String name;
		Object value;
		for (Field field : fields) {
			try {
				name = field.getName();
				classs.put(name, field.getType());
				if (name.indexOf("<init>") >= 0) {
					continue;
				}
				if (!values.containsKey(name)) {
					value = FiledInfoUtil.getFieldValue(target, name);
					if (value != null) {
						classs.put(name, field.getType());
						//values.put(name, );
						$$$___setValue(name, FiledInfoUtil.getFieldValue(target, name));
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public boolean $$$___compare() {
		if (hash != values.hashCode()) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean $$$___isMulMix() {
		
		return mulMix;
	}
	
	@Override
	public CharWriter $$$___getOuter(Object key) {
		
		return outer.get(key);
	}
	
	@Override
	public CharWriter $$$___getOuterMulMix(Object key) {
		
		return outerMulMix.get(key);
	}
	
	@Override
	public boolean $$$___existOuter(Object key) {
		
		return outer.containsKey(key);
	}
	
	@Override
	public boolean $$$___existOuterMulMix(Object key) {
		
		return outerMulMix.containsKey(key);
	}
	
	@Override
	public void $$$___setOuter(Object key, CharWriter outer) {
		this.outer.put(key, outer);
	}
	
	@Override
	public void $$$___setOuterMulMix(Object key, CharWriter outer) {
		this.outerMulMix.put(key, outer);
	}
	
	public static class FiledInfoUtil {
		public static Object getFieldValue(Object target, String fieldName) {
			if (target == null) {
				return null;
			}
			try {
				Class<?> clazz = target.getClass();
				Field field = clazz.getDeclaredField(fieldName);
				if (field == null) {
					field = clazz.getField(fieldName);
				}
				field.setAccessible(true);
				return field.get(target);
			} catch (Exception e) {
			}
			return null;
		}
		
	}
	
}
