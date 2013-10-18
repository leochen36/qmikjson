package org.qmik.qmikjson.out;

import java.text.DateFormat;
import java.util.Collection;

public class Array2Text extends Base2Text {
	private static Array2Text			instance	= new Array2Text();
	//线程变量
	private ThreadLocal<CharWriter>	l_writer	= new ThreadLocal<CharWriter>() {
																protected CharWriter initialValue() {
																	return new CharWriter(10240);
																};
															};
	
	private Array2Text() {
	}
	
	public static Array2Text getInstance() {
		return instance;
	}
	
	public String toJSONString(Collection<Object> list, DateFormat df) {
		//CharWriter writer = new CharWriter(getSize(list));
		CharWriter writer = l_writer.get();
		writer.clear();
		writer(writer, list, df);
		return writer.toString();
	}
	
	private void writer(CharWriter writer, Collection<Object> list, DateFormat df) {
		writer.append('[');
		boolean gtOne = false;
		for (Object value : list) {
			if (value == null) {
				continue;
			}
			if (gtOne) {
				writer.append(',');
			}
			appendValue(writer, list, null, value, df);
			gtOne = true;
		}
		writer.append(']');
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void appendWriter(CharWriter writer, Object value, DateFormat df) {
		writer(writer, (Collection) value, df);
	}
	
}
