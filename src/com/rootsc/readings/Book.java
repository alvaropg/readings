package com.rootsc.readings;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

public class Book extends CouchDbDocument {
	private static final long serialVersionUID = 1L;
	private String type;
	private String title;
	private List<String> authors;

	public Book () {
		this.setType("Book");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public void addAuthor (String author) {
		if (this.authors == null) {
			this.authors = new ArrayList<String>();
		}
		this.authors.add(author);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
