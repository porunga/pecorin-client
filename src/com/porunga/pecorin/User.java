package com.porunga.pecorin;

public class User {

	private String facebookId;
	private String name;
	
	public User(String facebookId, String name) {
		this.facebookId = facebookId;
		this.name = name;
	}
	
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebook_id(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
