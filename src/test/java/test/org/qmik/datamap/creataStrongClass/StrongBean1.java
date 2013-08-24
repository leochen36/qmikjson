package test.org.qmik.datamap.creataStrongClass;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.qmik.qmikjson.asm.org.objectweb.asm.ClassReader;
import org.qmik.qmikjson.asm.org.objectweb.asm.ClassVisitor;
import org.qmik.qmikjson.asm.org.objectweb.asm.ClassWriter;
import org.qmik.qmikjson.asm.org.objectweb.asm.FieldVisitor;
import org.qmik.qmikjson.asm.org.objectweb.asm.Label;
import org.qmik.qmikjson.asm.org.objectweb.asm.MethodVisitor;
import org.qmik.qmikjson.asm.org.objectweb.asm.Opcodes;
import org.qmik.qmikjson.asm.org.objectweb.asm.Type;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.JavaType;

/**
 * 
 * @author leo
 *
 */
public class StrongBean1 extends ClassLoader implements Opcodes {
	public final static String	suffix	= "$strongBean";
	
	public static String getClassName(Class<?> clazz) {
		return clazz.getSimpleName() + suffix;
	}
	
	public Class<?> makeClass(Class<?> superClazz, Class<?>... superInterfaces) {
		try {
			
			String className = getClassName(superClazz);
			String[] interfaces = new String[superInterfaces.length];
			
			//
			for (int i = 0; i < interfaces.length; i++) {
				System.out.println(superInterfaces[i].getName().replace(".", "/"));
				interfaces[i] = superInterfaces[i].getName().replace(".", "/");
			}
			//ClassReader cr = new ClassReader(superClazz.getName());
			//
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			//	ClassVisitor cv = new ClassAdapter(cw, className, interfaces);
			//cr.accept(cv, Opcodes.ASM4);
			System.out.println(">>"+className);
			//cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, className.replace(".", "/"), interfaces);
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", interfaces);
			//System.out.println(superClazz.getName().replace(".", "/"));
			//空构造  
			System.out.println(Type.getDescriptor(superClazz));
			Field[] fields = superClazz.getDeclaredFields();
			for (Field field : fields) {
				cw.visitField(ACC_PUBLIC, field.getName(), JavaType.getDesc(field.getType()), null, null).visitEnd();
			}
			//Structure
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
			
			Method[] methods = superClazz.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					makeSetMethod(method, cw, method.getName(), className);
				} else if (method.getName().startsWith("get")) {
					makeGetMethod(method, cw, method.getName(), className);
				} else {
					//makeMethod(cw, method.getName(), className);
				}
			}
			for (Class<?> clazz : superInterfaces) {
				methods = clazz.getDeclaredMethods();
				if (clazz == IBean.class) {
					for (Method method : methods) {
						if (method.getName().equals("__setValue")) {
							makeIBeanSetMethod(method, cw, method.getName(), className, fields);
						} else if (method.getName().equals("__getValue")) {
							
						}
					}
				} else {
					
				}
				
			}
			cw.visitEnd();
			
			byte[] code = cw.toByteArray();
			FileOutputStream fos = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource("").getFile() + className);
			fos.write(code);
			fos.flush();
			fos.close();
			return this.defineClass(className, code, 0, code.length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void makeField(ClassWriter cw, String name, String className) {
		FieldVisitor mv = cw.visitField(ACC_PUBLIC, name, "", null, null);
		
	}
	
	private static void makeIBeanSetMethod(Method method, ClassWriter cw, String methodName, String className, Field[] fields) {
		System.out.println("---------"+method.getName());
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		
		
		Label f0 = new Label();
		/*mv.visitVarInsn(ILOAD, 2);
		mv.visitJumpInsn(IFLT, f0);*/
		mv.visitLdcInsn("key");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFEQ, f0);
		
	
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitFieldInsn(PUTFIELD, className, "id", "I");
		

		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, className, "ko", "Ljava/lang/Object;");	

		
		Label end=new Label();
		mv.visitJumpInsn(GOTO, end);
		
		
		mv.visitLabel(f0);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
		mv.visitInsn(DUP);
		
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V");
		mv.visitInsn(ATHROW);
		
		mv.visitLabel(end);
		mv.visitFrame(F_SAME, 0, null, 0, null);
		
		
		Label f1=new Label();
		mv.visitLdcInsn("key");
		mv.visitVarInsn(ALOAD, 1);
		
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
		
		mv.visitJumpInsn(IFNE, f1);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("-haha--");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

		mv.visitLabel(f1);
		
		
		
		mv.visitInsn(RETURN);
		
		mv.visitMaxs(16, 8);
		
		mv.visitEnd();
		
	}
	
	private static void makeGetMethod(Method method, ClassWriter cw, String methodName, String className) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, className, "id", "I");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static void makeSetMethod(Method method, ClassWriter cw, String methodName, String className) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "()V", null, null);
		
		System.out.println(method.getName() + "----" + JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()));
		//MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, JavaType.getMethodDesc(method.getParameterTypes(), method.getReturnType()), null, null);
		mv.visitCode();
		Label la = new Label();
		mv.visitLabel(la);
		mv.visitLineNumber(8, la);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("调用方法 [" + methodName + "]");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		Label lb = new Label();
		mv.visitLabel(lb);
		mv.visitLineNumber(9, lb);
		mv.visitInsn(RETURN);
		Label lc = new Label();
		mv.visitLabel(lc);
		mv.visitLocalVariable("this", "L" + className + ";", null, la, lc, 0);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
	
	private static void makeMethod(ClassWriter cw, String methodName, String className) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(8, l0);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn("调用方法 [" + methodName + "]");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(9, l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L" + className + ";", null, l0, l2, 0);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}
}
