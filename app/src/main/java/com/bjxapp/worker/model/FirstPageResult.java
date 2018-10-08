package com.bjxapp.worker.model;

public class FirstPageResult {
	private int resultCode;
	private Object signObject;
	private Object orderObject;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public Object getSignObject() {
		return signObject;
	}

	public void setSignObject(Object signObject) {
		this.signObject = signObject;
	}
	
	public Object getOrderObject() {
		return orderObject;
	}

	public void setOrderObject(Object orderObject) {
		this.orderObject = orderObject;
	}

}
