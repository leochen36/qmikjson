package org.qmik.qmikjson.token;

import java.util.Map;

import org.qmik.datamap.Data;

/**
 * json string to mep token算法
 * @author leo
 *
 */
public class DataToken extends Token {
	@Override
	@SuppressWarnings("rawtypes")
	protected Object createDataNode(Class<?> clazz) {
		return new Data();
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void add(Object node, String key, Object value) {
		((Map) node).put(key, value);
	}
}
