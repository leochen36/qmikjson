package test.org.qmik.datamap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestJavaField {
	
	/**
	 * @param args
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static void main(String[] args) throws Exception {
		
		//Field field = User.class.getField("id");
		
		long log1 = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			Object user = User.class.newInstance();
			//Method method = User.class.getMethod("setId", new Class[] { int.class});
			//field.set(user, i);
			//method.invoke(user, i);
			Class<?> param;
			Method[] methods = User.class.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					param = method.getParameterTypes()[0];
					if (param == int.class) {
						method.invoke(user, i);
					}else if(param.isInstance(String.class)){
						method.invoke(user, i+"");
					}
					
				}
				
			}
		}
		long log2 = System.currentTimeMillis();
		System.out.println("time:" + (log2 - log1));
		//System.out.println(user.id);
		
	}
	
	public static class User {
		private int		id;
		private String	name;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
	
}
