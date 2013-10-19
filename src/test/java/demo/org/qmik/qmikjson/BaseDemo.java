package demo.org.qmik.qmikjson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.qmik.qmikjson.JSON;

public class BaseDemo {
	public static boolean	isPrint	= false;
	public static int			loopNum	= 100;
	
	public static void print(String msg) {
		if (isPrint) {
			System.out.println(msg);
		}
	}
	
	public static List<?> createList(boolean isStrong) {
		List<Object> list = new ArrayList<Object>();
		Account account = null;
		if (isStrong) {
			account = JSON.newInstance(Account.class);
			
		} else {
			account = new Account();
		}
		account.setFee(167.1);
		account.setId(11);
		account.setUserId("76");
		AccountInfo accountInfo = null;
		if (isStrong) {
			accountInfo = JSON.newInstance(AccountInfo.class);
		} else {
			accountInfo = new AccountInfo();
			;
		}
		accountInfo.setAccountId(11);
		accountInfo.setId(111);
		accountInfo.setInfo("asfdasf");
		account.setAccountInfo(accountInfo);
		for (int i = 0; i < 1000; i++) {
			User user = null;
			if (isStrong) {
				user = JSON.newInstance(User.class);
			} else {
				user = new User();
			}
			user.setAddr("addr" + i);
			user.setAge(i);
			user.setId(i);
			user.setName("name固体" + i);
			user.setNick("nick" + i);
			user.setPasswd("passwd" + i);
			user.setCreateDate(new Date());
			user.setAccount(account);
			list.add(user);
		}
		return list;
	}
}
