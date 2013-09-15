package org.qmik.qmikjson.out;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

public class Object2Text extends Base2Text {
	private static Object2Text	instance	= new Object2Text();
	
	private Object2Text() {
	}
	
	public static Object2Text getInstance() {
		return instance;
	}
	
	public String toJSONString(Object bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String toJSONString(Object value, DateFormat df) {
		if (value instanceof Map) {
			return Data2Text.getInstance().toJSONString((Map) value, df);
		} else if (value instanceof Collection) {
			return Array2Text.getInstance().toJSONString((Collection) value, df);
		} else {
			return Bean2Text.getInstance().toJSONString(value, df);
		}
	}
	
	@Override
	protected void appendWriter(CharWriter writer, Object value, DateFormat df) {
		if (value instanceof Map) {
			Data2Text.getInstance().appendWriter(writer, value, df);
		} else if (value instanceof Collection) {
			Array2Text.getInstance().appendWriter(writer, value, df);
		} else {
			Bean2Text.getInstance().appendWriter(writer, value, df);
		}
	}
	
}
