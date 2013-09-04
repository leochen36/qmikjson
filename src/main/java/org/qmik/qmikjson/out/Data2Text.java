package org.qmik.qmikjson.out;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * map 转换成 json string
 * @author leo
 *
 */
public class Data2Text extends Base2Text {
	private static ThreadLocal<SoftReference<CharWriter>>	gtl_writers	= new ThreadLocal<SoftReference<CharWriter>>() {
																								@SuppressWarnings({ "unchecked", "rawtypes" })
																								protected SoftReference<CharWriter> initialValue() {
																									return new SoftReference(new CharWriter(1024));
																								};
																							};
	private static Data2Text										instance		= new Data2Text();
	
	private Data2Text() {
	}
	
	public static Data2Text getInstance() {
		return instance;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String map2JSON(Map map, DateFormat df) {
		CharWriter writer = gtl_writers.get().get();
		if (writer == null) {
			gtl_writers.set(new SoftReference(new CharWriter(1024)));
		}
		writer.clear();
		try {
			map2JSON(writer, map, df);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void map2JSON(CharWriter writer, Map map, DateFormat df) throws IOException {
		Iterator<Object> keys = map.keySet().iterator();
		Object key;
		Object value;
		writer.append('{');
		while (keys.hasNext()) {
			key = keys.next();
			value = map.get(key);
			if (value == null) {
				continue;
			}
			append(writer, map, key.toString(), value, df);
			if (keys.hasNext()) {
				writer.append(',');
			}
		}
		writer.append('}');
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void toStringWriter(CharWriter writer, Object value, DateFormat df) throws IOException {
		map2JSON(writer, (Map) value, df);
	}
}
