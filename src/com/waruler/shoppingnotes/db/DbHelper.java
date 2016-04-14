package com.waruler.shoppingnotes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建和升级数据库的帮助类
 * 
 * @author waruler
 * **/
public class DbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "shopping_notes.db";
	private static final int DB_VERSION = 1;

	public static final String SHOPPING_NOTES_TABLE_NAME = "shopping_notes";
	public static final String SHOPPING_NOTES_DETAIL_TABLE_NAME = "shopping_notes_detail";

	private static DbHelper mHelper;

	private DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, version);
	}

	public static DbHelper getDbHelper(Context context) {
		if (mHelper == null) {
			mHelper = new DbHelper(context, DB_NAME, null, DB_VERSION);
		}

		return mHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("CREATE TABLE ")
				.append(SHOPPING_NOTES_TABLE_NAME)
				.append("( _id INTEGER PRIMARY KEY AUTOINCREMENT, note_title TEXT,time INTEGER ,status Integer )");
		db.execSQL(sb.toString());

		sb.delete(0, sb.length());
		sb.append("CREATE TABLE ")
				.append(SHOPPING_NOTES_DETAIL_TABLE_NAME)
				.append("( _id INTEGER PRIMARY KEY AUTOINCREMENT, notes_id INTEGER, name TEXT NOT NULL , count Integer , status Integer)");
		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)"); //往表中增加一列

		db.execSQL("DROP TABLE IF EXIST " + SHOPPING_NOTES_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXIST " + SHOPPING_NOTES_DETAIL_TABLE_NAME);
		onCreate(db);
	}

}
