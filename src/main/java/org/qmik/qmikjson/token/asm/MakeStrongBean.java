package org.qmik.qmikjson.token.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.qmik.qmikjson.Config;
import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.asm.org.objectweb.asm.ClassWriter;
import org.qmik.qmikjson.asm.org.objectweb.asm.Label;
import org.qmik.qmikjson.asm.org.objectweb.asm.MethodVisitor;
import org.qmik.qmikjson.asm.org.objectweb.asm.Opcodes;
import org.qmik.qmikjson.asm.org.objectweb.asm.Type;
import org.qmik.qmikjson.out.CharWriter;
import org.qmik.qmikjson.util.JavaType;
import org.qmik.qmikjson.util.MixUtil;

/**
 * 核心实现类,用于构建bean的增强子Bean
 * @author leo
 *
 */
public class MakeStrongBean extends ClassLoader implements Opcodes {
	//字段
	public final static String		FIELD_STORE				= "$$$___keys";
	public final static String		FIELD_TARGET			= "$$$___target";
	public final static String		FIELD_HASH				= "$$$___hash";
	public final static String		FIELD_CHASH				= "$$$___chash";
	public final static String		FIELD_MUL				= "$$$___mul";
	public final static String		FIELD_FIELDTYPES		= "$$$___fieldTypes";
	public final static String		FIELD_OUTER1			= "$$$___outer1";
	public final static String		FIELD_OUTER2			= "$$$___outer2";
	
	//方法
	public final static String		METHOD_SET				= "$$$___setValue";
	public final static String		METHOD_GET				= "$$$___getValue";
	
	public final static String		METHOD_SETTARGET		= "$$$___setTarget";
	public final static String		METHOD_HASH				= "$$$___hash";
	public final static String		METHOD_COMPARE			= "$$$___compare";
	public final static String		METHOD_ISMULMIX		= "$$$___isMulMix";
	public final static String		METHOD_FIELDTYPES		= "$$$___fieldTypes";
	public final static String		METHOD_GETOUTER1		= "$$$___getOuter1";
	public final static String		METHOD_GETOUTER2		= "$$$___getOuter2";
	public final static String		METHOD_SETOUTER1		= "$$$___setOuter1";
	public final static String		METHOD_SETOUTER2		= "$$$___setOuter2";
	public final static String		METHOD_EXISTOUTER1	= "$$$___existOuter1";
	public final static String		METHOD_EXISTOUTER2	= "$$$___existOuter2";
	//
	public final static String		SERIALVERSIONUID		= "serialVersionUID";
	public final static String		suffix					= "$StrongBean";
	public final static Class<?>	STORE						= ArrayList.class;
	
	//
	public final static String		DESC_OBJECT				= JavaType.getDesc(Object.class);
	public final static String		DESC_STRING				= JavaType.getDesc(String.class);
	public final static String		DESC_OUTER				= JavaType.getDesc(CharWriter.class);
	public final static String		DESC_MAP					= JavaType.getDesc(HashMap.class);
	public final static String		DESC_CLASS				= JavaType.getDesc(Class.class);
	//
	public final static String		INTERNAL_MAP			= getInternalName(HashMap.class);
	public final static String		INTERNAL_CLASS			= getInternalName(Class.class);
	
	///
	public static String getInternalName(Class<?> clazz) {
		//return clazz.getSimpleName() + suffix;
		return Type.getInternalName(clazz);
	}
	
	public Class<?> makeClass(Class<?> superClazz, Class<?>... superInterfaces) {
		try {
			if (superClazz == IStrongBean.class) {
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
			cw.visitField(ACC_PUBLIC, FIELD_OUTER1, JavaType.getDesc(HashMap.class), null, null).visitEnd();
			cw.visitField(ACC_PUBLIC, FIELD_OUTER2, JavaType.getDesc(HashMap.class), null, null).visitEnd();
			
			//创建outer
			cw.visitField(ACC_PUBLIC, FIELD_CHASH, "I", null, null).visitEnd();
			//创建次数
			cw.visitField(ACC_PUBLIC, FIELD_MUL, "I", null, null).visitEnd();
			cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, FIELD_FIELDTYPES, DESC_MAP, null, null).visitEnd();
			
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
			
			/*for (Class<?> clazz : superInterfaces) {
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
			}*/
			makeIBeanSetMethod(cw, subInternalName, fields, superClazz);
			makeIBeanGetMethod(cw, subInternalName, fields, superClazz);
			
			//创建toString方法
			makeToStringMethod(cw);
			//创建 static{}
			makeStaticStruct(cw, subInternalName);
			//创建keys
			makeKeysMethod(cw, fields, subInternalName);
			
			makeIBeanSetTargetMethod(cw, subInternalName, fields, superClazz);
			//创建 METHOD_HASH方法
			makeGetMethod(cw, subInternalName, METHOD_HASH, FIELD_HASH, "I");
			makeIBeanCompareMethod(cw, subInternalName, superInternalName, fields, superClazz);
			
			makeIBeanMulMixMethod(cw, subInternalName, superInternalName, fields, superClazz);
			
			makeIBeanFieldTypesMethod(cw, subInternalName);
			
			makeIBeanGetOuter1Method(cw, subInternalName, METHOD_GETOUTER1, FIELD_OUTER1);
			makeIBeanGetOuter1Method(cw, subInternalName, METHOD_GETOUTER2, FIELD_OUTER2);
			makeIBeanSetOuter1Method(cw, subInternalName, METHOD_SETOUTER1, FIELD_OUTER1);
			makeIBeanSetOuter1Method(cw, subInternalName, METHOD_SETOUTER2, FIELD_OUTER2);
			
			makeIBeanExistOuter1Method(cw, subInternalName, METHOD_EXISTOUTER1, FIELD_OUTER1);
			makeIBeanExistOuter1Method(cw, subInternalName, METHOD_EXISTOUTER2, FIELD_OUTER2);
			
			//
			cw.visitEnd();
			byte[] code = cw.toByteArray();
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
		mv.visitTypeInsn(NEW, INTERNAL_MAP);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, INTERNAL_MAP, "<init>", "()V");
		mv.visitFieldInsn(PUTSTATIC, subInternalName, FIELD_FIELDTYPES, DESC_MAP);
		
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
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitLdcInsn(1);
		mv.visitFieldInsn(PUTFIELD, subInternalName, FIELD_CHASH, "I");
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitLdcInsn(0);
		mv.visitFieldInsn(PUTFIELD, subInternalName, FIELD_MUL, "I");
		
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
	}
	
