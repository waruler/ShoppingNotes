package com.waruler.shoppingnotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.waruler.shoppingnotes.db.NotesContentProvider.ShoppingNotes;
import com.waruler.shoppingnotes.db.NotesContentProvider.ShoppingNotesDetail;

public class DbUtils {

	public static void updateShoppingNotesTable(Context context, int notesId) {
		Cursor cursor = context.getContentResolver().query(
				ShoppingNotesDetail.CONTENT_URI, new String[] { "count(*)" },
				"notes_id=? AND status=0",
				new String[] { String.valueOf(notesId) }, null);
		if (cursor != null) {
			ContentValues values = new ContentValues();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				values.put("status", count == 0 ? 1 : 0);
			}
			context.getContentResolver().update(ShoppingNotes.CONTENT_URI,
					values, "_id=?", new String[] { String.valueOf(notesId) });
			cursor.close();
		}
	}

}
