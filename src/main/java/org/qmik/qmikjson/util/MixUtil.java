package org.qmik.qmikjson.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MixUtil {
	public static Date toDate(Object value) {
		return toDate(value, null);
	}
	
	private final static SimpleDateFormat	sdf	= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public static Date toDate(Object value, Date _default) {
		try {
			if (value == null) {
				return _default;
			}
			if (value instanceof Date) {
				return (Date) value;
			}
			return sdf.parse(value.toString());
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
	
	public static String indexLower(String s, int index) {
		if (s == null)
			return null;
		if (index >= s.length())
			return s;
		return s.substring(0, index) + s.substring(index, index + 1).toLowerCase() + s.substring(index + 1);
	}
	
	public static boolean isPrimitive(Class<?> type) {
		return type == Integer.class || type == Boolean.class || type == Long.class || type == Double.class || type == Character.class || type == Byte.class
				|| type == Float.class || type.isPrimitive();
	}
	
	public static String getRunPath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getFile();
	}
}
