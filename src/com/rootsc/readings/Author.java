package com.rootsc.readings;

import org.ektorp.support.CouchDbDocument;

public class Author extends CouchDbDocument {
	private static final long serialVersionUID = 1L;
	private String type;
	private String name;

	public Author() {
		this.type = "Author";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
