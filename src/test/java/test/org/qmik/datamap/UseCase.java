package test.org.qmik.datamap;

import java.util.Date;

public class UseCase {
	
	private int		id;
	private Integer uid;
	private Object	ko;
	private Object	kb;
	private String name;
	private boolean bk;
	private long time;
	private double money;
	private Date date;
 
	public void setKo(Object ko) {
		if("key".equals(ko)){
			System.out.println("--------");
		}
		
		this.ko = ko;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}
