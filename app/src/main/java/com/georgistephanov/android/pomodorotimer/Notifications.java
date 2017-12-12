package com.georgistephanov.android.pomodorotimer;

import android.app.Notification;
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
	 * Creates and displays a notification in the status bar with the default notification
	 * title and text. Creates and bounds an intent to the notification so that when it is
	 * clicked the app will be put on top.
	 */
	void showStatusBarNotification(boolean wasBreak) {
		// Builds the notification
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, MAIN_NOTIFICATION_CHANNEL);
		notificationBuilder
				.setAutoCancel(true)
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
		// Set intent action to notify the main activity that it was opened from the notification after a task
		// or a break had finished and therefore it should display the correct buttons for break / continue
		notificationIntent.setAction(context.getResources().getString(R.string.notification_intentAction_breakContinue));

		// Bounds the intent which to execute when the notification is clicked to the notification
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

	static Notification buildTimerRunningNotification(Context context, int timeLeft, boolean isBreak) {
		String contentTitle = (isBreak)
				? context.getResources().getString(R.string.notification_break_session_title)
				: context.getResources().getString(R.string.notification_work_session_title);
		int minutesLeft = (timeLeft / 60 / 1000) + 1;
		String contentText = minutesLeft > 1
				? minutesLeft + " minutes left"
				: minutesLeft + " minute left";

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL ID")
				.setAutoCancel(false)
				.setOngoing(true)
				.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setSmallIcon(R.mipmap.ic_launcher_round);

		// Creates an intent to this class
		Intent notificationIntent = new Intent(context, MainActivity.class);
		// Stacks the activity as the only activity open of the app even if it was open from another activity
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// Bounds the intent which to execute when the notification is clicked to the notification
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		builder.setContentIntent(pendingIntent);

		return builder.build();
	}
}
