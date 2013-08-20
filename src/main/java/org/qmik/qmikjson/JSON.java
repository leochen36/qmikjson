package org.qmik.qmikjson;

import java.util.Collection;
import java.util.Map;

import org.qmik.qmikjson.out.Data2Text;

public class JSON {
	private final static JSONParse	parse	= new JSONParse();
	
	public static Object parse(String json) {
		return parse.parse(json);
	}
	
	public static Object parse(String json, Class<?> clazz) {
		return parse.parse(json, clazz);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String toJSONString(Object obj) {
		if (obj instanceof Map) {
			return Data2Text.map2JSON((Map) obj);
		}
		if (obj instanceof Collection) {
			return Data2Text.list2JSON((Collection) obj);
		}
		return null;
	}
	
}
