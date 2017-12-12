package com.georgistephanov.android.pomodorotimer;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
	private static TimerService serviceInstance = null;

	private static final int SERVICE_ID = 623;
	private static final int INTERVAL = 100;

	private Context context;
	private Timer timer = new Timer();

	private static int taskDuration;
	private static int timeLeft;
	private static String taskName = "";

	private static boolean isBreak = false;
	private static boolean isRunning = false;
	private static boolean isPause = false;

	private static boolean hasFinishedInBackground = false;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

		// Only if no instance of this service is registered, register it
		if ( serviceInstance == null ) {
			// Hold a reference to the current service context
			context = this;

			// Check if it has been started from the app widget and get the task duration if so
			if (intent.getAction() != null && intent.getAction().equals("play")) {
				// Get the task duration from the database
				Database.getInstance(this.getBaseContext()); // Make instance of the DatabaseHelper in case it hadn't been initalised
				Cursor cursor = Database.getSettings();

				if (cursor.moveToNext()) {
					taskDuration = cursor.getInt(0);
				} else {
					throw new RuntimeException("Could not get the settings from the database");
				}
			} else if (intent.getAction() != null && intent.getAction().equals("stop")) {
				stopSelf();
			}

			isRunning = true;

			if ( !isPause ) {
				timeLeft = taskDuration;
			} else {
				// The task was paused, so resume it without resetting the time left
			}

			// Schedule the timer which will run every 100 milliseconds
			timer.scheduleAtFixedRate(
					new TimerTask() {
						@Override
						public void run() {
							// Subtract from the total time left the interval in which the timer runs
							timeLeft -= INTERVAL;

							// Update the notification every minute
							if (timeLeft % 1000 % 60 == 0) {
								startForeground(SERVICE_ID, Notifications.buildTimerRunningNotification(context, timeLeft, isBreak));
							}

							// Send an update about the time left to the UI every second
							if (timeLeft % 1000 == 0) {
								sendUpdateToUI(timeLeft);
							}
						}
					}, 0, INTERVAL);

			// Initialise the service instance at the end so that it is known that a service is running
			// and no other will be registered until this one finishes
			serviceInstance = this;
		} else {
			// If the stop button on the app widget has been pressed when a service is running - stop it
			if (intent.getAction() != null && intent.getAction().equals("stop")) {
				hasFinishedInBackground = true;
				stopSelf();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// Unregister the static service instance
		serviceInstance = null;

		// Stop the timer
		if (timer != null) {
			timer.cancel();
		}

		// Write the task to the database if it was not a break and it is not paused
		if ( isBreak ) {
			isBreak = false;
		} else if ( !isPause ) {
			writeTaskToDatabase();
		}

		// Update the isRunning flag to its default values
		isRunning = false;

		// Stop the foreground service and remove the notification
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
	 * Returns whether the task/break is paused
	 * @return isPaused
	 */
	static boolean isPaused() {
		return isPause;
	}

	/**
	 * Returns whether the service has ended while the app was not running
	 * @return
	 */
	static boolean hasFinishedInBackground() {
		return hasFinishedInBackground;
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
	 * Set the variable which controls whether the current task running is a work session or break
	 * that it is a break.
	 * @param br the value to set to the isBreak
	 */
	static void setIsBreak(boolean br) {
		isBreak = br;
	}

	/**
	 * Sets the isPaused variable which controls whether the last task ran was paused or stopped
	 * @param pause the value to set to the isPaused
	 */
	static void setIsPause(boolean pause) {
		isPause = pause;
	}

	/**
	 * Writes the task session to the database for the current task
	 */
	private void writeTaskToDatabase() {
		taskName = taskName.length() != 0
				? taskName
				: context.getResources().getString(R.string.task_default_name);
		int timeCompleted = taskDuration / 1000 - timeLeft / 1000;

		DatabaseHelper db = Database.getInstance(context);

		ContentValues cv = new ContentValues();
		cv.put(db.getTaskNameColumnName(), taskName);
		cv.put(db.getTaskLengthColumnName(), timeCompleted);
		cv.put(db.getTaskDateColumnName(), System.currentTimeMillis());
		Database.addTask(cv);

		db.close();

		// Restore the task name after it had been written do the database for the next session
		taskName = "";
	}

	private void sendUpdateToUI(int timeLeft) {
		Intent intent = new Intent("TimeLeft")
				.putExtra("timeLeft", timeLeft);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
