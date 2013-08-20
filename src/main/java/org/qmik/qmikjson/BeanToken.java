package org.qmik.qmikjson;

import java.util.Map;
import org.qmik.datamap.Data;

public class BeanToken extends Token {
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Object createDataNode() {
		return new Data();
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void add(Object node, String key, Object value) {
		((Map) node).put(key, value);
	}
	
}
