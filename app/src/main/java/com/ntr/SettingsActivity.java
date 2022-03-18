package com.ntr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ListView;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;

/**
 * Created by charleston on 25/03/14.
 */
public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
        AdBuddiz.showAd(this);
	}

    @Override
    protected void onPause() {
        int[] mAppWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this,NoteWidget.class));
        NoteWidget.configChanged(this, mAppWidgetIds);
        Log.i("teste", "paused");
        super.onPause();
    }
}
