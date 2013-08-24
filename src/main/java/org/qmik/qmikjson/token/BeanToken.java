package org.qmik.qmikjson.token;

import org.qmik.datamap.Data;

public class BeanToken extends Token {
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Object createDataNode(Class<?> clazz) {
		return new Data();
	}
	
	@Override
	protected void add(Object node, String key, Object value) {
		((IBean) node).$$$___setValue(key, value);
	}
	
}
