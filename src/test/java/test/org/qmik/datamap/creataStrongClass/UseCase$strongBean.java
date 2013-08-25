package test.org.qmik.datamap.creataStrongClass;

import org.qmik.qmikjson.token.IBean;
import test.org.qmik.datamap.UseCase;

public class UseCase$strongBean implements IBean {
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void $$$___setValue(String s, Object i) {
		id = ((Integer) i).intValue();
	}
	
	public Object $$$___getValue(String name) {
		return Integer.valueOf(100);
	}
	
	public int	id;
	
	public UseCase$strongBean() {
	}
	
	public UseCase$strongBean(UseCase useCase) {
	}
}
