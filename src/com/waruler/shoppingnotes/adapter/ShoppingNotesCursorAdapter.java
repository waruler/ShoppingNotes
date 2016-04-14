package com.waruler.shoppingnotes.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.waruler.shoppingnotes.R;
import com.waruler.shoppingnotes.bean.ShoppingNotesBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class ShoppingNotesCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	private SimpleDateFormat dateFormat;

	public ShoppingNotesCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = LayoutInflater.from(context);
		dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
	}

	@Override
	public Object getItem(int position) {
		ShoppingNotesBean notes = new ShoppingNotesBean();
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		notes.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		notes.setTitle(cursor.getString(cursor.getColumnIndex("note_title")));
		notes.setCreateTime(cursor.getLong(cursor.getColumnIndex("time")));
		return notes;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.shopping_notes_list_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view
				.findViewById(R.id.textview_notes_title);
		TextView statusText = (TextView) view
				.findViewById(R.id.textview_status);
		TextView createTime = (TextView) view
				.findViewById(R.id.textview_notes_create_time);
		title.setText(cursor.getString(1));
		long time = cursor.getLong(2);
		int status = cursor.getInt(3);
		createTime.setText(dateFormat.format(new Date(time)));
		statusText.setText(status == 0 ? "进行中" : "已完成");
	}
}
