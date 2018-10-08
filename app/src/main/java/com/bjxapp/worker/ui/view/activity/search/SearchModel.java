package com.bjxapp.worker.ui.view.activity.search;

public class SearchModel {
	private String code;
	private String name;
	private String sortLetters;
	private String sortString;
	private int check;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
	public String getSortString() {
		return sortString;
	}
	public void setSortString(String sortString) {
		this.sortString = sortString;
	}
	
	public int getCheck() {
		return check;
	}
	public void setCheck(int check) {
		this.check = check;
	}
}
