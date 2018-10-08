package com.bjxapp.worker.model;

public class BankInfo {
	private String card;
	private String name;
	private String person;
	private String mobile;
	
	private double balanceMoney;
	private double cashMoney;
	
	private int pledgeDays;
	private int cashStart1;
	private int cashEnd1;
	private int cashStart2;
	private int cashEnd2;
	private int cashStart3;
	private int cashEnd3;

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public double getBalanceMoney() {
		return balanceMoney;
	}

	public void setBalanceMoney(double balanceMoney) {
		this.balanceMoney = balanceMoney;
	}
	
	public double getCashMoney() {
		return cashMoney;
	}

	public void setCashMoney(double cashMoney) {
		this.cashMoney = cashMoney;
	}
	
	public int getPledgeDays() {
		return pledgeDays;
	}

	public void setPledgeDays(int pledgeDays) {
		this.pledgeDays = pledgeDays;
	}
	
	public int getCashStart1() {
		return cashStart1;
	}

	public void setCashStart1(int cashStart1) {
		this.cashStart1 = cashStart1;
	}
	
	public int getCashEnd1() {
		return cashEnd1;
	}

	public void setCashEnd1(int cashEnd1) {
		this.cashEnd1 = cashEnd1;
	}
	
	public int getCashStart2() {
		return cashStart2;
	}

	public void setCashStart2(int cashStart2) {
		this.cashStart2 = cashStart2;
	}
	
	public int getCashEnd2() {
		return cashEnd2;
	}

	public void setCashEnd2(int cashEnd2) {
		this.cashEnd2 = cashEnd2;
	}
	
	public int getCashStart3() {
		return cashStart3;
	}

	public void setCashStart3(int cashStart3) {
		this.cashStart3 = cashStart3;
	}
	
	public int getCashEnd3() {
		return cashEnd3;
	}

	public void setCashEnd3(int cashEnd3) {
		this.cashEnd3 = cashEnd3;
	}

}
