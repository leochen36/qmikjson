package org.qmik.qmikjson.out;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.qmik.qmikjson.JSONException;
import org.qmik.qmikjson.util.MixUtil;

/**
 * map 转换成 json string
 * @author leo
 *
 */
public class Data2Text {
	private static ThreadLocal<SoftReference<CharWriter>>	gtl_writers	= new ThreadLocal<SoftReference<CharWriter>>() {
																								@SuppressWarnings({ "unchecked", "rawtypes" })
																								protected SoftReference<CharWriter> initialValue() {
																									return new SoftReference(new CharWriter(1024));
																								};
																							};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String list2JSON(Collection<Object> list, DateFormat df) {
		CharWriter writer = gtl_writers.get().get();
		if (writer == null) {
			gtl_writers.set(new SoftReference(new CharWriter(1024)));
		}
		writer.clear();
		list2JSON(writer, list, df);
		return writer.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void list2JSON(CharWriter writer, Collection<Object> list, DateFormat df) {
		try {
			writer.append('[');
			Iterator<Object> vals = list.iterator();
			Object value;
			while (vals.hasNext()) {
				value = vals.next();
				if (value == null) {
					continue;
				}
				if (value instanceof CharSequence) {
					writer.append('"').append(value.toString()).append('"');
				} else if (MixUtil.isPrimitive(value.getClass())) {
					writer.append(String.valueOf(value));
				} else if (value instanceof Map) {
					map2JSON(writer, (Map) value, df);
				} else if (value instanceof Collection) {
					if (value == list) {
						continue;
					}
					list2JSON(writer, (Collection) value, df);
				} else {
					writer.append("\"").append(value.toString()).append('"');
				}
				if (vals.hasNext()) {
					writer.append(',');
				}
			}
			writer.append(']');
		} catch (Exception e) {
			throw new JSONException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String map2JSON(Map map, DateFormat df) {
		CharWriter writer = gtl_writers.get().get();
		if (writer == null) {
			gtl_writers.set(new SoftReference(new CharWriter(1024)));
		}
		writer.clear();
		map2JSON(writer, map, df);
		return writer.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void map2JSON(CharWriter writer, Map map, DateFormat df) {
		try {
			writer.append('{');
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
				writer.append('"').append(key.toString()).append("\":");
				if (value instanceof CharSequence) {
					writer.append('"').append(value.toString()).append('"');
				} else if (MixUtil.isPrimitive(value.getClass())) {
					writer.append(String.valueOf(value));
				} else if (value instanceof Map) {
					if (value == map) {
						continue;
					}
					map2JSON(writer, (Map) value, df);
				} else if (value instanceof Collection) {
					list2JSON(writer, (Collection) value, df);
				} else {
					writer.append('"').append(value.toString()).append('"');
				}
				if (keys.hasNext()) {
					writer.append(',');
				}
			}
			writer.append('}');
		} catch (Exception e) {
			throw new JSONException(e.getMessage(), e);
		}
	}
}
