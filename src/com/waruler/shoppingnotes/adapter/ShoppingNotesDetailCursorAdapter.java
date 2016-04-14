package com.waruler.shoppingnotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.waruler.shoppingnotes.R;
import com.waruler.shoppingnotes.bean.ShoppingNotesDetailBean;

@SuppressLint("InflateParams")
public class ShoppingNotesDetailCursorAdapter extends CursorAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	public ShoppingNotesDetailCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public Object getItem(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		ShoppingNotesDetailBean notesDetail = new ShoppingNotesDetailBean();
		notesDetail.setId(cursor.getInt(0));
		notesDetail.setListId(cursor.getInt(1));
		notesDetail.setProductName(cursor.getString(2));
		notesDetail.setProductCount(cursor.getInt(3));
		notesDetail.setStatus(cursor.getInt(4));

		return notesDetail;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater
				.inflate(R.layout.shopping_notes_detail_list_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view
				.findViewById(R.id.textview_product_name);
		TextView count = (TextView) view
				.findViewById(R.id.textview_product_count);
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.checkbox_shopping_status);
		name.setText(cursor.getString(2));
		count.setText(cursor.getInt(3) + "");
		int status = cursor.getInt(4);
		checkBox.setChecked(status == 1 ? true : false);
	}

}
