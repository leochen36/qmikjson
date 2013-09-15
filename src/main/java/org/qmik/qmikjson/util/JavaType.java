package org.qmik.qmikjson.util;

import org.qmik.qmikjson.asm.org.objectweb.asm.Type;

/**
 * java 字节码指令 对象类型描述
 * @author leo
 *
 */
public class JavaType {
	public static String getMethodDesc(Class<?>[] paramType, Class<?> returnType) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		if (paramType != null) {
			for (Class<?> type : paramType) {
				sb.append(getDesc(type));
			}
		}
		sb.append(")");
		sb.append(getDesc(returnType));
		return sb.toString();
	}
	
	/**
	 * 取得对象的java底层描述,如:int>I,bool>Z,String>Ljava/lang/String;
	 * @param type
	 * @return
	 */
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
		if (type == String[].class) {
			return "[Ljava/lang/String;";
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
	
	/**
	 * 取得class的类路径 ,把包.类转换成 包/类,如果java/lang/Object形式
	 * @param clazz
	 * @return
	 */
	public static String getInternalName(Class<?> clazz) {
		return Type.getInternalName(clazz);
	}
}
