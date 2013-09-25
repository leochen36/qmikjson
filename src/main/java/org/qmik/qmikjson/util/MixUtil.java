package org.qmik.qmikjson.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MixUtil {
	public static Date toDate(Object value) {
		return toDate(value, null);
	}
	
	private final static DateFormat	sdf	= new SimpleDateFormat("yyyyMMddhhmmss");
	
	@SuppressWarnings("deprecation")
	public static Date toDate(Object value, Date _default) {
		try {
			if (value == null) {
				return _default;
			}
			if (value instanceof Date) {
				return (Date) value;
			}
			if (value instanceof Long) {
				return new Date((Long) value);
			}
			if (value instanceof String) {
				String val = (String) value;
				StringBuilder sb = new StringBuilder();
				char[] cs = val.toCharArray();
				for (char c : cs) {
					if (c >= '0' && c <= '9') {
						sb.append(c);
					}
				}
				val = sb.toString();
				if (val.length() == 14) {
					return sdf.parse(val);
				}
				return new Date((String) value);
			}
			return _default;
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static char toChar(Object value) {
		return (char) toInt(value, 0);
	}
	
	public static int toInt(Object value) {
		return toInt(value, 0);
	}
	
	public static int toInt(Object value, int _default) {
		try {
			return Integer.valueOf(value.toString());
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static long toLong(Object value) {
		return toLong(value, 0);
	}
	
	public static long toLong(Object value, long _default) {
		try {
			return Long.valueOf(value.toString());
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static float toFloat(Object value) {
		return toFloat(value, 0.0f);
	}
	
	public static float toFloat(Object value, float _default) {
		try {
			return Float.valueOf(value.toString());
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static double toDouble(Object value) {
		return toDouble(value, 0.0);
	}
	
	public static double toDouble(Object value, double _default) {
		try {
			return Double.valueOf(value.toString());
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static boolean toBoolean(Object value) {
		return toBoolean(value, false);
	}
	
	public static boolean toBoolean(Object value, boolean _default) {
		try {
			return Boolean.valueOf(value.toString());
		} catch (Exception e) {
			return _default;
		}
	}
	
	public static Object to4Byte(String value) {
		value = value.trim();
		if ("null".equals(value)) {
			return null;
		}
		if ("true".equals(value) || "false".equals(value)) {
			return Boolean.valueOf(value);
		}
		if (value.indexOf(".") >= 0) {
			return Double.valueOf(value);
		}
		if (value.length() > 9) {
			return Long.valueOf(value);
		}
		return Integer.valueOf(value);
	}
	
	public static String indexLower(String s, int index) {
		if (s == null)
			return null;
		if (index >= s.length())
			return s;
		return s.substring(0, index) + s.substring(index, index + 1).toLowerCase() + s.substring(index + 1);
	}
	
	public static String indexUpper(String s, int index) {
		if (s == null)
			return null;
		if (index >= s.length())
			return s;
		return s.substring(0, index) + s.substring(index, index + 1).toUpperCase() + s.substring(index + 1);
	}
	
	public static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type == Integer.class || type == Boolean.class || type == Long.class || type == Double.class || type == Character.class
				|| type == Byte.class || type == Float.class;
	}
	
	/** 是否是单元类型,像基本类型,时间类型,字符串类型都认为是 */
	public static boolean isUnitType(Class<?> type) {
		return isPrimitive(type) || CharSequence.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type);
	}
	
	public static String getRunPath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getFile();
	}
	
	public static int getTimeM() {
		return (int) (System.currentTimeMillis() / 6000);
	}
	
}
