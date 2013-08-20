package org.qmik.qmikjson;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.qmik.qmikjson.token.DataToken;
import org.qmik.qmikjson.token.Token;
import org.qmik.qmikjson.util.MixUtil;

public class JSONParse {
	private Token	tokenData	= new DataToken();
	
	public JSONParse() {
	}
	
	private String firstLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	@SuppressWarnings("rawtypes")
	public Object parse(String json, Class<?> clazz) {
		try {
			
			Object result = parse(json);
			;
			if (clazz.isInstance(Map.class)) {
				if (!(result instanceof Map)) {
					throw new IllegalArgumentException("ars json=" + json + "is Illegal,is not map structures!");
				}
				return result;
			}
			if (clazz.isInstance(Collection.class)) {
				if (!(result instanceof Collection)) {
					throw new IllegalArgumentException("ars json=" + json + "is Illegal,is not list structures!");
				}
				return result;
			}
			Map map = (Map) result;
			Method[] methods = clazz.getMethods();
			String name;
			Class<?>[] param;
			int setIndex;
			Object value = clazz.newInstance();
			Object _value;
			for (Method method : methods) {
				try {
					name = method.getName();
					setIndex = name.indexOf("set");
					if (setIndex == 0) {
						param = method.getParameterTypes();
						if (param.length < 1) {
							continue;
						}
						name = firstLower(name.substring(setIndex));
						_value = map.get(name);
						if (param[0].isInstance(CharSequence.class)) {
							method.invoke(value, _value.toString());
						} else if (param[0].isInstance(int.class) || param[0].isInstance(Integer.class)) {
							method.invoke(value, MixUtil.toInt(_value));
						} else if (param[0].isInstance(long.class) || param[0].isInstance(Long.class)) {
							method.invoke(value, MixUtil.toLong(_value));
						} else if (param[0].isInstance(double.class) || param[0].isInstance(Double.class)) {
							method.invoke(value, MixUtil.toDouble(_value));
						} else if (param[0].isInstance(float.class) || param[0].isInstance(Float.class)) {
							method.invoke(value, MixUtil.toFloat(_value));
						} else if (param[0].isInstance(boolean.class) || param[0].isInstance(Boolean.class)) {
							method.invoke(value, MixUtil.toBoolean(_value));
						} else if (param[0].isInstance(Date.class)) {
							method.invoke(value, MixUtil.toDate(_value));
						}
					}
				} catch (Exception e) {
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object parse(String json) {
		return (Object) tokenData.token(json);
	}
	
}
