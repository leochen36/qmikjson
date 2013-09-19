package test.org.qmik.datamap.creataStrongClass;

import java.util.Date;
import java.util.Map;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.StrongBeanFactory;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.token.asm.IStrongBean;

import test.org.qmik.datamap.UseCase;

public class MainBean {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//Thread.currentThread().setContextClassLoader(User.class.getClassLoader());
		//test();
		
		System.out.println(User.class.getClassLoader());
		
		IStrongBean userBean = StrongBeanFactory.get(User.class);
		if (true)
			return;
		User user = (User) userBean;
		System.out.println(user.getClass().getInterfaces()[0]);
		user.setId(111);
		user.setCreateDate(new Date());
		user.setName("leo");
		user.setNick("mpp");
		user.setUid(3434304340L);
		
		user.setId1(111);
		user.setCreateDate1(new Date());
		user.setName1("leo");
		user.setNick1("mpp");
		user.setUid1(3434304340L);
		
		user.setId2(111);
		user.setCreateDate2(new Date());
		user.setName2("leo");
		user.setNick2("mpp");
		user.setUid2(3434304340L);
		
		user.setId3(111);
		user.setCreateDate3(new Date());
		user.setName3("leo");
		user.setNick3("mpp");
		user.setUid3(3434304340L);
		
		String json = JSON.toJSONString(user);
		System.out.println("length:" + json.length());
		System.out.println("qmikjson:" + json);
		System.out.println("fastjson:" + JSON.toJSONString(user));
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 1111130000; i++) {
			JSON.toJSONString(user);
			//com.alibaba.fastjson.JSON.toJSONString(user);
		}
		System.out.println("time:" + (System.currentTimeMillis() - l1));
		
		String ms = "{\"id\":111,\"name\":\"leo\",\"nick\":\"mpp\",\"uid\":3434304340}";
		JSON.parse(ms);
		//System.out.println(JSON.parse(ms));
	}
	
	static void test() {
		IStrongBean bean = StrongBeanFactory.get(UseCase.class);
		bean.$$$___setValue("id", 3);
		//System.out.println(bean.$__getValue("id"));
		UseCase user = (UseCase) bean;
		System.out.println(user.getId());
	}
}
