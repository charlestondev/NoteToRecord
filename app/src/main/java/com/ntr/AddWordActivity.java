package com.ntr;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


public class AddWordActivity extends Activity {
	public DBAdapter dbAdapter;
	private Word word;
	private boolean edit;
	private int position;
	private String search;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_word_fragment);
		Bundle bundle = getIntent().getExtras();
		edit = false;
		position = -1;
		this.search = "";
		if(bundle!=null)
		{
			edit = bundle.getBoolean("edit");
			if(edit) {
				word = bundleToWord(bundle);
				position = bundle.getInt("position");
				View separatorChecked = findViewById(R.id.separator_checked);
				separatorChecked.setVisibility(View.VISIBLE);
				//View separatorReview = findViewById(R.id.separator_review);
				//separatorReview.setVisibility(View.VISIBLE);
				fillPanel(word);
			}
			else
			{
				this.search = bundle.getString("search").trim();
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                String pasteData = "";
                if (clipboardManager.hasPrimaryClip()){
                    if ((clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)))
                    {
                        ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                        pasteData = item.getText().toString();
                        if(pasteData!=null) {
                            EditText et = ((EditText) findViewById(R.id.key_word));
                            et.setText(pasteData);
                            //et.setTextColor(Word.LIGHT_COLOR_WORD);
                            ((EditText)findViewById(R.id.key_definition)).requestFocus();
                        }
                    }
                }
				((EditText)findViewById(R.id.key_word)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            EditText editText = ((EditText) findViewById(R.id.key_word));
                            dbAdapter = new DBAdapter(getApplicationContext());
                            Word existentWord = dbAdapter.getWord(editText.getText() + "");
                            if (existentWord != null) {
                                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.there_is_this_word), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, (int) editText.getY() + editText.getHeight());
                                toast.show();
                                getActionBar().setTitle(getResources().getString(R.string.edit));
                                fillPanel(existentWord);
                                AddWordActivity.this.word = existentWord;
                                AddWordActivity.this.edit = true;
                                position = dbAdapter.getPosition(existentWord.getId(),search)-1;
                            } else {
                                AddWordActivity.this.edit = false;
                                getActionBar().setTitle(getResources().getString(R.string.add));
                            }
                        }
                    }
                });
				((EditText)findViewById(R.id.key_word)).addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						AddWordActivity.this.edit = false;
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}
		}
		setResult(101);
	}
	public void fillPanel(Word word){
		((EditText)findViewById(R.id.key_word)).setText(word.getWord());
		((EditText)findViewById(R.id.key_definition)).setText(word.getDefinition());
		if(edit) {
			((CheckBox)findViewById(R.id.checked)).setChecked(word.getChecked()>=Config.getCheckTimes(getApplicationContext()));
			//((DatePicker)findViewById(R.id.datePicker)).updateDate();
		}
	}
	public Word bundleToWord(Bundle bundle){
		return new Word(bundle.getLong("id"), bundle.getString("word"), bundle.getString("definition"), bundle.getInt("checked"), bundle.getString("review"));
	}
	public void save(){
		EditText et = (EditText)findViewById(R.id.key_word);
        if(et.getText().toString().trim().equals("")) {
            Toast.makeText(this,getResources().getString(R.string.void_word),Toast.LENGTH_LONG).show();
            return;
        }
		String key_word = et.getText().toString();
		String key_definition = ((EditText)findViewById(R.id.key_definition)).getText().toString();
		boolean check = ((CheckBox)findViewById(R.id.checked)).isChecked();
		dbAdapter = new DBAdapter(this);
		if(!edit)
			word = dbAdapter.insertWord(key_word, key_definition);
		else {
			word = dbAdapter.updateWord(word.getId(), key_word, key_definition,check?Config.getCheckTimes(this):0);
		}
		Intent intent = new Intent();
		intent.putExtra("id", word.getId());
		intent.putExtra("word", word.getWord());
		intent.putExtra("definition", word.getDefinition());
		intent.putExtra("checked", word.getChecked());
		intent.putExtra("review", word.getReview());
        Log.i("position", position+"");
		intent.putExtra("position", position);
		setResult(100,intent);
		finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_word, menu);
		ActionBar actionBar = getActionBar();
		if(!this.edit)
			actionBar.setTitle(getString(R.string.add));
		else
			actionBar.setTitle(getResources().getString(R.string.edit));
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_save:
				save();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
