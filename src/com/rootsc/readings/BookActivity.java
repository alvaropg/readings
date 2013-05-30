package com.rootsc.readings;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class BookActivity extends Activity {

	private static final String TAG = "ReadingsApp";
	private Book book;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		Intent intent = getIntent();
		if (intent.getBooleanExtra("new", true)) {
			this.book = new Book();
		} else {
			try {
				this.book = (Book) intent.getSerializableExtra("book");
			} catch (Exception e) {
				Log.e(TAG, "Exception retrieving book", e);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book, menu);
		return true;
	}

	public void cancel(View view) {
		this.setResult(RESULT_CANCELED);
		this.finish();
	}

	public void ok(View view) {
		EditText titleTextView = (EditText) findViewById(R.id.activity_book_title);
		EditText authorTextView = (EditText) findViewById(R.id.activity_book_author);
		this.book.setTitle(titleTextView.getText().toString());
		this.book.addAuthor(authorTextView.getText().toString());

		Intent intent = new Intent();
		intent.putExtra("book", this.book);

		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
