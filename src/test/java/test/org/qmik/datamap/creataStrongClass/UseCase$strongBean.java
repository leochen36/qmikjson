package test.org.qmik.datamap.creataStrongClass;

import java.util.Map;

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

	@Override
	public Map<String, char[]> $$$___keys() {
		// TODO Auto-generated method stub
		return null;
	}
}
