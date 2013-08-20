package test.org.qmik.datamap;

import org.qmik.datamap.Data;
import org.qmik.datamap.IData;
import org.qmik.datamap.IField;

public class UserSV {
	
	public IData<FUser> getUserById(Long id) {
		IData<FUser> user = new Data<FUser>();
		user.add(FUser.age, 1);
		user.add(FUser.id, id);
		user.add(FUser.name, "aa");
		return user;
	}
	
	public <T extends IField> IData<T> get(T t) {
		IData<T> user = new Data<T>();
		
		user.add("age", "1");
		user.add("name", "leo");
		return user;
	}
}
