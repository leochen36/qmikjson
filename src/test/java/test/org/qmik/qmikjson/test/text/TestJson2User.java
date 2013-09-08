package test.org.qmik.qmikjson.test.text;

import org.qmik.qmikjson.JSON;

import test.org.qmik.datamap.creataStrongClass.User;
import test.org.qmik.qmikjson.test.bean.CoreBeanUnit;

public class TestJson2User {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		User user = CoreBeanUnit.createIBean();
		
		System.out.println(user.toString());
		
	}
	
}
