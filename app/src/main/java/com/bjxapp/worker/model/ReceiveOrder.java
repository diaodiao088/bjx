package com.bjxapp.worker.model;

public class ReceiveOrder {
	private int orderID;
	private int orderStatus;
	private String orderDate;
	private String orderTime;
	private String address;
	private String houseNumber;
	private String contacts;
	private String telephone;
	private String serviceSubName;
	private String totalMoney;

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
	
	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public String getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	
	public String getOrderTime() {
		return orderTime;
	}
	
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getHouseNumber() {
		return houseNumber;
	}
	
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	
	public String getContacts() {
		return contacts;
	}
	
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getServiceSubName() {
		return serviceSubName;
	}
	
	public void setServiceSubName(String serviceSubName) {
		this.serviceSubName = serviceSubName;
	}
	
	public String getTotalMoney() {
		return totalMoney;
	}
	
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}
	
}
