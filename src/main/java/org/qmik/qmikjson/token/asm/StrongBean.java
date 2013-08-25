package org.qmik.qmikjson.token.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
public class StrongBean extends ClassLoader implements Opcodes {
	public final static String	suffix	= "$StrongBean";
	
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
				cw.visitField(ACC_PUBLIC, field.getName(), JavaType.getDesc(field.getType()), null, null).visitEnd();
			}
			
			//Structure,创建构造函数
			makeStruct(cw, superClazz);
			
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
							makeIBeanSetMethod(method, cw, method.getName(), subInternalName, fields);
						} else if (method.getName().equals("$$$___getValue")) {
							makeIBeanGetMethod(method, cw, method.getName(), subInternalName, fields);
						}
					}
				}
			}
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
	private static void makeStruct(ClassWriter cw, Class<?> superClass) {
		String superClassName = getInternalName(superClass);
		
		//创建无参构造函数
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
		//创建带父类对象的构造函数
		String pv = "(" + JavaType.getDesc(superClass) + ")V";
		MethodVisitor mv1 = cw.visitMethod(ACC_PUBLIC, "<init>", pv, null, null);
		mv1.visitVarInsn(ALOAD, 0);
		mv1.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V");
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
	public static void makeIBeanSetMethod(Method method, ClassWriter cw, String methodName, String internalName, Field[] fields) {
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
			mv.visitFieldInsn(PUTFIELD, internalName, name, desc);
			
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
	public static void makeIBeanGetMethod(Method method, ClassWriter cw, String methodName, String internalName, Field[] fields) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		String name, desc;
		Label label;
		for (Field field : fields) {
			label = new Label();
			name = field.getName();
			desc = JavaType.getDesc(field.getType());
			//type = Type.getInternalName(field.getType());
			//equals
			mv.visitLdcInsn(name);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
			mv.visitJumpInsn(IFEQ, label);
			
			//get
			mv.visitVarInsn(ALOAD, 0);
			if ("I".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			} else if ("Z".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			} else if ("J".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			} else if ("D".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			} else if ("C".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			} else if ("S".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			} else if ("F".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			} else if ("B".equals(desc)) {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			} else {
				mv.visitFieldInsn(GETFIELD, internalName, name, desc);
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
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalName, fieldName, desc);
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
		
		mv.visitMaxs(1, 1);
		mv.visitEnd();
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
		
		String fieldName = MixUtil.indexLower(methodName.substring(3), 0);
		Field field = owner.getDeclaredField(fieldName);
		String desc = JavaType.getDesc(field.getType());
		
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
		
		mv.visitFieldInsn(PUTFIELD, internalName, fieldName, desc);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
}
