package com.waruler.shoppingnotes;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.waruler.shoppingnotes.adapter.ShoppingNotesCursorAdapter;
import com.waruler.shoppingnotes.bean.ShoppingNotesBean;
import com.waruler.shoppingnotes.db.NotesContentProvider.ShoppingNotes;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenu;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuCreator;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuItem;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuListView;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

public class ShoppingNotesMainActivity extends BaseActivity implements
		LoaderCallbacks<Cursor>, OnClickListener {

	private static final int ID = 10086;

	private ViewGroup mAdContainer;
	private SwipeMenuListView mNotesList;
	private ShoppingNotesCursorAdapter mAdapter;

	private Context mContext;

	private EditText mInputTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_notes_main);
		mContext = this;

		mActionBarBack.setOnClickListener(this);

		mAdContainer = (ViewGroup) findViewById(R.id.ad_container);
		mNotesList = (SwipeMenuListView) findViewById(R.id.listview_shopping_notes);
		View emptyView = findViewById(R.id.empty_view);
		mNotesList.setEmptyView(emptyView);

		mInputTitle = (EditText) findViewById(R.id.edittext_notes_title);
		findViewById(R.id.imageview_add).setOnClickListener(this);

		mAdapter = new ShoppingNotesCursorAdapter(mContext, null, true);
		mNotesList.setAdapter(mAdapter);

		// step 1. create a MenuCreator
		SwipeMenuCreator swipeMenuCreator = getSwipeMenuCreator(mContext);

		// set creator
		mNotesList.setMenuCreator(swipeMenuCreator);

		// step 2. listener item click event
		mNotesList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					ShoppingNotesBean notes = (com.waruler.shoppingnotes.bean.ShoppingNotesBean) mAdapter
							.getItem(position);
					int notesId = notes.getId();
					deleteNotesFromDb(mContext, notesId);
					break;
				}
			}
		});

		mNotesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShoppingNotesBean notes = (ShoppingNotesBean) mAdapter
						.getItem(position);
				Intent i = new Intent(mContext,
						ShoppingNotesDetailActivity.class);
				i.putExtra(ShoppingNotesDetailActivity.SHOPPING_NOTES_ID_KEY,
						notes.getId());
				i.putExtra(
						ShoppingNotesDetailActivity.SHOPPING_NOTES_TITLE_KEY,
						notes.getTitle());
				startActivity(i);
			}
		});

		getLoaderManager().initLoader(ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(mContext, ShoppingNotes.CONTENT_URI, null,
				null, null, "time ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageview_back:
			// actionbar的返回按钮

			break;

		case R.id.imageview_add:
			// 添加按钮,新建一条便签
			String title = mInputTitle.getText().toString();
			long time = System.currentTimeMillis();
			ContentValues values = new ContentValues();
			values.put("note_title", title);
			values.put("time", time);
			values.put("status", 0);
			getContentResolver().insert(ShoppingNotes.CONTENT_URI, values);
			mInputTitle.setText("");
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(mInputTitle.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			break;

		case R.id.edittext_notes_title:
			break;

		default:
			break;
		}
	}

	public static SwipeMenuCreator getSwipeMenuCreator(final Context context) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// // create "open" item
				// SwipeMenuItem openItem = new SwipeMenuItem(
				// getApplicationContext());
				// // set item background
				// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
				// 0xC9,
				// 0xCE)));
				// // set item width
				// openItem.setWidth(dp2px(90));
				// // set item title
				// openItem.setTitle("Open");
				// // set item title fontsize
				// openItem.setTitleSize(18);
				// // set item title font color
				// openItem.setTitleColor(Color.WHITE);
				// // add to menu
				// menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(context, 90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	private int deleteNotesFromDb(Context context, int id) {
		return context.getContentResolver().delete(ShoppingNotes.CONTENT_URI,
				"_id=?", new String[] { String.valueOf(id) });
	}

}
