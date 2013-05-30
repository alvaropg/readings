package com.rootsc.readings;

import java.io.IOException;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.couchbase.touchdb.TDDatabase;
import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.TDViewMapBlock;
import com.couchbase.touchdb.TDViewMapEmitBlock;
import com.couchbase.touchdb.ektorp.TouchDBHttpClient;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class CollectionActivity extends ListActivity {

	private static final String TAG = "ReadingsApp";

	public static final String DATABASE_NAME = "readings";

	public static final String dBookName = "Book";
	public static final String allBookViewName = "all";

	public static final int BOOK_INSERT = 0;

	protected static TDServer server;
	protected static HttpClient httpClient;
	protected CouchDbInstance dbInstance;
	protected CouchDbConnector dbConnector;

	protected BookRepository booksRepo;
	protected CollectionListAdapter collectionAdapter;

	//static inializer to ensure that touchdb URLs are handled properly
	{
    	TDURLStreamHandlerFactory.registerSelfIgnoreError();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);

		this.startTouchDB();
		this.startEktorp();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_book:
			Intent intent = new Intent(this, BookActivity.class);
			intent.putExtra("new", true);
			startActivityForResult(intent, BOOK_INSERT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == BOOK_INSERT) {
			Book book = (Book) data.getSerializableExtra("book");
			booksRepo.add(book);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void startTouchDB() {
	    String filesDir = getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }

	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NAME);
	    TDView view = db.getViewNamed(String.format("Book/all", dBookName, allBookViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {
	    	@Override
			public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
				String type = (String) document.get("type");
                if ("Book".equals(type)){
                	emitter.emit(null, document.get("_id"));
                }
			}
        }, null, "1.0");
	}

	protected void startEktorp () {
		if(httpClient != null) {
			httpClient.shutdown();
		}
		httpClient = new TouchDBHttpClient(server);
		dbInstance = new StdCouchDbInstance(httpClient);

		EktorpAsyncTask asyncTask = new EktorpAsyncTask() {
			@Override
			protected void doInBackground() {
				dbConnector = dbInstance.createConnector(DATABASE_NAME, true);
			}
			@Override
			protected void onSuccess() {
				booksRepo = new BookRepository(dbConnector);
				collectionAdapter = new CollectionListAdapter(CollectionActivity.this, R.layout.book_list_item, booksRepo);
				setListAdapter(collectionAdapter);
			}
		};
		asyncTask.execute();
	}
}
