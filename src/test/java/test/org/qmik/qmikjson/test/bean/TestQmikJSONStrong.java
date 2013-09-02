package test.org.qmik.qmikjson.test.bean;

import test.org.qmik.datamap.creataStrongClass.User;

public class TestQmikJSONStrong {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("test qmikjson strong");
		User user = CoreBeanUnit.createIBean();
		CoreBeanUnit.testQmikJSON(user);
	}
	
}
