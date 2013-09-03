package test.org.qmik.qmikjson.test.map;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.out.Data2Text;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;
import org.qmik.qmikjson.util.BeanUtil;

import test.org.qmik.datamap.creataStrongClass.User;

public class CoreMapUnit {
	private static int	maxJson	= 30000;
	private static int	dealSum	= 30000;
	private static int	arrays	= 30;
	
	public static Map<String, Object> create() {
		IBean user = StrongBeanFactory.get(User.class);
		initBean((User) user);
		Map<String, Object> map = new HashMap<String, Object>();
		for (String field : user.$$$___keys().keySet()) {
			map.put(field, user.$$$___getValue(field));
		}
		return map;
	}
	
	private static void initBean(User user) {
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
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//User user = StrongBeanFactory.get(User.class);
		Map<String, Object> user = create();
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			//user.toString();
			com.alibaba.fastjson.JSON.toJSONString(user);
		}
		
		System.out.println(System.currentTimeMillis() - l1);
		System.out.println(user.toString());
	}
	
	public static void testQmikJSON() {
		Map<String, Object> user = create();
		System.out.println("循环次数:" + dealSum);
		System.out.println("length:" + user.toString().length());
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < dealSum; i++) {
			Data2Text.map2JSON(user, null);
		}
		long lg = System.currentTimeMillis();
		System.out.println("耗时:" + (lg - l1) + "ms");
		System.out.println("json:" + JSON.toJSONString(user));
	}
	
	public static void testFastJSON() {
		Map<String, Object> user = create();
		System.out.println("循环次数:" + dealSum);
		System.out.println("length:" + user.toString().length());
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < dealSum; i++) {
			com.alibaba.fastjson.JSON.toJSONString(user);
		}
		long lg = System.currentTimeMillis();
		System.out.println("耗时:" + (lg - l1) + "ms");
	}
}
