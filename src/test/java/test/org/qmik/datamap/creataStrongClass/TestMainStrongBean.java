package test.org.qmik.datamap.creataStrongClass;

import java.util.Date;
import java.util.HashMap;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.StrongBeanFactory;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.token.asm.IStrongBean;

public class TestMainStrongBean {
	
	public static void main(String[] args) {
		System.out.println(User.class.getClassLoader());
		
		IStrongBean userBean = StrongBeanFactory.get(User.class);
		//User userBean = new User();
		User user = (User) userBean;
		
		//User user = new User();
		user.setId(111);
		user.setCreateDate(new Date());
		user.setName("leo");
		user.setNick("mpp");
		user.setUid(3434304340L);
		//user.setFres(new HashMap<String, String>());
		user.setAccount1(new Account());
		String json = JSON.toJSONString(user);
		//System.out.println("qmikjson:" + json);
		System.out.println("qmikjson:" + JSON.toJSONString(user));
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			JSON.toJSONString(user);
			//com.alibaba.fastjson.JSON.toJSONString(user);
		}
		System.out.println("time:" + (System.currentTimeMillis() - l1));
		
		String ms = "{\"id\":111,\"name\":\"leo\",\"nick\":\"mpp\",\"uid\":3434304340}";
		JSON.parse(ms);
		//System.out.println("--" + user);
	}
	
}
