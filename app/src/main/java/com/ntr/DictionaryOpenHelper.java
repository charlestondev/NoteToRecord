package com.ntr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by charleston on 18/02/14.
 */
public class DictionaryOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ntr.db";
    public static final String DICTIONARY_TABLE_NAME = "dictionary";
    public static final String ID = "_id";
    public static final String KEY_WORD = "key_word";
    public static final String KEY_DEFINITION = "key_definition";
	public static final String CHECKED = "checked";
	public static final String REVIEW = "review";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_WORD + " TEXT NOT NULL, " +
                    KEY_DEFINITION + " TEXT NOT NULL,"+
					CHECKED + " INTEGER DEFAULT 0,"+
					REVIEW +" TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DictionaryOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + "to" + newVersion + ", which will destroy all old data");
        //db.execSQL("DROP TABLE IF EXISTS " + DICTIONARY_TABLE_NAME);
        onCreate(db);
    }
}
