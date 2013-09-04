package org.qmik.qmikjson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.qmik.qmikjson.out.Array2Text;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.out.Data2Text;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;
import org.qmik.qmikjson.util.MixUtil;

public class JSON {
	private static Map<String, DateFormat>	dfs			= new HashMap<String, DateFormat>();
	private final static JSONParse			parse			= new JSONParse();
	private final static Bean2Text			bean2Text	= Bean2Text.getInstance();
	private final static Data2Text			data2Text	= Data2Text.getInstance();
	private final static Array2Text			array2Text	= Array2Text.getInstance();
	
	/** 创建增强对象 */
	public static <T> T makeStrong(Class<T> clazz) {
		return StrongBeanFactory.get(clazz);
	}
	
	public static Object parse(String json) {
		return parse.parse(json);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T parse(String json, Class<T> clazz) {
		return (T) parse.parse(json, clazz);
	}
	
	public static String toJSONString(Object obj) {
		return toJSON(obj, null);
	}
	
	public static String toJSONStringWithDateFormat(Object obj, String formate) {
		DateFormat df = null;
		if (formate != null) {
			df = dfs.get(formate);
			if (df == null) {
				df = new SimpleDateFormat(formate);
				dfs.put(formate, df);
			}
		}
		return toJSON(obj, df);
	}
	
	public static String toJSONStringWithDateFormat(Object obj, DateFormat df) {
		return toJSON(obj, df);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String toJSON(Object obj, DateFormat df) {
		if (obj instanceof CharSequence) {
			return obj.toString();
		}
		if (MixUtil.isPrimitive(obj.getClass())) {
			return obj.toString();
		}
		if (obj instanceof Date) {
			return df == null ? ((Date) obj).getTime() + "" : df.format((Date) obj);
		}
		if (obj instanceof Map) {
			return data2Text.map2JSON((Map) obj, df);
		}
		if (obj instanceof Collection) {
			return array2Text.list2JSON((Collection) obj, df);
		}
		return bean2Text.toJSONString(obj, df);
	}
}
