package org.qmik.qmikjson;

import java.util.Date;

public class JSON {
	private final static JSONParse	parse	= new JSONParse();
	
	public static Object parse(String json) {
		return parse.parse(json);
	}
	
	public static Object parse(String json, Class<?> clazz) {
		return parse.parse(json, clazz);
	}
	
	public static String toJSONString(Object obj) {
		
		return null;
	}
	
	
	public static class User {
		private int		id;
		private String	name;
		private String	addr;
		private int		age;
		private int		sex;
		private Date	birth;
		
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
		
		public String getAddr() {
			return addr;
		}
		
		public void setAddr(String addr) {
			this.addr = addr;
		}
		
		public int getAge() {
			return age;
		}
		
		public void setAge(int age) {
			this.age = age;
		}
		
		public int getSex() {
			return sex;
		}
		
		public void setSex(int sex) {
			this.sex = sex;
		}
		
		public Date getBirth() {
			return birth;
		}
		
		public void setBirth(Date birth) {
			this.birth = birth;
		}
	}
}
