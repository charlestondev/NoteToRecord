package com.ntr;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NoteWidget extends AppWidgetProvider {
	static final String ACTION_CHECK = "com.ntr.action.CHECK";
	static final String ACTION_SHOW = "com.ntr.action.SHOW";
	static final String ACTION_NEXT = "com.ntr.action.NEXT";
	static final String ACTION_CONFIG_CHANGED = "com.ntr.action.CONFIG_CHANGED";
	public static Word word;
	public static boolean show = false;
	public static DBAdapter dbAdapter;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Toast.makeText(context,"onUpdate start",Toast.LENGTH_LONG).show();
        final int N = appWidgetIds.length;
		if(NoteWidget.word == null) {
			NoteWidget.word = dbAdapter.nextWord();
			if(NoteWidget.word == null)
				NoteWidget.word = new Word(0,context.getResources().getString(R.string.no_words),"", 0, null);
		}
		SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);

            int mode = prefs.getInt("mode"+appWidgetId, 0);
			if(mode==0)
			{
				String note = prefs.getString("note"+appWidgetId,"");
				remoteViews.setTextViewText(R.id.word, note);
				remoteViews.setViewVisibility(R.id.definition,View.GONE);
				remoteViews.setViewVisibility(R.id.knowed,View.GONE);
				remoteViews.setViewVisibility(R.id.nknowed,View.GONE);
				remoteViews.setViewVisibility(R.id.next,View.GONE);
			}
			else if(mode==1)
			{
				remoteViews.setTextViewText(R.id.word, NoteWidget.word.getWord());
				remoteViews.setTextViewText(R.id.definition, NoteWidget.word.getDefinition());
				if(NoteWidget.show)
					remoteViews.setViewVisibility(R.id.definition,View.VISIBLE);
				else
					remoteViews.setViewVisibility(R.id.definition,View.GONE);
				remoteViews.setViewVisibility(R.id.knowed,View.GONE);
				remoteViews.setViewVisibility(R.id.nknowed,View.VISIBLE);
				remoteViews.setViewVisibility(R.id.next, View.VISIBLE);

				Intent intent = new Intent(context, NoteWidget.class);
				intent.setAction(ACTION_NEXT);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
				intent.putExtra("thereIsFlashCards", true);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context,appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.next, pendingIntent);

				Intent intent2 = new Intent(context, NoteWidget.class);
				intent2.setAction(ACTION_SHOW);
				intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
				intent2.putExtra("word_id",NoteWidget.word.getId());
				PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context,appWidgetId, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.nknowed, pendingIntent2);
			}
			else
			{
				remoteViews.setTextViewText(R.id.word, NoteWidget.word.getWord());
				remoteViews.setTextViewText(R.id.definition, NoteWidget.word.getDefinition());
				remoteViews.setViewVisibility(R.id.definition,View.VISIBLE);
				remoteViews.setViewVisibility(R.id.knowed,View.VISIBLE);
				remoteViews.setViewVisibility(R.id.nknowed,View.GONE);
				remoteViews.setViewVisibility(R.id.next, View.VISIBLE);

				Intent intent = new Intent(context, NoteWidget.class);
				intent.setAction(ACTION_NEXT);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context,appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.next, pendingIntent);

				Intent intent2 = new Intent(context, NoteWidget.class);
				intent2.setAction(ACTION_CHECK);
				intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
				intent2.putExtra("word_id",NoteWidget.word.getId());
				PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context,appWidgetId, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.knowed, pendingIntent2);
			}

			Intent aIntent = new Intent(context, NoteConfigure.class);
			aIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

			PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget,pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.word,pendingIntent);
			reArrange(context,appWidgetManager,appWidgetId,remoteViews);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            Toast.makeText(context,"onUpdate end",Toast.LENGTH_LONG).show();
        }
    }

	@Override
	public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
		Bundle extras = intent.getExtras();
		if(NoteWidget.dbAdapter==null)
		{
			NoteWidget.dbAdapter = new DBAdapter(context);
		}
		if(intent.getAction().equals(ACTION_CHECK)) {
			Intent i = new Intent(context,NoteWidget.class);
			i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS));
			Log.i("id   ",NoteWidget.word.getId()+"");

			if(NoteWidget.word.getId()!=0) {
				Log.i("id   ",word.getId()+"");
				//String stringToday = DBAdapter.getStringToday();
				NoteWidget.dbAdapter.updateWord(NoteWidget.word.getId(), NoteWidget.word.getWord(), NoteWidget.word.getDefinition(), NoteWidget.word.getChecked()+1, NoteWidget.word.getReview());
			}
			context.sendBroadcast(i);
		}
		else if(intent.getAction().equals(ACTION_NEXT))
		{
			Log.i("next", "metodo next");
			boolean flashCards = extras.getBoolean("thereIsFlashCards", false);
			if(NoteWidget.word!=null) {
				if (flashCards && !show && NoteWidget.word.getId() != 0) {
					//String stringToday = DBAdapter.getStringToday();
					NoteWidget.dbAdapter.updateWord(NoteWidget.word.getId(), NoteWidget.word.getWord(), NoteWidget.word.getDefinition(), NoteWidget.word.getChecked() + 1, NoteWidget.word.getReview());
				}
				NoteWidget.word = NoteWidget.dbAdapter.nextWord(NoteWidget.word);
			}
			NoteWidget.show = false;
			Intent i = new Intent(context, NoteWidget.class);
			i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS));
			context.sendBroadcast(i);
		}
		else if(intent.getAction().equals(ACTION_SHOW))
		{
			NoteWidget.show = true;
			Intent i = new Intent(context,NoteWidget.class);
			i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS));
			context.sendBroadcast(i);
		}
		else if(intent.getAction().equals(ACTION_CONFIG_CHANGED))
		{
			NoteWidget.show = false;
			NoteWidget.word = null;
			Intent i = new Intent(context,NoteWidget.class);
			i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS));
			context.sendBroadcast(i);
		}
	}
	public static void configChanged(Context context,int[] appWidgetIds)
	{
		Intent intent = new Intent(context, NoteWidget.class);
		intent.setAction(NoteWidget.ACTION_CONFIG_CHANGED);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
		context.sendBroadcast(intent);
	}
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
		reArrange(context,appWidgetManager,appWidgetId,remoteViews);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
    public void reArrange(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews remoteViews)
    {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        Resources r = context.getResources();
        int dpWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int dpHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int left_percent = 18;
        int text_percent = 15;
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) ((dpWidth * scale + 0.5f)*left_percent/100);
        remoteViews.setViewPadding(R.id.layout_widget, padding_in_px,0,0,0);
        //remoteViews.setViewPadding(R.id.buttons,0,100 - (pxSizeHeight*bottom_percent/100),0,0);
        //remoteViews.setViewPadding(R.id.buttons,0,0,0,0);
        if(Config.getResizeText(context)) {
            remoteViews.setTextViewTextSize(R.id.next, TypedValue.COMPLEX_UNIT_SP, dpHeight/2 * text_percent / 100);
            remoteViews.setTextViewTextSize(R.id.knowed, TypedValue.COMPLEX_UNIT_SP, dpHeight/2 * text_percent / 100);
            remoteViews.setTextViewTextSize(R.id.nknowed, TypedValue.COMPLEX_UNIT_SP, dpHeight/2 * text_percent / 100);
            remoteViews.setTextViewTextSize(R.id.word, TypedValue.COMPLEX_UNIT_SP, dpHeight/2 * text_percent / 100);
            remoteViews.setTextViewTextSize(R.id.definition, TypedValue.COMPLEX_UNIT_SP, dpHeight/2 * text_percent / 100);
        }


    }
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
		//prefs.edit().clear().commit();
		prefs.edit().remove("mode"+appWidgetIds[0]);
		prefs.edit().remove("note"+appWidgetIds[0]).commit();
	}
}