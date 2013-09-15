package org.qmik.qmikjson.token.asm;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.asm.org.objectweb.asm.ClassWriter;
import org.qmik.qmikjson.asm.org.objectweb.asm.Label;
import org.qmik.qmikjson.asm.org.objectweb.asm.MethodVisitor;
import org.qmik.qmikjson.asm.org.objectweb.asm.Opcodes;
import org.qmik.qmikjson.asm.org.objectweb.asm.Type;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.MixUtil;

/**
 * 核心实现类,用于构建bean的增强子Bean
 * @author leo
 *
 */
public class StrongBean_VN extends ClassLoader implements Opcodes {
	//字段
	public final static String		FIELD_STORE			= "$$$___keys";
	public final static String		FIELD_TARGET		= "$$$___target";
	public final static String		FIELD_HASH			= "$$$___hash";
	public final static String		FIELD_CHASH			= "$$$___chash";
	public final static String		FIELD_OUTER			= "$$$___outer";
	
	//方法
	public final static String		METHOD_SETTARGET	= "$$$___setTarget";
	public final static String		METHOD_HASH			= "$$$___hash";
	public final static String		METHOD_SETOUTER	= "$$$___setOuter";
	public final static String		METHOD_GETOUTER	= "$$$___getOuter";
	public final static String		SERIALVERSIONUID	= "serialVersionUID";
	public final static String		suffix				= "$StrongBean";
	public final static Class<?>	STORE					= ArrayList.class;
	
	public static String getInternalName(Class<?> clazz) {
		//return clazz.getSimpleName() + suffix;
		return Type.getInternalName(clazz);
	}
	
	public Class<?> makeClass(Class<?> superClazz, Class<?>... superInterfaces) {
		try {
			if (superClazz == IBean.class) {
				return superClazz;
			}
			String superInternalName = getInternalName(superClazz);
			String subInternalName = superInternalName + suffix;
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			String[] interfaces = new String[superInterfaces.length];
			
			//
			for (int i = 0; i < interfaces.length; i++) {
				interfaces[i] = getInternalName(superInterfaces[i]);
			}
			
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, subInternalName, null, superInternalName, interfaces);
			
			//创建字段
			Field[] fields = superClazz.getDeclaredFields();
			for (Field field : fields) {
				if (SERIALVERSIONUID.equals(field.getName())) {
					continue;
				}
				cw.visitField(ACC_PUBLIC, field.getName(), JavaType.getDesc(field.getType()), null, null).visitEnd();
			}
			//创建存储key的字段 list
			cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, FIELD_STORE, JavaType.getDesc(STORE), null, null).visitEnd();
			
			//创建存储父节点字段
			cw.visitField(ACC_PUBLIC, FIELD_TARGET, JavaType.getDesc(superClazz), null, null).visitEnd();
			//创建hash
			cw.visitField(ACC_PUBLIC, FIELD_HASH, "I", null, null).visitEnd();
			//创建outer
			cw.visitField(ACC_PUBLIC, FIELD_OUTER, JavaType.getDesc(String.class), null, null).visitEnd();
			//创建outer
			cw.visitField(ACC_PUBLIC, FIELD_CHASH, "I", null, null).visitEnd();
			
			//Structure,创建构造函数
			makeStruct(cw, subInternalName, superClazz, fields);
			
