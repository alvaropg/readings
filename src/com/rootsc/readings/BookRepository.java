package com.rootsc.readings;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class BookRepository extends CouchDbRepositorySupport<Book>
{
	private CouchDbConnector dbConnector;

	protected BookRepository(CouchDbConnector db) {
		super(Book.class, db);
		initStandardDesignDocument();
		this.dbConnector = db;
	}

	@Override
	public List<Book> getAll() {
		return queryView("all");
	};

	public List<Book> findByAuthor (String authorId) {
		return queryView("by_author", authorId);
	}

	public CouchDbConnector getDbConnector() {
		return dbConnector;
	}
}
