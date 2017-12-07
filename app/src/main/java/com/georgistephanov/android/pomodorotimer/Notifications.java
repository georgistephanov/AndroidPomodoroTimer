package com.georgistephanov.android.pomodorotimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Georgi on 07-Dec-17.
 */

public class Notifications {
	// Main notification channel id
	private static final String MAIN_NOTIFICATION_CHANNEL = "PomodoroActivity";

	private Context context;

	private boolean vibrate = true;
	private boolean playSound = true;

	Notifications(Context context) {
		this.context = context;
	}

	/**
	 * Plays the melody and vibration pattern.
	 */
	void playTimerEndNotification() {
		if (playSound) {
			MediaPlayer mp = MediaPlayer.create(context, R.raw.end_sound);
			mp.start();
		}

		if (vibrate) {
			// Vibrate upon the end of the task/break
			Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			long[] vibrationPattern = {0, 300, 100, 800};
			vibrator.vibrate(vibrationPattern, -1);
		}
	}

	/**
	 * Creates and displays a notificator in the status bar with the default notificator
	 * title and text. Creates and bounds an intent to the notificator so that when it is
	 * clicked the app will be put on top.
	 */
	void showStatusBarNotification(boolean wasBreak) {
		// Builds the notificator
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MAIN_NOTIFICATION_CHANNEL);
		notificationBuilder
				.setAutoCancel(true)
				.setSmallIcon(R.mipmap.ic_launcher_round);

		Resources res = context.getResources();
		if ( wasBreak ) {
			notificationBuilder
					.setContentTitle(res.getString(R.string.notification_break_title))
					.setContentText(res.getString(R.string.notification_break_text));
		} else {
			notificationBuilder
					.setContentTitle(res.getString(R.string.notification_task_title))
					.setContentText(res.getString(R.string.notification_task_text));
		}

		// Creates an intent to this class
		Intent notificationIntent = new Intent(context, MainActivity.class);
		// Stacks the activity as the only activity open of the app even if it was open from another activity
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// Bounds the intent which to execute when the notificator is clicked to the notificator
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notificationBuilder.setContentIntent(intent);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notificationBuilder.build());
	}

	void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	void setPlaySound(boolean playSound) {
		this.playSound = playSound;
	}
}
