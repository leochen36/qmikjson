package test.org.qmik.datamap.creataStrongClass;

import java.util.Date;
import java.util.Map;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;
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
		
		
		IBean userBean = StrongBeanFactory.get(User.class, IBean.class);
		System.out.println("1");
		System.out.println(userBean);
		IBean userBean1 = StrongBeanFactory.get(test.org.qmik.datamap.User.class, IBean.class);
		System.out.println("2");
		User user = (User) userBean;
		System.out.println(user.getClass().getInterfaces()[0]);
		user.setId(111);
		user.setCreateDate(new Date());
		user.setName("leo");
		user.setNick("mpp");
		user.setUid(3434304340L);
		
		String json = Bean2Text.toJSONString(user);
		System.out.println("qmikjson:" + json);
		System.out.println("fastjson:" + JSON.toJSONString(user));
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			JSON.toJSONString(user);
			//com.alibaba.fastjson.JSON.toJSONString(user);
		}
		System.out.println("time:" + (System.currentTimeMillis() - l1));
		
		String ms = "{\"id\":111,\"name\":\"leo\",\"nick\":\"mpp\",\"uid\":3434304340}";
		JSON.parse(ms);
		//System.out.println(JSON.parse(ms));
	}
	
	static void test() {
		IBean bean = StrongBeanFactory.get(UseCase.class, IBean.class);
		bean.$$$___setValue("id", 3);
		//System.out.println(bean.$__getValue("id"));
		UseCase user = (UseCase) bean;
		System.out.println(user.getId());
	}
}
