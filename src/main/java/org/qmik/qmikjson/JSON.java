package org.qmik.qmikjson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.out.Data2Text;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;

public class JSON {
	private static Map<String, DateFormat>	dfs	= new HashMap<String, DateFormat>();
	private final static JSONParse			parse	= new JSONParse();
	
	/** 创建增强对象 */
	public static <T> T makeStrong(Class<T> clazz) {
		return StrongBeanFactory.get(clazz, IBean.class);
	}
	
	public static Object parse(String json) {
		return parse.parse(json);
	}
	
	public static Object parse(String json, Class<?> clazz) {
		return parse.parse(json, clazz);
	}
	
	public static String toJSONString(Object obj) {
		return toJSON(obj, null);
	}
	
	public static String toJSONStringWithDateFormat(Object obj, String formate) {
		return toJSON(obj, formate);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String toJSON(Object obj, String formate) {
		DateFormat df = null;
		if (formate != null) {
			df = dfs.get(formate);
			if (df == null) {
				df = new SimpleDateFormat(formate);
				dfs.put(formate, df);
			}
		}
		if (obj instanceof Map) {
			return Data2Text.map2JSON((Map) obj, df);
		}
		if (obj instanceof Collection) {
			return Data2Text.list2JSON((Collection) obj, df);
		}
		return Bean2Text.toJSONString(obj, df);
	}
}
