package com.waruler.shoppingnotes;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.waruler.shoppingnotes.adapter.ShoppingNotesDetailCursorAdapter;
import com.waruler.shoppingnotes.bean.ShoppingNotesDetailBean;
import com.waruler.shoppingnotes.db.DbUtils;
import com.waruler.shoppingnotes.db.NotesContentProvider.ShoppingNotesDetail;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenu;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuCreator;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuListView;
import com.waruler.shoppingnotes.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

public class ShoppingNotesDetailActivity extends BaseActivity implements
		LoaderCallbacks<Cursor>, OnClickListener {

	public static final String SHOPPING_NOTES_ID_KEY = "shopping_notes_id";
	public static final String SHOPPING_NOTES_TITLE_KEY = "shopping_notes_title";
	private static final int ID = 10088;
	private int mNotesId;

	private Context mContext;
	private ShoppingNotesDetailCursorAdapter mAdapter;
	private SwipeMenuListView mListView;
	private EditText mProductNameEditText;
	private EditText mProductCountEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_shopping_notes_detail);
		mContext = this;

		Intent intent = getIntent();
		mNotesId = intent.getIntExtra(SHOPPING_NOTES_ID_KEY, -1);
		String title = intent.getStringExtra(SHOPPING_NOTES_TITLE_KEY);
		if (!TextUtils.isEmpty(title)) {
			mActionBarTitle.setText(title);
		}

		mActionBarBack.setOnClickListener(this);
		mProductNameEditText = (EditText) findViewById(R.id.edittext_detail_product_name);
		mProductCountEditText = (EditText) findViewById(R.id.edittext_detail_product_count);
		findViewById(R.id.imageview_add).setOnClickListener(this);

		mListView = (SwipeMenuListView) findViewById(R.id.listview_shopping_notes_detail);
		mListView.setEmptyView(findViewById(R.id.empty_view));

		mAdapter = new ShoppingNotesDetailCursorAdapter(mContext, null, true);
		mListView.setAdapter(mAdapter);

		// step 1. create a MenuCreator
		SwipeMenuCreator swipeMenuCreator = ShoppingNotesMainActivity
				.getSwipeMenuCreator(mContext);

		// set creator
		mListView.setMenuCreator(swipeMenuCreator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					ShoppingNotesDetailBean detail = (ShoppingNotesDetailBean) mAdapter
							.getItem(position);
					getContentResolver().delete(
							ShoppingNotesDetail.CONTENT_URI,
							"notes_id=? AND _id=?",
							new String[] { String.valueOf(mNotesId),
									String.valueOf(detail.getId()) });
					DbUtils.updateShoppingNotesTable(mContext, mNotesId);
					break;
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShoppingNotesDetailBean detail = (ShoppingNotesDetailBean) mAdapter
						.getItem(position);
				int status = detail.getStatus();
				ContentValues values = new ContentValues();
				values.put("status", status == 0 ? 1 : 0);
				getContentResolver().update(
						ShoppingNotesDetail.CONTENT_URI,
						values,
						"notes_id=? AND _id=?",
						new String[] { String.valueOf(mNotesId),
								String.valueOf(detail.getId()) });
				DbUtils.updateShoppingNotesTable(mContext, mNotesId);
			}
		});

		getLoaderManager().initLoader(ID, null, this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(mContext, ShoppingNotesDetail.CONTENT_URI,
				null, "notes_id=?", new String[] { String.valueOf(mNotesId) },
				"_id ASC");
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
		case R.id.imageview_add:
			String productName = mProductNameEditText.getText().toString();
			String productCount = mProductCountEditText.getText().toString();
			if (!TextUtils.isEmpty(productCount)
					&& !TextUtils.isEmpty(productName)) {
				ContentValues values = new ContentValues();
				values.put("name", productName);
				values.put("count", Integer.parseInt(productCount));
				values.put("status", 0);
				values.put("notes_id", mNotesId);
				getContentResolver().insert(ShoppingNotesDetail.CONTENT_URI,
						values);
				mProductNameEditText.setText("");
				mProductCountEditText.setText("");
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(
						mProductNameEditText.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				DbUtils.updateShoppingNotesTable(mContext, mNotesId);
			} else {
				Toast.makeText(mContext, "请输入商品名称和数量", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.imageview_back:
			// actionbar的返回按钮
			finish();
			break;
		default:
			break;
		}
	}
}
