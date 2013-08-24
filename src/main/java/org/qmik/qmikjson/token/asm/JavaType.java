package org.qmik.qmikjson.token.asm;

public class JavaType {
	public static String getMethodDesc(Class<?>[] paramType, Class<?> returnType) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (Class<?> type : paramType) {
			sb.append(getDesc(type));
		}
		sb.append(")");
		sb.append(getDesc(returnType));
		return sb.toString();
	}
	
	public static String getDesc(Class<?> type) {
		if (type == boolean.class) {
			return "Z";
		}
		if (type == char.class) {
			return "C";
		}
		if (type == byte.class) {
			return "B";
		}
		if (type == short.class) {
			return "S";
		}
		if (type == int.class) {
			return "I";
		}
		if (type == float.class) {
			return "F";
		}
		if (type == long.class) {
			return "J";
		}
		if (type == double.class) {
			return "D";
		}
		////////////////////////////
		if (type.isArray()) {
			if (type == boolean[].class) {
				return "[Z";
			}
			if (type == char[].class) {
				return "[C";
			}
			if (type == byte[].class) {
				return "[B";
			}
			if (type == short[].class) {
				return "[S";
			}
			if (type == int[].class) {
				return "[I";
			}
			if (type == float[].class) {
				return "[F";
			}
			if (type == long[].class) {
				return "[J";
			}
			if (type == double[].class) {
				return "[D";
			}
			return "[L" + type.getName().replace(".", "/") + ";";
		}
		if (type.getName().equals("void")) {
			return "V";
		}
		return "L" + type.getName().replace(".", "/") + ";";
	}
	
}
