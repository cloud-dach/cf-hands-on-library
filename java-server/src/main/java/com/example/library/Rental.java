package com.example.library;

import java.util.Date;

public class Rental{
	
	private String id;
	private String _id;
	private String _rev;
	private String bookid;
	private String customerid;
	private Date start;
	private Date end;
	
	public Rental(){
	}
	public Rental(String id, String _id, String _rev, String bookid, String customerid, Date end, Date start){
		this.id = id;
		this._id = _id;
		this._rev = _rev;
		this.bookid = bookid;
		this.customerid = customerid;
		this.start = start;
		this.end = end;
	}
	public Rental(String id, String bookid, String customerid, Date end, Date start){
		this.id = id;
		this._id = id;
		this.bookid = bookid;
		this.customerid = customerid;
		this.start = start;
		this.end = end;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		this._id = id;
	}
	public String getBookid() {
		return bookid;
	}
	public void setBookid(String bookid) {
		
		this.bookid = bookid;
		
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
	
}