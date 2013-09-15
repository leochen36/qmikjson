package pkg;

import java.util.HashMap;

import org.qmik.qmikjson.out.Bean2Text;
import org.qmik.qmikjson.out.CharWriter;

import test.org.qmik.datamap.creataStrongClass.User;

public class UUU {
	
	private User	user;
	private int		i	= 0;
	private int		id	= 0;
	
	public boolean bb() {
		return true;
	}
	
	public void aa(User user) {
		id = user.getId();
	}
	
	public int getId() {
		if (user != null) {
			return user.getId();
		}
		return 1;
	}
	
	int	hash	= 0;
	
	public void move() {
		hash++;
	}
	
	public void setId(int id) {
		if (user != null) {
			user.setId(id);
		}
		
	}
	
	private String	s;
	private String	s1;
	
	public String set(Object o) {
		if (s1 == s) {
			return s;
		}
		return s1;
	}
	
	private int				ehash	= 0;
	private long			l;
	private double			d;
	private CharWriter	cw;
	
	public CharWriter compare(String a, String b) {
		CharWriter cc = new CharWriter(cw);
		return cc;
	}
	
	private HashMap<String,Class>	map;
	
	public void set() {
		map = new HashMap<String,Class>();
	}
	public void set(String v,Class clazz){
		map.put(v, clazz);
	}
}
