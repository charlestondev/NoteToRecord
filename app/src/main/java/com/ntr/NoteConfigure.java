package com.ntr;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class NoteConfigure extends Activity {
	private int mode;
	private RemoteViews views;
	private SharedPreferences prefs;
	SharedPreferences.Editor editor;
	private int mAppWidgetId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        AdBuddiz.setPublisherKey("423b41d6-6c96-4c3e-92c4-95bef83619c7");
        AdBuddiz.cacheAds(NoteConfigure.this);
		setContentView(R.layout.notetorecord_configure);
		mAppWidgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		prefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), this.MODE_PRIVATE);
		mode = prefs.getInt("mode"+mAppWidgetId, 0);
		String note = prefs.getString("note"+mAppWidgetId,"");
		((EditText)findViewById(R.id.note)).setText(note);
		initActionBar();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_configure, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_settings:
				this.openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void initActionBar(){
		final SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.modes,android.R.layout.simple_spinner_dropdown_item);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getResources().getText(R.string.mode));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ActionBar.OnNavigationListener mOnNavigationListener = new ActionBar.OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				TextView tip = (TextView) findViewById(R.id.tip);
				tip.setText(getResources().getStringArray(R.array.modes_desc)[itemPosition]);
				if(itemPosition!=0) {
					findViewById(R.id.note).setVisibility(View.GONE);
					findViewById(R.id.btn_manage_item).setVisibility(View.VISIBLE);
					if(itemPosition == 1)
						((Button)findViewById(R.id.btn_manage_item)).setText(getResources().getString(R.string.manage_cards));
					else
						((Button)findViewById(R.id.btn_manage_item)).setText(getResources().getString(R.string.manage_memos));
				}
				else {
					findViewById(R.id.note).setVisibility(View.VISIBLE);
					findViewById(R.id.btn_manage_item).setVisibility(View.GONE);
				}
				return false;
			}
		};
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
		actionBar.setSelectedNavigationItem(mode);
	}
	public void save(){
		editor = prefs.edit();
		editor.putInt("mode"+mAppWidgetId, getActionBar().getSelectedNavigationIndex());

		if(getActionBar().getSelectedNavigationIndex() == 0)
		{
			String note = ((EditText)findViewById(R.id.note)).getText().toString();
			note.trim();
			editor.putString("note"+mAppWidgetId, note);
		}
		else
		{
		}
		editor.commit();
		int[] mAppWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this,NoteWidget.class));
		NoteWidget.configChanged(this, mAppWidgetIds);
		finish();
	}
	public void openSettings(){
		Intent intent =  new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	public void startListWords(View v){
		Intent intent = new Intent(this,SlidingPaneActivity.class);
		startActivity(intent);

	}

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Activity", "paused");
        this.save();
    }
}