	/**
	 * 创建 ibean接口的 set方法
	 * @param method
	 * @param cw
	 * @param methodName
	 * @param className
	 * @param fields
	 */
	public static void makeIBeanSetMethod(ClassWriter cw, String internalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_SET, "(" + DESC_STRING + DESC_OBJECT + ")V", null, null);
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
	public static void makeIBeanGetMethod(ClassWriter cw, String internalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_GET, "(" + DESC_STRING + ")" + DESC_OBJECT, null, null);
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
	
	private static void makeIBeanFieldTypesMethod(ClassWriter cw, String subInternalName) {
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_FIELDTYPES, "()Ljava/util/Map;", null, null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, subInternalName, METHOD_FIELDTYPES, DESC_MAP);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	/** 创建setTarget方法 */
	private static void makeIBeanSetTargetMethod(ClassWriter cw, String internalName, Field[] fields, Class<?> owner) {
		String desc = JavaType.getDesc(owner);
		String superInternalName = getInternalName(owner);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_SETTARGET, "(" + JavaType.getDesc(Object.class) + ")V", null, null);
		mv.visitCode();
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, superInternalName);
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_TARGET, desc);
		//
		for (Field field : fields) {
			try {
				String name = field.getName();
				String desc1 = JavaType.getDesc(field.getType());
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 0);
				//mv.visitVarInsn(ALOAD, 1);
				//mv.visitTypeInsn(CHECKCAST, superInternalName);
				mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, desc);
				mv.visitMethodInsn(INVOKEVIRTUAL, superInternalName, "get" + MixUtil.indexUpper(name, 0), "()" + desc1);
				mv.visitFieldInsn(PUTFIELD, internalName, name, desc1);
			} catch (Exception e) {
			}
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanSetOuter1Method(ClassWriter cw, String internalName, String methodName, String fieldName) {
		String descCW = JavaType.getDesc(CharWriter.class);
		String internalOuter = getInternalName(HashMap.class);
		String descOuter = JavaType.getDesc(HashMap.class);
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(" + DESC_OBJECT + descCW + ")V", null, null);
		mv.visitCode();
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		mv.visitFieldInsn(PUTFIELD, internalName, FIELD_CHASH, "I");
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, descOuter);
		//
		Label lab1 = new Label();
		mv.visitJumpInsn(IFNONNULL, lab1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitTypeInsn(NEW, internalOuter);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, internalOuter, "<init>", "()V");
		mv.visitFieldInsn(PUTFIELD, internalName, fieldName, descOuter);
		mv.visitLabel(lab1);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, descOuter);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, internalOuter, "put", "(" + DESC_OBJECT + DESC_OBJECT + ")" + DESC_OBJECT);
		mv.visitInsn(POP);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanCompareMethod(ClassWriter cw, String internalName, String superInternalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_COMPARE, "()Z", null, null);
		mv.visitCode();
		String desc = JavaType.getDesc(owner);
		
		addIntField(mv, internalName, FIELD_MUL);
		
		//
		Label label = new Label();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_HASH, "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_CHASH, "I");
		mv.visitJumpInsn(IF_ICMPEQ, label);
		mv.visitLdcInsn(false);
		mv.visitInsn(IRETURN);
		mv.visitLabel(label);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		///////////////////////////////////////////// start 1
		Label labelp = new Label();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, desc);
		mv.visitJumpInsn(IFNULL, labelp);
		/////
		String fieldDesc, methodName;
		for (Field field : fields) {
			fieldDesc = JavaType.getDesc(field.getType());
			methodName = "get" + MixUtil.indexUpper(field.getName(), 0);
			/////
			/*if (!MixUtil.isUnitType(field.getType())) {
				Label lab = new Label();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, internalName, methodName, "()" + fieldDesc);
				mv.visitJumpInsn(IFNULL, lab);
				mv.visitLdcInsn(false);
				mv.visitInsn(IRETURN);
				mv.visitLabel(lab);
				mv.visitFrame(F_SAME, 0, null, 0, null);
			}*/
			//////
			label = new Label();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, internalName, field.getName(), fieldDesc);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, internalName, FIELD_TARGET, desc);
			mv.visitMethodInsn(INVOKEVIRTUAL, superInternalName, methodName, "()" + fieldDesc);
			
			if ("I".equals(fieldDesc) || "Z".equals(fieldDesc) || "C".equals(fieldDesc) || "S".equals(fieldDesc)//
					|| "F".equals(desc) || "B".equals(fieldDesc)) {
				mv.visitJumpInsn(IF_ICMPEQ, label);
			} else if ("J".equals(fieldDesc)) {
				mv.visitInsn(LCMP);
				mv.visitJumpInsn(IFEQ, label);
			} else if ("D".equals(fieldDesc)) {
				mv.visitInsn(DCMPL);
				mv.visitJumpInsn(IFEQ, label);
			} else {
				mv.visitJumpInsn(IF_ACMPEQ, label);
			}
			mv.visitLdcInsn(false);
			mv.visitInsn(IRETURN);
			mv.visitLabel(label);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		///////////////////////////////////////////// end 1
		mv.visitLabel(labelp);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		//
		mv.visitLdcInsn(true);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanGetOuter1Method(ClassWriter cw, String internalName, String methodName, String fieldName) {
		String internalCW = getInternalName(CharWriter.class);
		String descCW = JavaType.getDesc(CharWriter.class);
		String internalOuter = getInternalName(HashMap.class);
		String descOuter = JavaType.getDesc(HashMap.class);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(" + DESC_OBJECT + ")" + JavaType.getDesc(CharWriter.class), null, null);
		mv.visitCode();
		
		//创建无参构造函数
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, FIELD_OUTER1, descOuter);
		Label lab1 = new Label();
		mv.visitJumpInsn(IFNONNULL, lab1);
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitLabel(lab1);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, descOuter);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, internalOuter, "get", "(" + DESC_OBJECT + ")" + DESC_OBJECT);
		mv.visitTypeInsn(CHECKCAST, internalCW);
		mv.visitVarInsn(ASTORE, 2);
		mv.visitVarInsn(ALOAD, 2);
		Label lab2 = new Label();
		mv.visitJumpInsn(IFNONNULL, lab2);
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitLabel(lab2);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		//
		mv.visitTypeInsn(NEW, internalCW);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESPECIAL, internalCW, "<init>", "(" + descCW + ")V");
		mv.visitVarInsn(ASTORE, 3);
		mv.visitVarInsn(ALOAD, 3);
		
		mv.visitInsn(ARETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();
	}
	
	/**
	 * 创建最简单的get方法,
	 * @param cw
	 * @param internalName
	 * @param method
	 * @param field
	 * @param desc
	 */
	private static void makeGetMethod(ClassWriter cw, String internalName, String method, String field, String desc) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, method, "()" + desc, null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, field, desc);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanMulMixMethod(ClassWriter cw, String internalName, String superInternalName, Field[] fields, Class<?> owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_ISMULMIX, "()Z", null, null);
		mv.visitCode();
		
		String fieldDesc, methodName;
		for (Field field : fields) {
			fieldDesc = JavaType.getDesc(field.getType());
			methodName = "get" + MixUtil.indexUpper(field.getName(), 0);
			/////
			if (!MixUtil.isUnitType(field.getType())) {
				Label lab = new Label();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, internalName, methodName, "()" + fieldDesc);
				mv.visitJumpInsn(IFNULL, lab);
				
				Label lab1 = new Label();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, FIELD_MUL, "I");
				mv.visitLdcInsn(Config.mul);
				mv.visitJumpInsn(IF_ICMPLE, lab1);
				mv.visitLdcInsn(true);
				mv.visitInsn(IRETURN);
				mv.visitLabel(lab1);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				
				mv.visitLabel(lab);
				mv.visitFrame(F_SAME, 0, null, 0, null);
			}
		}
		mv.visitLdcInsn(false);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static void makeIBeanExistOuter1Method(ClassWriter cw, String internalName, String methodName, String fieldName) {
		String internalOuter = getInternalName(HashMap.class);
		String descOuter = JavaType.getDesc(HashMap.class);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(" + DESC_OBJECT + ")Z", null, null);
		mv.visitCode();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, descOuter);
		Label lab1 = new Label();
		mv.visitJumpInsn(IFNONNULL, lab1);
		mv.visitLdcInsn(false);
		mv.visitInsn(IRETURN);
		mv.visitLabel(lab1);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		//
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, descOuter);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, internalOuter, "containsKey", "(" + DESC_OBJECT + ")Z");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
}
