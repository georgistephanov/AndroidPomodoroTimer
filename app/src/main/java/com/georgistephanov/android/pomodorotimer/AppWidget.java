package com.georgistephanov.android.pomodorotimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Georgi on 11-Dec-17.
 */

public class AppWidget extends AppWidgetProvider {
	private static Context context;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Intent intent = new Intent(context, TimerService.class);
		Intent mainActivityIntent = new Intent(context, MainActivity.class);

		PendingIntent pendingIntentPlay = PendingIntent.getService(context, 0, intent.setAction("play"), 0);
		PendingIntent pendingIntentStop = PendingIntent.getService(context, 0, intent.setAction("stop"), 0);
		PendingIntent pendingIntentOpenMainActivity = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteViews.setOnClickPendingIntent(R.id.widget_start_button, pendingIntentPlay);
		remoteViews.setOnClickPendingIntent(R.id.widget_stop_button, pendingIntentStop);
		remoteViews.setOnClickPendingIntent(R.id.widget_launcher_icon, pendingIntentOpenMainActivity);

		appWidgetManager.updateAppWidget(new ComponentName(context, AppWidget.class), remoteViews);
	}
}
