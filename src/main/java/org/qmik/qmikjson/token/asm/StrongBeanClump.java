package org.qmik.qmikjson.token.asm;

import java.util.HashMap;
import java.util.Map;

/**
 * 增加子类集合
 * @author leo
 *
 */
public class StrongBeanClump {
	
	private final static Map<String, Class<?>>	map	= new HashMap<String, Class<?>>();
	
	public void add(String name, Class<?> clazz) {
		map.put(name, clazz);
	}
	
	public Class<?> get(String name) {
		return map.get(name);
	}
	
}
