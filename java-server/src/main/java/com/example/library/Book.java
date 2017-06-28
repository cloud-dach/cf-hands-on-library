package com.example.library;

import java.util.Collection;

public class Book{
	private String id;
	private String isbn;
	private Collection<String> authors;
	private String author;
	private String title;
	private String _id;
	private String _rev;
	private Collection<String> tags;
	private String picture;
	private String about_the_book;

	public Book(){
		
	}
	
	/**public Book(String id, String ISBN, String author, String title){
		this.id=id;
		this._id=id;
		this.isbn=ISBN;
		this.title=title;
		this.author=author;
	}*/
	
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id=id;
		this._id=id;
	}
	
	public String getISBN(){
		return this.isbn;
	}
	public void setISBN(String ISBN){
		this.isbn=ISBN;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public Collection<String> getTags() {
		return tags;
	}

	public void setTags(Collection<String> tags) {
		this.tags = tags;
	}

	public Collection<String> getAuthors() {
		return authors;
	}

	public void setAuthors(Collection<String> authors) {
		this.authors = authors;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAbout_the_book() {
		return about_the_book;
	}

	public void setAbout_the_book(String about_the_book) {
		this.about_the_book = about_the_book;
	}

	
}