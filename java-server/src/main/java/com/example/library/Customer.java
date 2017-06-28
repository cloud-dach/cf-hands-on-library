package com.example.library;

public class Customer{
	private String name;
	private String email;
	private String password;
	private String id;
	private String _id;
	private String _rev;
	
	public Customer(String id, String email, String password, String name){
		this.id=id;
		this._id=id;
		this.email=email;
		this.password=password;
		this.name=name;
	}
	
	public Customer(){	
	}
	
	public Customer(String _id, String id, String email, String password, String name, String _rev){
		this.id=id;
		this._id=_id;
		this.email=email;
		this.password=password;
		this.name=name;
		this._rev=_rev;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		this._id=id;
	}
	
	
}