package com.ntr;

import android.graphics.Color;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by charleston on 21/02/14.
 */
public class Word {
	private long id;
	private String word;
	private String definition;
	private int checked;
	private String review;
	public static final String STRING_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static int LIGHT_COLOR_WORD= Color.rgb(190, 170, 240);
    public static int NORMAL_COLOR_WORD = Color.rgb(34,0,153);
	public Word(long id, String word, String definition){
		this.id = id;
		this.word = word;
		this.definition = definition;
		this.checked = 0;
		this.review = null;
	}
	public Word(long id, String word, String definition, int checked,String review){
		this.id = id;
		this.word = word;
		this.definition = definition;
		this.checked = checked;
		this.review = review;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public int getChecked(){
		return this.checked;
	}
	public void setChecked(int checked){
		this.checked = checked;
	}
	public String getReview(){return this.review; }
	public void setReview(String review){this.review = review;}
	public String toString(){
		return this.word;
	}
}
