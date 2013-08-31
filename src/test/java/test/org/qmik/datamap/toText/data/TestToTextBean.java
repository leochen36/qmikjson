package test.org.qmik.datamap.toText.data;

import java.util.Date;

import org.qmik.qmikjson.token.asm.StrongBeanFactory;

import test.org.qmik.datamap.creataStrongClass.User;

public class TestToTextBean {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		User user = StrongBeanFactory.get(User.class);
		
		user.setId(111);
		user.setCreateDate(new Date());
		user.setName("leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		user.setNick("mddddddddddddddddddddddddddddadfasfdasfdpp");
		user.setUid(3434304340L);
		
		user.setId1(111);
		user.setCreateDate1(new Date());
		user.setName1("leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		user.setNick1("mddddddddddddddddddddddddddddadfasfdasfdpp");
		user.setUid1(3434304340L);
		
		user.setId2(111);
		user.setCreateDate2(new Date());
		user.setName2("leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		user.setNick2("mddddddddddddddddddddddddddddadfasfdasfdpp");
		user.setUid2(3434304340L);
		
		user.setId3(111);
		user.setCreateDate3(new Date());
		user.setName3("leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		user.setNick3("mddddddddddddddddddddddddddddadfasfdasfdpp");
		user.setUid3(3434304340L);
		
		System.out.println(user);
		System.out.println(user.toString().length());
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 20000; i++) {
			user.toString();
			//com.alibaba.fastjson.JSON.toJSONString(user);
		}
		
		System.out.println(System.currentTimeMillis() - l1);
	}
}
