package test.org.qmik.datamap.creataStrongClass;

import java.io.Serializable;

public class Account implements Serializable {
	private int				id;
	private AccountInfo	accountInfo;
	private String			userId;
	private double			fee;
	
	public AccountInfo getAccountInfo() {
		return accountInfo;
	}
	
	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public double getFee() {
		return fee;
	}
	
	public void setFee(double fee) {
		this.fee = fee;
	}
	
}
