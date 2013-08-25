package org.qmik.qmikjson.out;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.qmik.qmikjson.util.MixUtil;

/**
 * map 转换成 json string
 * @author leo
 *
 */
public class Data2Text {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String list2JSON(Collection<Object> list, DateFormat df) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Iterator<Object> vals = list.iterator();
		
		Object value;
		while (vals.hasNext()) {
			value = vals.next();
			if (value == null) {
				continue;
			}
			if (value instanceof CharSequence) {
				sb.append("\"").append(value).append("\"");
			} else if (MixUtil.isPrimitive(value.getClass())) {
				sb.append(value);
			} else if (value instanceof Map) {
				sb.append(map2JSON((Map) value, df));
			} else if (value instanceof Collection) {
				if (value == list) {
					continue;
				}
				sb.append(list2JSON((Collection) value, df));
			} else {
				sb.append("\"").append(value.toString()).append("\"");
			}
			if (vals.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String map2JSON(Map map, DateFormat df) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Iterator<Object> keys = map.keySet().iterator();
		Object key;
		Object value;
		while (keys.hasNext()) {
			key = keys.next();
			if (key == null) {
				continue;
			}
			value = map.get(key);
			if (value == null) {
				continue;
			}
			sb.append("\"").append(key).append("\":");
			if (value instanceof CharSequence) {
				sb.append("\"").append(value).append("\"");
			} else if (MixUtil.isPrimitive(value.getClass())) {
				sb.append(value);
			} else if (value instanceof Map) {
				if (value == map) {
					continue;
				}
				sb.append(map2JSON((Map) value, df));
			} else if (value instanceof Collection) {
				sb.append(list2JSON((Collection) value, df));
			} else {
				sb.append("\"").append(value.toString()).append("\"");
			}
			if (keys.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
