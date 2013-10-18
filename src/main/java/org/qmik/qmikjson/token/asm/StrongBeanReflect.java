package org.qmik.qmikjson.token.asm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class StrongBeanReflect {
	
	private final static Map<Class<?>, Field[]>	caches	= new HashMap<Class<?>, Field[]>(1024);
	
	public static Field get(Class<?> clazz, String fieldName) {
		Field[] fields = get(clazz);
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}
	
	public static Field[] get(Class<?> clazz) {
		Field[] fields = caches.get(clazz);
		if (fields == null) {
			if (clazz.getSuperclass() == Object.class) {//没有继承其它父类,取自己定义的
				fields = clazz.getDeclaredFields();
			} else {
				Field[] tmps = clazz.getSuperclass().getDeclaredFields();
				Map<String, Field> map = new HashMap<String, Field>(tmps.length * 2);
				for (Field field : tmps) {
					map.put(field.getName(), field);
				}
				tmps = clazz.getDeclaredFields();
				for (Field field : tmps) {
					map.put(field.getName(), field);
				}
				fields = new Field[map.size()];
				int i = 0;
				for (Field field : map.values()) {
					fields[i++] = field;
				}
			}
			caches.put(clazz, fields);
		}
		return fields;
	}
	
}
