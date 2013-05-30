package com.rootsc.readings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.SwappedDataInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;
import org.ektorp.android.util.ChangesFeedAsyncTask;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.DocumentChange;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionListAdapter extends ArrayAdapter<Book>
{
	private static final String TAG = "ReadingsApp";

	private BookRepository booksRepo;
	private List<Book> books;

	private CollectionListAdapterChangesAsyncTask changesAsyncTask;
	private EktorpAsyncTask updateBooksTask;

	protected long lastUpdateChangesFeed = -1L;

	private static class ViewHolder {
		ImageView bookImage;
	    TextView bookTitle;
	    TextView bookAuthor;
	}

	public CollectionListAdapter(Context context, int textViewResourceId, BookRepository booksRepo) {
		super(context, textViewResourceId);
		this.booksRepo = booksRepo;
		this.books = new ArrayList<Book>();

		this.updateBooks();
	}

	protected void handleAsyncTaskDbAccessException(DbAccessException dbAccessException) {
		Log.e(TAG, "DbAccessException accessing view for books list", dbAccessException);
	}

	protected void updateBooks() {
		if(updateBooksTask == null) {
			updateBooksTask = new EktorpAsyncTask() {
				@Override
				protected void doInBackground() {
					Log.i(TAG, "A por ellos");
					books = booksRepo.getAll();
				}
	
				@Override
				protected void onSuccess() {
					if (books != null) {
						notifyDataSetChanged();
						Log.i(TAG, "succes y tenemos books");
					}

					updateBooksTask = null;
	
					if (changesAsyncTask == null) {
						ChangesCommand changesCmd = new ChangesCommand.Builder().since(lastUpdateChangesFeed)
								.includeDocs(false)
								.continuous(true)
								.heartbeat(5000)
								.build();
						changesAsyncTask = new CollectionListAdapterChangesAsyncTask(booksRepo.getDbConnector(), changesCmd);
						changesAsyncTask.execute();
					}
				}
	
				@Override
				protected void onDbAccessException(DbAccessException dbAccessException) {
					handleAsyncTaskDbAccessException(dbAccessException);
				}
			};
	
			updateBooksTask.execute();
		}
	}

	private class CollectionListAdapterChangesAsyncTask extends ChangesFeedAsyncTask
	{
		public CollectionListAdapterChangesAsyncTask(CouchDbConnector couchDbConnector, ChangesCommand changesCommand) {
			super(couchDbConnector, changesCommand);
		}

		@Override
		protected void handleDocumentChange(DocumentChange change) {
			Log.i(TAG,"Feed function");
			lastUpdateChangesFeed = change.getSequence();
			updateBooks();
		}

		@Override
		protected void onDbAccessException(DbAccessException dbAccessException) {
			handleAsyncTaskDbAccessException(dbAccessException);
		}

	}

	@Override
	public int getCount() {
		return this.books.size();
	}

	@Override
	public Book getItem(int position) {
		return this.books.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Book book = this.getItem(position);
		View row = convertView;
		ViewHolder holder;

        if (row == null) {
        	LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = mInflater.inflate(R.layout.book_list_item, parent, false);
            // initialize the elements
        	holder = new ViewHolder();
        	holder.bookImage = (ImageView) row.findViewById(R.id.imageViewBookIcon);
        	holder.bookTitle = (TextView) row.findViewById(R.id.textViewBookTitle);
        	holder.bookAuthor = (TextView) row.findViewById(R.id.textViewBookAuthor);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if (book != null) {
        	holder.bookImage.setImageResource(R.drawable.ic_launcher);
        	holder.bookTitle.setText(book.getTitle());
        	String authors = "";
        	for (String a : book.getAuthors()) {
        		authors += a + ", ";
        	}
        	holder.bookAuthor.setText(authors);
        }

        return row;
	}
}