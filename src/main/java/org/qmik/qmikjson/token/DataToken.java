package org.qmik.qmikjson.token;

import org.qmik.datamap.Data;

/**
 * json string to mep token算法
 * @author leo
 *
 */
public class DataToken extends Token {
	@Override
	@SuppressWarnings("rawtypes")
	protected Object createDataNode(Class<?> root, Object parent, String field) {
		return new Data();
	}
	
}
