package org.qmik.qmikjson.out;

import java.text.DateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * map 转换成 json string
 * @author leo
 *
 */
public class Data2Text extends Base2Text {
	//线程变量
	private ThreadLocal<CharWriter>	l_writer	= new ThreadLocal<CharWriter>() {
																protected CharWriter initialValue() {
																	return new CharWriter(2048);
																};
															};
	private static Data2Text			instance	= new Data2Text();
	
	private Data2Text() {
	}
	
	public static Data2Text getInstance() {
		return instance;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public String toJSONString(Map map, DateFormat df) {
		//CharWriter writer = new CharWriter(getSize(map));
		CharWriter writer = l_writer.get();
		writer.clear();
		writer(writer, map, df);
		return writer.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writer(CharWriter writer, Map map, DateFormat df) {
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
			appendValue(writer, map, key.toString(), value, df);
			if (keys.hasNext()) {
				writer.append(',');
			}
		}
		writer.append('}');
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void appendWriter(CharWriter writer, Object value, DateFormat df) {
		writer(writer, (Map) value, df);
	}
}
