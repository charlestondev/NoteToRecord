package com.ntr;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by charleston on 19/02/14.
 */
public class SlidingPaneActivity extends Activity{
	public SlidingPaneLayout pane;
	public ListWordsFragment list;
	public ViewWordFragment details;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sliding_pane_layout);
		pane = (SlidingPaneLayout) findViewById(R.id.sp);
		pane.setPanelSlideListener(new PaneListener());
		pane.openPane();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.leftpane, new ListWordsFragment());
        fragmentTransaction.add(R.id.rightpane, new ViewWordFragment()).commit();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sliding_pane, menu);
		ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(R.layout.serch_field);
		EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				list.filterAdapter(s+"");
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		/*search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				list.filterAdapter(v.getText()+"");
				return false;
			}
		});*/
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_add:
				EditText search = (EditText) getActionBar().getCustomView().findViewById(R.id.searchfield);
				list.addWord(search.getText()+"");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class PaneListener implements SlidingPaneLayout.PanelSlideListener {
		//Todo remove System.out
		@Override
		public void onPanelClosed(View view) {
			//System.out.println("Panel closed");
			if(list.actionMode != null)
				list.actionMode.finish();
		}

		@Override
		public void onPanelOpened(View view) {
			//System.out.println("Panel opened");
		}

		@Override
		public void onPanelSlide(View view, float arg1) {
			//System.out.println("Panel sliding");
		}
	}
}