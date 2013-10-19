package demo.org.qmik.qmikjson;

import java.io.Serializable;

import org.qmik.qmikjson.StrongBeanFactory;

public class Account extends PUser implements Serializable, Cloneable {
	private int				id;
	private AccountInfo	accountInfo;
	private String			userId;
	private double			fee;
	
	public AccountInfo getAccountInfo() {
		return accountInfo;
	}
	
	public void setAccountInfo(AccountInfo accountInfo) {
		//this.accountInfo = accountInfo;
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
	
	public Account clone() {
		Account ac = StrongBeanFactory.get(Account.class, this);
		return ac;
	}
}
