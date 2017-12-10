package com.georgistephanov.android.pomodorotimer;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Georgi on 09-Dec-17.
 */

public class TimerService extends Service {
	private static final int SERVICE_ID = 623;
	private static final int INTERVAL = 100;

	private static Context mainActivityContext;
	private Timer timer = new Timer();

	private static int taskDuration;
	private static int timeLeft;
	private static String taskName;

	private static boolean isBreak = false;
	private static boolean isRunning = false;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
		isRunning = true;
		timeLeft = taskDuration;

		timer.scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						timeLeft -= INTERVAL;

						// Every minute update the notification
						if (timeLeft % 1000 % 60 == 0) {
							startForeground(SERVICE_ID, Notifications.buildTimerRunningNotification(mainActivityContext, timeLeft, isBreak));
						}

						// Send an update about the time left to the UI every second
						if (timeLeft % 1000 == 0) {
							sendUpdateToUI(timeLeft);
						}
					}
				}, 0, INTERVAL);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}

		if ( isBreak ) {
			isBreak = false;
		} else {
			writeTaskToDatabase();
		}

		isRunning = false;

		stopForeground(true);
		super.onDestroy();
	}

	/**
	 * Returns the remaining time left of the task in milliseconds
	 * @return the time left
	 */
	static int getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Returns the name of the current task
	 * @return taskName
	 */
	static String getTaskName() {
		return taskName;
	}

	/**
	 * Returns whether a task (or break) is currently running
	 * @return isRunning
	 */
	static boolean isRunning() {
		return isRunning;
	}

	/**
	 * Set the current task name
	 * @param taskName1 the task name
	 */
	static void setTaskName(String taskName1) {
		taskName = taskName1;
	}

	/**
	 * Set the task duration length in milliseconds
	 * @param duration in milliseconds
	 */
	static void setTaskDuration(int duration) {
		taskDuration = duration;
	}

	/**
	 * Sets the activity context to a local static variable to hold a reference to it.
	 * @param context the (main) activity context
	 */
	static void setContext(Context context) {
		mainActivityContext = context;
	}

	/**
	 * Set the variable which controls whether the current task running is a work session or break
	 * that it is a break.
	 */
	static void setIsBreak() {
		isBreak = true;
	}

	/**
	 * Writes the task session to the database for the current task
	 */
	private static void writeTaskToDatabase() {
		taskName = taskName.length() != 0
				? taskName
				: mainActivityContext.getResources().getString(R.string.task_default_name);
		int timeCompleted = taskDuration / 1000 - timeLeft / 1000;

		DatabaseHelper db = Database.getInstance(mainActivityContext);

		ContentValues cv = new ContentValues();
		cv.put(db.getTaskNameColumnName(), taskName);
		cv.put(db.getTaskLengthColumnName(), timeCompleted);
		cv.put(db.getTaskDateColumnName(), System.currentTimeMillis());
		Database.addTask(cv);

		db.close();
	}

	private static void sendUpdateToUI(int timeLeft) {
		Intent intent = new Intent("TimeLeft")
				.putExtra("timeLeft", timeLeft);
		LocalBroadcastManager.getInstance(mainActivityContext).sendBroadcast(intent);
	}

}
