package test.org.qmik.qmikjson.test.bean;

import test.org.qmik.datamap.creataStrongClass.User;

public class TestQmikJSON {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("test qmikjson");
		User user = CoreBeanUnit.create();
		CoreBeanUnit.testQmikJSON(user);
	}
	
}
