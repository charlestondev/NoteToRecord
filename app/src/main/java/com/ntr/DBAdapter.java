package com.ntr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by charleston on 18/02/14.
 */
public class DBAdapter {

	private SQLiteDatabase database;
	private DictionaryOpenHelper doHelper;
	private Context context;
	private String[] allColumns = { DictionaryOpenHelper.ID, DictionaryOpenHelper.KEY_WORD, DictionaryOpenHelper.KEY_DEFINITION, DictionaryOpenHelper.CHECKED, DictionaryOpenHelper.REVIEW};

	public DBAdapter(Context context) {
		doHelper = new DictionaryOpenHelper(context);
		database = doHelper.getWritableDatabase();
		this.context = context;
	}

	public Word insertWord(String key_word, String key_definition) {
		ContentValues values = new ContentValues();
		values.put(doHelper.KEY_WORD, key_word);
		values.put(doHelper.KEY_DEFINITION,key_definition);
		long insertId = database.insert(doHelper.DICTIONARY_TABLE_NAME, null, values);
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME, allColumns, doHelper.ID + " = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Word word = new Word(insertId,cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
		cursor.close();
		return word;
	}

	public Word updateWord(long id, String key_word, String key_definition, int checked, String review){
		ContentValues values = new ContentValues();
		values.put(doHelper.KEY_WORD, key_word);
		values.put(doHelper.KEY_DEFINITION,key_definition);
		values.put(doHelper.CHECKED, checked);
		if(checked<Config.getCheckTimes(this.context)) {
			values.put(doHelper.REVIEW, review);
		}else{
			String stringToday = getStringToday();
			values.put(doHelper.REVIEW,stringToday);
		}
		database.update(doHelper.DICTIONARY_TABLE_NAME,values,doHelper.ID+"="+id,null);
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME, allColumns, doHelper.ID + " = " + id, null,null, null, null);
		cursor.moveToFirst();
		Word word = new Word(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
		cursor.close();
		return word;
	}
	public Word updateWord(long id, String key_word,String key_definition, int checked)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(Word.STRING_DATE_FORMAT);
		Date date = new Date();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return updateWord(id, key_word, key_definition, checked, dateFormat.format(date));
	}
	public Word updateWord(long id, String key_word, String key_definition){
		SimpleDateFormat dateFormat = new SimpleDateFormat(Word.STRING_DATE_FORMAT);
		Date date = new Date();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return updateWord(id, key_word, key_definition, 0, dateFormat.format(date));
	}
	public Word getWord(String word_word)
	{
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME,allColumns,doHelper.KEY_WORD+" LIKE ?", new String[]{word_word}, null, null, null);
		cursor.moveToFirst();
		Word word = null;
		if(!cursor.isAfterLast())
			word = new Word(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
		return word;
	}
	public int getPosition(long id, String whereClause){
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME,new String[]{doHelper.ID},doHelper.ID+" <= "+id+" and "+doHelper.KEY_WORD+" LIKE ?",new String[]{"%"+whereClause+"%"},null,null,doHelper.ID);
		return cursor.getCount();
	}
	public int getPosition(long id){
		return getPosition(id,"");
	}
	public void removeWord(Word word){
		database.delete(doHelper.DICTIONARY_TABLE_NAME, doHelper.ID + " = " + word.getId(),
					null);
	}

	public List<Word> getAllWords(String whereClause, String whereArgs[]){
		List<Word> words = new ArrayList<Word>();
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME,allColumns,whereClause,whereArgs,null,null,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Word word = new Word(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
			words.add(word);
			cursor.moveToNext();
		}
		cursor.close();
		return words;
	}
	public List<Word> getAllWords(){
		return getAllWords(null,null);
	}

	public Word nextWord(Word word){

		Calendar gap = Calendar.getInstance();
		gap.setTime(new Date());
		gap.add(Calendar.DAY_OF_MONTH, -Config.getGapDays(this.context));
		SimpleDateFormat sdf = new SimpleDateFormat(Word.STRING_DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringGap = sdf.format(gap.getTime());
		String[] selectionArgs = new String[]{stringGap};
		String selection = doHelper.CHECKED+"<"+Config.getCheckTimes(this.context)+" or "+doHelper.REVIEW+"<?";
		if(word!=null)
		{
			long id = word.getId();
			selection = "("+doHelper.CHECKED+"<"+Config.getCheckTimes(this.context)+" or "+doHelper.REVIEW+"<?) and "+doHelper.ID+">"+id;
		}
		Cursor cursor = database.query(doHelper.DICTIONARY_TABLE_NAME,allColumns,selection,selectionArgs,null,null,doHelper.ID);
		cursor.moveToFirst();
		Word nextWord = null;
		if(!cursor.isAfterLast()) {
			nextWord = new Word(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4));
			Log.i("checked", nextWord.getChecked()+"");
			if(nextWord.getChecked()>=Config.getCheckTimes(this.context)) {
				nextWord = updateWord(nextWord.getId(), nextWord.getWord(), nextWord.getDefinition(), 0, nextWord.getReview());
			}
		}
		cursor.close();
		return nextWord;
	}
	public static String getStringToday(){
		Calendar today = Calendar.getInstance();
		today.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(Word.STRING_DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringToday = sdf.format(today.getTime());
		return stringToday;
	}
	public Word nextWord()
	{
		return nextWord(null);
	}
}
