package com.weivapp.model;


public class UserActivityData{
	String name;
	String imgeUrl;
	String message;
	boolean status;
	int id;
	
	public UserActivityData(String name,String message, String imgeUrl, boolean status,int id) {
		this.name = name;
		this.imgeUrl = imgeUrl;
		this.status = status;
		this.message=message;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMessage() {
		return message;
	}

	public void setPhone(String phone) {
		this.message = phone;
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
