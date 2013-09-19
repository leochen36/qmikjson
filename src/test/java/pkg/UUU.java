package pkg;

import java.text.DateFormat;
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
		CharWriter cw = new CharWriter(cc);
		return cw;
	}
	
	private HashMap	map	= null;
	
	public CharWriter get(Object df) {
		if (map == null) {
			return null; 
		}
		CharWriter cc = (CharWriter) map.get(df);
		if (cc == null) {
			return null;
		}
		CharWriter cw = new CharWriter(cc);
		return cw;
	}
	
	public void add(Object df, CharWriter writer) {
		if (map == null) {
			map = new HashMap();
		}
		map.put(df, writer);
	}
	
	public void set(String v, Class clazz) {
		map.put(v, clazz);
	}
	public boolean exist(String v){
		if(map==null){
			return false;
		}
		return map.containsKey(v);
	}
}
