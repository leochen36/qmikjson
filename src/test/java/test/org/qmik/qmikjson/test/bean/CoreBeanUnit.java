package test.org.qmik.qmikjson.test.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;
import org.qmik.qmikjson.util.BeanUtil;

import test.org.qmik.datamap.creataStrongClass.Account;
import test.org.qmik.datamap.creataStrongClass.AccountInfo;
import test.org.qmik.datamap.creataStrongClass.User;

public class CoreBeanUnit {
	private static int	maxJson	= 30000;
	private static int	dealSum	= 30000;
	private static int	arrays	= 30;
	
	public static User create() {
		User user = new User();
		initBean(user);
		System.out.println(user);
		System.out.println("length:" + user.toString().length());
		return user;
	}
	
	public static User createIBean() {
		User user = StrongBeanFactory.get(User.class);
		//User user = new User();
		
		initBean(user);
		return user;
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
		Map<String, String> map = new HashMap<String, String>();
		map.put("11a", "a");
		user.setFres(map);
		
		Account account = StrongBeanFactory.get(Account.class);
		account.setFee(167.1);
		account.setId(11);
		account.setUserId("76");
		user.setAccount(account);
		user.setAccount1(account);
		
		AccountInfo accountInfo = StrongBeanFactory.get(AccountInfo.class);
		accountInfo.setAccountId(11);
		accountInfo.setId(111);
		accountInfo.setInfo("asfdasf");
		account.setAccountInfo(accountInfo);
		List<Account> array = new ArrayList<Account>();
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		array.add(account);
		user.setAccounts(array);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//User user = StrongBeanFactory.get(User.class);
		User user = create();
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			//user.toString();
			com.alibaba.fastjson.JSON.toJSONString(user);
		}
		
		System.out.println(System.currentTimeMillis() - l1);
		System.out.println(user.toString());
	}
	
	public static void testQmikJSON(User user) {
		System.out.println("循环次数:" + dealSum);
		System.out.println("length:" + user.toString().length());
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < dealSum; i++) {
			JSON.toJSONString(user);
		}
		long lg = System.currentTimeMillis();
		System.out.println("耗时:" + (lg - l1) + "ms");
	}
	
	public static void testFastJSON(User user) {
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