			Method[] methods = superClazz.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					makeSetMethod(method, cw, method.getName(), subInternalName, superClazz);
				} else if (method.getName().startsWith("get")) {
					makeGetMethod(method, cw, method.getName(), subInternalName, superClazz);
				}
			}
			
			for (Class<?> clazz : superInterfaces) {
				methods = clazz.getDeclaredMethods();
				if (clazz == IBean.class) {
					for (Method method : methods) {
						if (method.getName().equals("$$$___setValue")) {
							makeIBeanSetMethod(method, cw, method.getName(), subInternalName, fields, superClazz);
						} else if (method.getName().equals("$$$___getValue")) {
							makeIBeanGetMethod(method, cw, method.getName(), subInternalName, fields, superClazz);
						}
					}
				}
			}
			//创建toString方法
			makeToStringMethod(cw);
			//创建 static{}
			makeStaticStruct(cw, subInternalName);
			//创建keys
			makeKeysMethod(cw, fields, subInternalName);
			
			makeIBeanSetTargetMethod(cw, subInternalName, fields, superClazz);
			
			makeIBeanHashMethod(cw, subInternalName);
			//生成setOuter方法
			makeIBeanSetOuterMethod(cw, subInternalName);
			
			makeIBeanGetOuterMethod(cw, subInternalName);
			cw.visitEnd();
			byte[] code = cw.toByteArray();
			FileOutputStream fos = new FileOutputStream("D:/a.class");
			fos.write(code);
			fos.close();
			return this.defineClass(subInternalName.replace("/", "."), code, 0, code.length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 创建构造函数
	 * @param cw
	 * @param superClass
	 */
	private static void makeStaticStruct(ClassWriter cw, String subInternalName) {
		String newClass = Type.getInternalName(STORE);
		//创建无参构造函数
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitTypeInsn(NEW, newClass);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, newClass, "<init>", "()V");
		mv.visitFieldInsn(PUTSTATIC, subInternalName, FIELD_STORE, JavaType.getDesc(STORE));
		
		//
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	/** 给 STATE_FIELD 状态机设置初始值 0 */
	private static void initStateField(MethodVisitor mv, String subInternalName) {
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, subInternalName, "hashCode", "()I");
		mv.visitFieldInsn(PUTFIELD, subInternalName, FIELD_HASH, "I");
		
	}
	
	/**
	 * 创建构造函数
	 * @param cw
	 * @param superClass
	 */
	private static void makeStruct(ClassWriter cw, String subInternalName, Class<?> superClass, Field[] fields) {
		String superClassName = getInternalName(superClass);
		//////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////
		//创建无参构造函数
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
		
		//给 STATE_FIELD 状态机设置初始值 0
		initStateField(mv, subInternalName);
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
		//////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////
		//创建带父类对象的构造函数
		String desc = JavaType.getDesc(superClass);
		String pv = "(" + desc + ")V";
		MethodVisitor mv1 = cw.visitMethod(ACC_PUBLIC, "<init>", pv, null, null);
		mv1.visitVarInsn(ALOAD, 0);
		mv1.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
		
		//给 STATE_FIELD 状态机设置初始值 0
		initStateField(mv1, subInternalName);
		
		//设置值
		mv1.visitVarInsn(ALOAD, 0);
		mv1.visitVarInsn(ALOAD, 1);
		mv1.visitFieldInsn(PUTFIELD, subInternalName, FIELD_TARGET, desc);
		
		for (Field field : fields) {
			try {
				String name = field.getName();
				String desc1 = JavaType.getDesc(field.getType());
				mv1.visitVarInsn(ALOAD, 0);
				mv1.visitVarInsn(ALOAD, 1);
				mv1.visitMethodInsn(INVOKEVIRTUAL, getInternalName(superClass), "get" + MixUtil.indexUpper(name, 0), "()" + desc1);
				mv1.visitFieldInsn(PUTFIELD, subInternalName, name, desc1);
			} catch (Exception e) {
			}
		}
		
		//返回
		mv1.visitInsn(RETURN);
		mv1.visitMaxs(1, 1);
		mv1.visitEnd();
	}
	
	/**
	 * 创建 ibean接口的 set方法
	 * @param method
	 * @param cw
	 * @param methodName
	 * @param className
	 * @param fields
	 */
	public static void makeIBeanSetMethod(Method method, ClassWriter cw, String methodName, String internalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		String name, desc, type;
		Label label;
		for (Field field : fields) {
			label = new Label();
			name = field.getName();
			desc = JavaType.getDesc(field.getType());
			type = Type.getInternalName(field.getType());
			//equals
			mv.visitLdcInsn(name);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
			mv.visitJumpInsn(IFEQ, label);
			
			//set
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			
			if ("I".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			} else if ("Z".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
			} else if ("J".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
			} else if ("D".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
			} else if ("C".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
			} else if ("S".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
			} else if ("F".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
			} else if ("B".equals(desc)) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
			} else {
				mv.visitTypeInsn(CHECKCAST, type);
			}
			//mv.visitFieldInsn(PUTFIELD, internalName, name, desc);
			mv.visitMethodInsn(INVOKESPECIAL, internalName, "set" + MixUtil.indexUpper(name, 0), "(" + desc + ")V");
			
			mv.visitInsn(RETURN);
			mv.visitLabel(label);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		
	}
	
	/**
	 * 创建ibean接口的get方法
	 * @param method
	 * @param cw
	 * @param methodName
	 * @param className
	 * @param fields
	 */
	public static void makeIBeanGetMethod(Method method, ClassWriter cw, String methodName, String internalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		String name, desc;
		Label label;
		for (Field field : fields) {
			label = new Label();
			name = field.getName();
			desc = JavaType.getDesc(field.getType());
			
			//equals
			mv.visitLdcInsn(name);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
			mv.visitJumpInsn(IFEQ, label);
			
			//get
			mv.visitVarInsn(ALOAD, 0);
			//mv.visitFieldInsn(GETFIELD, internalName, name, desc);
			mv.visitMethodInsn(INVOKESPECIAL, internalName, "get" + MixUtil.indexUpper(name, 0), "()" + desc);
			if ("I".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			} else if ("Z".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			} else if ("J".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			} else if ("D".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			} else if ("C".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			} else if ("S".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			} else if ("F".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			} else if ("B".equals(desc)) {
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			}
			mv.visitInsn(ARETURN);
			mv.visitLabel(label);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
	}
	
	/** makeSetMethod 方法专用,用于 return 类型 */
	private static void makeGetMethodT(MethodVisitor mv, String desc) {
		if ("I".equals(desc)) {
			mv.visitInsn(IRETURN);
		} else if ("Z".equals(desc)) {
			mv.visitInsn(IRETURN);
		} else if ("J".equals(desc)) {
			mv.visitInsn(LRETURN);
		} else if ("D".equals(desc)) {
			mv.visitInsn(DRETURN);
		} else if ("C".equals(desc)) {
			mv.visitInsn(IRETURN);
		} else if ("S".equals(desc)) {
			mv.visitInsn(IRETURN);
		} else if ("F".equals(desc)) {
			mv.visitInsn(FRETURN);
		} else if ("B".equals(desc)) {
			mv.visitInsn(IRETURN);
		} else {
			mv.visitInsn(ARETURN);
		}
	}
	
	private static void addIntField(MethodVisitor mv, String internalName, String fieldName) {
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(DUP);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, "I");
		mv.visitLdcInsn(1);
		mv.visitInsn(IADD);
		mv.visitFieldInsn(PUTFIELD, internalName, fieldName, "I");
	}
	
	/**
	 * 创建实现父类的get方法
	 * @param method
	 * @param cw
	 * @param methodName
	 * @param className
	 * @param owner
	 * @throws Exception
	 */
	private static void makeGetMethod(Method method, ClassWriter cw, String methodName, String internalName, Class<?> owner) throws Exception {
		String fieldName = MixUtil.indexLower(methodName.substring(3), 0);
		Field field = owner.getDeclaredField(fieldName);
		String desc = JavaType.getDesc(field.getType());
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		
		////////if判断
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, JavaType.getDesc(owner));
		Label label = new Label();
		mv.visitJumpInsn(IFNULL, label);
		mv.visitVarInsn(ALOAD, 0);
		//
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, JavaType.getDesc(owner));
		mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(owner), methodName, "()" + desc);
		//
		//mv.visitFieldInsn(GETFIELD, internalName, fieldName, desc);
		
		makeGetMethodT(mv, desc);
		mv.visitLabel(label);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		//////else
		mv.visitVarInsn(ALOAD, 0);
		//mv.visitFieldInsn(GETFIELD, internalName, fieldName, desc);
		mv.visitMethodInsn(INVOKESPECIAL, getInternalName(owner), methodName, "()" + desc);
		
		makeGetMethodT(mv, desc);
		
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	/** makeSetMethod 方法专用,用于 变量 load到LIFO队列 */
	private static void makeSetMethodT(MethodVisitor mv, String desc) {
		if ("I".equals(desc)) {
			mv.visitVarInsn(ILOAD, 1);
		} else if ("Z".equals(desc)) {
			mv.visitVarInsn(ILOAD, 1);
		} else if ("J".equals(desc)) {
			mv.visitVarInsn(LLOAD, 1);
		} else if ("D".equals(desc)) {
			mv.visitVarInsn(DLOAD, 1);
		} else if ("C".equals(desc)) {
			mv.visitVarInsn(ILOAD, 1);
		} else if ("S".equals(desc)) {
			mv.visitVarInsn(ILOAD, 1);
		} else if ("F".equals(desc)) {
			mv.visitVarInsn(FLOAD, 1);
		} else if ("B".equals(desc)) {
			mv.visitVarInsn(ILOAD, 1);
		} else {
			mv.visitVarInsn(ALOAD, 1);
		}
	}
	
	/**
	 * 创建实现父类的set方法
	 * @param method
	 * @param cw
	 * @param methodName
	 * @param className
	 * @param owner
	 * @throws Exception
	 */
	private static void makeSetMethod(Method method, ClassWriter cw, String methodName, String internalName, Class<?> owner) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		
		addIntField(mv, internalName, FIELD_HASH);
		
		String fieldName = MixUtil.indexLower(methodName.substring(3), 0);
		Field field = owner.getDeclaredField(fieldName);
		String desc = JavaType.getDesc(field.getType());
		
		////////if判断
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, JavaType.getDesc(owner));
		Label label = new Label();
		mv.visitJumpInsn(IFNULL, label);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, JavaType.getDesc(owner));
		makeSetMethodT(mv, desc);
		mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(owner), methodName, "(" + desc + ")V");
		///
		mv.visitVarInsn(ALOAD, 0);
		makeSetMethodT(mv, desc);
		mv.visitFieldInsn(PUTFIELD, internalName, fieldName, desc);
		
		mv.visitInsn(RETURN);
		mv.visitLabel(label);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		//set 
		makeSetMethodT(mv, desc);
		//mv.visitFieldInsn(PUTFIELD, internalName, fieldName, desc);
		mv.visitMethodInsn(INVOKESPECIAL, getInternalName(owner), methodName, "(" + desc + ")V");
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static String	beanToInternalName	= getInternalName(JSON.class);
	
	/**
	 * 创建 toString方法
	 * @param cw
	 * @throws Exception
	 */
	private static void makeToStringMethod(ClassWriter cw) throws Exception {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESTATIC, beanToInternalName, "toJSONString", "(Ljava/lang/Object;)Ljava/lang/String;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	private static void makeKeysMethod(ClassWriter cw, Field[] fields, String subInternalName) {
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, FIELD_STORE, "()Ljava/util/List;", null, null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, subInternalName, FIELD_STORE, JavaType.getDesc(STORE));
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	/** 创建setTarget方法 */
	private static void makeIBeanSetTargetMethod(ClassWriter cw, String internalName, Field[] fields, Class<?> owner) {
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_SETTARGET, "(" + JavaType.getDesc(Object.class) + ")V", null, null);
		mv.visitCode();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		
		mv.visitTypeInsn(CHECKCAST, getInternalName(owner));
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_TARGET, JavaType.getDesc(owner));
		
		mv.visitInsn(RETURN);
		
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	/** 创建hash方法 */
	private static void makeIBeanHashMethod(ClassWriter cw, String internalName) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_HASH, "()I", null, null);
		mv.visitCode();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		
		mv.visitInsn(IRETURN);
		
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	/** 创建hash方法 */
	private static void makeIBeanSetOuterMethod(ClassWriter cw, String internalName) {
		String desc = JavaType.getDesc(String.class);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_SETOUTER, "(" + desc + ")V", null, null);
		mv.visitCode();
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_CHASH, "I");
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_OUTER, desc);
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanGetOuterMethod(ClassWriter cw, String internalName) {
		String desc = JavaType.getDesc(String.class);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_GETOUTER, "()" + desc, null, null);
		mv.visitCode();
		
		////////if判断
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_CHASH, "I");
		Label label = new Label();
		mv.visitJumpInsn(IF_ICMPEQ, label);
		
		//set hash
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_CHASH, "I");
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_OUTER, desc);
		mv.visitInsn(ARETURN);
		mv.visitLabel(label);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		//////else	
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_OUTER, desc);
		mv.visitInsn(ARETURN);
		
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
}
