package com.weivapp.model;


public class UserInfo{
	String name;
	String imgeUrl;
	String phone;
	boolean status;
	int id;
	
	public UserInfo(int id,String name,String phone, String imgeUrl, boolean status) {
		this.name = name;
		this.imgeUrl = imgeUrl;
		this.status = status;
		this.phone=phone;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImgeUrl() {
		return imgeUrl;
	}

	public void setImgeUrl(String imgeUrl) {
		this.imgeUrl = imgeUrl;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
