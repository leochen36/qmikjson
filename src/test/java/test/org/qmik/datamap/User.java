package test.org.qmik.datamap;

public class User {
	
	public int		id;
	public String	name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setValue(String name, Object value) {
		if (name.equals("id")) {
			id = (int) (Integer) value;
		} else if (name.equals("name")) {
			name = (String) value;
		}
	}
}
