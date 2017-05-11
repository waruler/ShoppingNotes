package com.waruler.shoppingnotes.db;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class NotesContentProvider extends ContentProvider {

	private static final String TAG = "NotesContentProvider";

	/** provider authority */
	public static final String AUTHORITY = "com.waruler.notes";

	private static final String MIME_PREFIX_DIR = "vnd.android.cursor.dir";
	private static final String MIME_PREFIX_ITEM = "vnd.android.cursor.item";

	/** provider type path */
	private static final String SHOPPING_NOTES = "shopping_notes";
	private static final String SHOPPING_NOTES_DETAIL = "shopping_notes_detail";

	/** provider type */
	private static final int TYPE_SHOPPING_NOTES_ALL = 1;
	private static final int TYPE_SHOPPING_NOTES = 2;
	private static final int TYPE_SHOPPING_NOTES_DETAIL_ALL = 3;
	private static final int TYPE_SHOPPING_NOTES_DETAIL = 4;

	private static final UriMatcher matcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		matcher.addURI(AUTHORITY, SHOPPING_NOTES, TYPE_SHOPPING_NOTES_ALL);
		matcher.addURI(AUTHORITY, SHOPPING_NOTES + "/#", TYPE_SHOPPING_NOTES);
		matcher.addURI(AUTHORITY, SHOPPING_NOTES_DETAIL,
				TYPE_SHOPPING_NOTES_DETAIL_ALL);
		matcher.addURI(AUTHORITY, SHOPPING_NOTES_DETAIL + "/#",
				TYPE_SHOPPING_NOTES_DETAIL);
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	/**
	 * 购物便签表
	 */
	public static class ShoppingNotes {
		static final String NAME = DbHelper.SHOPPING_NOTES_TABLE_NAME;
		public static final Uri CONTENT_URI = Uri.parse(String.format(
				"content://%s/%s", AUTHORITY, SHOPPING_NOTES));
	}

	/**
	 * 购物便签详情表
	 */
	public static class ShoppingNotesDetail {
		static final String NAME = DbHelper.SHOPPING_NOTES_DETAIL_TABLE_NAME;
		public static final Uri CONTENT_URI = Uri.parse(String.format(
				"content://%s/%s", AUTHORITY, SHOPPING_NOTES_DETAIL));
	}

	@Override
	public String getType(Uri uri) {
		String type = null;
		switch (matcher.match(uri)) {
		case TYPE_SHOPPING_NOTES_ALL:
			type = String.format("%s/%s", MIME_PREFIX_DIR, SHOPPING_NOTES);
			break;

		case TYPE_SHOPPING_NOTES:
			type = String.format("%s/%s", MIME_PREFIX_ITEM, SHOPPING_NOTES);
			break;

		case TYPE_SHOPPING_NOTES_DETAIL_ALL:
			type = String.format("%s/%s", MIME_PREFIX_DIR,
					SHOPPING_NOTES_DETAIL);
			break;

		case TYPE_SHOPPING_NOTES_DETAIL:
			type = String.format("%s/%s", MIME_PREFIX_DIR,
					SHOPPING_NOTES_DETAIL);
			break;

		default:
			type = "";
			throw new IllegalArgumentException("Unknow Uri:" + uri);
		}
		return type;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = DbHelper.getDbHelper(getContext())
				.getReadableDatabase();
		long id = -1;
		String tableName;
		switch (matcher.match(uri)) {
		case TYPE_SHOPPING_NOTES:
			id = ContentUris.parseId(uri);
		case TYPE_SHOPPING_NOTES_ALL:
			tableName = SHOPPING_NOTES;
			break;

		case TYPE_SHOPPING_NOTES_DETAIL:
			id = ContentUris.parseId(uri);
		case TYPE_SHOPPING_NOTES_DETAIL_ALL:
			tableName = SHOPPING_NOTES_DETAIL;
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri:" + uri);
		}
		if (id > -1) {
			selection = String.format("_id=%s AND (%s)", id, selection);
		}

		if (!db.isOpen())
			return null;
		Cursor c = null;
		try {
			c = db.query(tableName, projection, selection, selectionArgs, null,
					null, sortOrder);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		if (c != null) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return c;

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = DbHelper.getDbHelper(getContext())
				.getWritableDatabase();
		long id = -1;
		boolean isNotes = false;
		boolean isNotesDetail = false;
		switch (matcher.match(uri)) {
		case TYPE_SHOPPING_NOTES:
		case TYPE_SHOPPING_NOTES_ALL:
			isNotes = true;
			id = insert(db, DbHelper.SHOPPING_NOTES_TABLE_NAME, values);
			break;

		case TYPE_SHOPPING_NOTES_DETAIL:
		case TYPE_SHOPPING_NOTES_DETAIL_ALL:
			isNotesDetail = true;
			id = insert(db, DbHelper.SHOPPING_NOTES_DETAIL_TABLE_NAME, values);
			break;

		default:
			throw new IllegalArgumentException("Unknow Uri:" + uri);
		}

		if (id > -1) {
			Uri newUri = ContentUris.withAppendedId(uri, id);
			ContentResolver contentResolver = getContext().getContentResolver();
			contentResolver.notifyChange(newUri, null);
			if (isNotes) {
				contentResolver.notifyChange(ShoppingNotes.CONTENT_URI, null);
			}
			if (isNotesDetail) {
				contentResolver.notifyChange(ShoppingNotesDetail.CONTENT_URI,
						null);
			}
			return newUri;
		}

		return null;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		return super.bulkInsert(uri, values);
	}

	/**
	 * 添加数据
	 * 
	 * @param db
	 *            数据库
	 * @param tableName
	 *            数据库名
	 * @param values
	 * @return id
	 */
	private long insert(SQLiteDatabase db, String tableName,
			ContentValues values) {
		long id = -1;
		if (!db.isOpen())
			return id;
		try {
			id = db.replace(tableName, null, values);
			Log.i(TAG, "id=" + id + " insert:" + tableName + ":" + values);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		return id;
	}

	/**
	 * 
	 * @param db
	 *            数据库
	 * @param tableName
	 *            表名
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	private int delete(SQLiteDatabase db, String tableName, String whereClause,
			String[] whereArgs) {
		int id = -1;
		if (!db.isOpen())
			return id;
		try {
			id = db.delete(tableName, whereClause, whereArgs);
			Log.i(TAG, "id=" + id + " delete:" + tableName + "[" + whereClause
					+ "," + Arrays.toString(whereArgs) + "]");
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}

		return id;
	}

	/**
	 * 在删除TYPE_SHOPPING_NOTES表的时候,必须将_id的值放在whereArgs的第一位置,<br>
	 * 以方便删除TYPE_SHOPPING_NOTES_DETAIL里的具体内容
	 * 
	 * **/
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = DbHelper.getDbHelper(getContext())
				.getWritableDatabase();
		int count = 0;
		boolean isNotes = false;
		boolean isNotesDetail = false;

		switch (matcher.match(uri)) {
		case TYPE_SHOPPING_NOTES:
		case TYPE_SHOPPING_NOTES_ALL:
			isNotes = true;
			count = delete(db, DbHelper.SHOPPING_NOTES_TABLE_NAME, whereClause,
					whereArgs);
			if (whereClause.contains("_id")) {
				if (whereArgs != null && whereArgs.length > 0) {
					db.delete(SHOPPING_NOTES_DETAIL, "notes_id=?", new String[]{whereArgs[0]});
				}
			}
			break;

		case TYPE_SHOPPING_NOTES_DETAIL:
		case TYPE_SHOPPING_NOTES_DETAIL_ALL:
			isNotesDetail = true;
			count = delete(db, DbHelper.SHOPPING_NOTES_DETAIL_TABLE_NAME,
					whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri:" + uri);
		}
		if (count > 0) {
			ContentResolver cr = this.getContext().getContentResolver();
			cr.notifyChange(uri, null);
			if (isNotes) {
				cr.notifyChange(ShoppingNotes.CONTENT_URI, null);
			}
			if (isNotesDetail) {
				cr.notifyChange(ShoppingNotesDetail.CONTENT_URI, null);
			}
		}
		return count;
	}

	private int update(SQLiteDatabase db, String tableName,
			ContentValues values, String whereClause, String[] whereArgs) {
		int count = -1;
		if (!db.isOpen())
			return count;
		try {
			count = db.update(tableName, values, whereClause, whereArgs);
			Log.i(TAG, "count=" + count + " update:" + tableName + "["
					+ whereClause + "," + Arrays.toString(whereArgs) + "]");
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,
			String[] whereArgs) {
		if (values == null)
			return -1;
		int count = 0;
		SQLiteDatabase db = DbHelper.getDbHelper(getContext())
				.getWritableDatabase();
		boolean isNotes = false;
		boolean isNotesDetail = false;

		switch (matcher.match(uri)) {
		case TYPE_SHOPPING_NOTES:
		case TYPE_SHOPPING_NOTES_ALL:
			isNotes = true;
			count = update(db, DbHelper.SHOPPING_NOTES_TABLE_NAME, values,
					whereClause, whereArgs);
			break;

		case TYPE_SHOPPING_NOTES_DETAIL:
		case TYPE_SHOPPING_NOTES_DETAIL_ALL:
			isNotesDetail = true;
			count = update(db, DbHelper.SHOPPING_NOTES_DETAIL_TABLE_NAME,
					values, whereClause, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknow Uri:" + uri);
		}

		if (count > 0) {
			ContentResolver resolver = getContext().getContentResolver();
			resolver.notifyChange(uri, null);

			if (isNotes) {
				resolver.notifyChange(ShoppingNotes.CONTENT_URI, null);
			}
			if (isNotesDetail) {
				resolver.notifyChange(ShoppingNotesDetail.CONTENT_URI, null);
			}

		}

		return count;
	}

}
