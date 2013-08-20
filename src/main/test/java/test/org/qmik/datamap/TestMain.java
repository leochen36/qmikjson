package test.org.qmik.datamap;

import org.qmik.datamap.IData;

public class TestMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UserSV userSV=new UserSV();
		IData<FUser> user=userSV.getUserById(11L);
		System.out.println(user);
		
		System.out.println(user.get(FUser.age));
		System.out.println(user.get(FUser.name));
		userSV.get(FUser.age);
		IData<FUser> user1=userSV.get(FUser.id);
		System.out.println(user1);
		System.out.println(System.currentTimeMillis());
	}
	
}
