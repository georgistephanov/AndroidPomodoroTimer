/**
 * This is my second android project. I will try to make a simple pomodoro timer
 * which I would be able to use personally. Ideally, by the end of the project this
 * app will be fully functional pomodoro timer and there will be a settings menu
 * from where I will be able to change some properties, such as the duration and the
 * theme of the app. I will keep track of the past tasks that had been finished and
 * hopefully will store them in a database so that they won't be lost. As usual, I have
 * set a deadline in front of myself - 13-Dec-2017. Hopefully, I will be able to finish
 * it in time again before I go home for the Christmas holidays.
 *
 * @Started 30-Nov-2017
 * @Author Georgi Stephanov
 */

package com.georgistephanov.android.pomodorotimer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener {
	// Database helper instance
	DatabaseHelper database;

	// The length of the task and the break in milliseconds
	private int taskLength;
	private int shortBreakLength;
	private int longBreakLength;
	private int longBreakAfter;
	private int numOfConsecutiveTasks = 0;

	// The state of the current timer in seconds
	private int totalSecondsLeft;

	// Main notification channel id
	private static final String MAIN_NOTIFICATION_CHANNEL = "PomodoroActivity";

	private Timer timer;
	private boolean isTimerRunning = false;
	private boolean isBreak = false;
	private boolean hasEnded = false;
	private boolean settingsUpdatePending = false;

	ObjectAnimator pb_animation;

	EditText et_taskName;
	ImageButton b_deleteEditText;

	ProgressBar pb_timer;
	TextView v_minutes;
	TextView v_seconds;

	ImageButton b_start;
	ImageButton b_pause;
	ImageButton b_stop;
	Button b_break;
	Button b_continue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().hide();

		et_taskName = findViewById(R.id.taskName);
		b_deleteEditText = findViewById(R.id.deleteTask);

		pb_timer = findViewById(R.id.pb_timer);
		v_minutes = findViewById(R.id.minutes);
		v_seconds = findViewById(R.id.seconds);

		b_start = findViewById(R.id.start_button);
		b_pause = findViewById(R.id.pause_button);
		b_stop = findViewById(R.id.stop_button);
		b_break = findViewById(R.id.takeBreak);
		b_continue = findViewById(R.id.continueTask);

		// Get the database helper instance
		database = Database.getInstance(this);

		// Get the task and break lengths from the database
		updateTimeFromSettings();
	}

	@Override
	protected void onDestroy() {
		database.close();
		super.onDestroy();
	}

	@Override
	protected void onResume()  {
		if ( !isTimerRunning ) {
			updateTimeFromSettings();
		} else {
			settingsUpdatePending = true;
		}
		super.onResume();
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		switch(menuItem.getItemId()) {
			case R.id.menu_statistics:
				return false;
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return false;
		}
	}


	/* =========== On click methods =========== */
	/**
	 * This method is called when the `X` of the edit text field for the task
	 * name has been clicked. It deletes the text of the edit text if such is
	 * entered.
	 * @param view the `x` button
	 */
	public void onDeleteTaskClick(View view) {
		if (et_taskName.getText().length() > 0) {
			et_taskName.setText("");

			if(et_taskName.hasFocus()) {
				et_taskName.clearFocus();
			}
		}
	}

	/**
	 * This method is called when the play button has been pressed.
	 * @param view the button that has been pressed
	 */
	public void onStartButtonClick(View view) {
		if (!isTimerRunning) {
			startTask();
			numOfConsecutiveTasks++;
		}
	}

	/**
	 * This method is called when the pause button has been clicked during a
	 * task. It toggles between paused and running state of the timer.
	 * @param view the pause button
	 */
	public void onPauseButtonClick(View view) {
		if ( isTimerRunning ) {
			pb_animation.pause();
			stopTimer();
		} else {
			pb_animation.resume();
			startTimer();
		}
	}

	/**
	 * This method is called when the stop button has been clicked during a
	 * task. It ends the current animation of the progress bar and calls the
	 * method which usually runs upon timer end.
	 * @param view the task end button
	 */
	public void onStopButtonClick(View view) {
		if ( !isBreak ) {
			// Restart the number of consecutive breaks
			numOfConsecutiveTasks = 0;
		}

		hasEnded = false;
		pb_animation.end();
		onTimerEnd(false);
	}

	/**
	 * This method is called when the `Take a break` button has been pressed. It
	 * sets the correct break colours for all widgets on the screen and starts a new
	 * task with the length of the break.
	 *
	 * @param view the take-a-break button
	 */
	public void onBreakButtonClick(View view) {
		isBreak = true;
		setBreakColors();

		// Start a break depending on whether it is supposed to be a short or a long break
		if ( longBreakAfter == numOfConsecutiveTasks ) {
			startTask(longBreakLength);
			numOfConsecutiveTasks = 0;
		} else {
			startTask(shortBreakLength);
		}
	}

	/**
	 * This method is being called when the `Continue` button has been pressed
	 * after the current task has finished. It calls the onStartButtonClick() method
	 * to start a new pomodoro timer.
	 * @param view the continue button
	 */
	public void onContinueButtonClick(View view) {
		onStartButtonClick(b_start);
	}

	/**
	 * Inflates and shows the popup menu.
	 * @param view whe popup menu image button
	 */
	public void showPopup(View view) {
		Context popupContext = new ContextThemeWrapper(this, R.style.PopupMenu);
		PopupMenu popupMenu = new PopupMenu(popupContext, view);

		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.inflate(R.menu.actions);
		popupMenu.show();
	}


	/* =========== Private methods =========== */
	/**
	 * Default start task method which overloads and calls the main startTask
	 * method with the default settings.
	 */
	private void startTask() {
		startTask(taskLength);
	}

	/**
	 * This method is responsible for starting the timer and setting the correct
	 * colours depending on whether it is a task or a break currently running. It
	 * then sets an appropriate animation for the progress bar.
	 *
	 * @param duration of the timer in milliseconds
	 */
	private void startTask(int duration) {
		if (!isTimerRunning) {
			// Set the correct amount of seconds left
			totalSecondsLeft = duration / 1000;
			updateTime();

			// Start the timer
			startTimer();
			showButtons();

			// Start the progress bar pb_animation
			pb_animation = ObjectAnimator.ofInt(pb_timer, "progress", 3600, 0);
			pb_animation.setDuration(duration);
			pb_animation.setInterpolator(new LinearInterpolator());
			pb_animation.start();

			// If the edit text is active make it inactive to avoid distraction of the blinking cursor
			if( et_taskName.hasFocus() ) {
				et_taskName.clearFocus();
			}
		}
	}

	/**
	 * Reads the settings from the database and updates the task and break lengths.
	 */
	private void updateTimeFromSettings() {
		// Read the settings from the database
		Cursor cursor = Database.getSettings();
		if (cursor.moveToNext()) {
			taskLength = cursor.getInt(0);
			shortBreakLength = cursor.getInt(1);
			longBreakLength = cursor.getInt(2);
			longBreakAfter = cursor.getInt(3);

			// If the task length is set to zero use the default task length
			if (taskLength == 0) {
				taskLength = Database.getInstance(this).getDefaultTaskLength();
			}

			// If the short break length is set to zero use the default short break length
			if (shortBreakLength == 0) {
				shortBreakLength = Database.getInstance(this).getDefaultShortBreakLength();
			}

			// If the long break length is set to zero use the default long break length
			if (longBreakLength == 0) {
				longBreakLength = Database.getInstance(this).getDefaultLongBreakLength();
			}

			// If the long break after x sessions is set to zero use the default long break after
			if (longBreakAfter == 0) {
				longBreakAfter = Database.getInstance(this).getDefaultLongBreakAfter();
			}

		} else {
			throw new RuntimeException("Database failed while getting the settings.");
		}

		totalSecondsLeft = taskLength / 1000;

		updateTime();
	}

	/**
	 * This method controls which method to call - updateTime() or onTimerEnd()
	 * depending on whether the timer has reached its end or it is still running.
	 */
	private void updateTimerClock() {
		if (totalSecondsLeft > 0) {
			updateTime();
		}
		else {
			hasEnded = true;
			onTimerEnd(true);
		}
	}

	/**
	 * This method runs every second. It is being called by the TimerTask activity
	 * and updates the minutes and seconds that are displayed in the middle of the
	 * progress bar.
	 */
	private void updateTime() {
		int minutesLeft = totalSecondsLeft / 60;
		int secondsLeft = totalSecondsLeft % 60;

		v_minutes.setText(minutesLeft < 10 ? "0" + minutesLeft : String.valueOf(minutesLeft));
		v_seconds.setText(secondsLeft < 10 ? "0" + secondsLeft : String.valueOf(secondsLeft));
	}

	/**
	 * This method runs when the timer has reached its end. It restores the
	 * activity to its initial position and allows the user to start a new
	 * pomodoro.
	 */
	private void onTimerEnd(boolean notification) {
		// Stop the timer
		stopTimer();
		showButtons();

		// Write the task session to the database
		if ( !isBreak ) {
			int taskSecondsCompleted = (taskLength / 1000) - totalSecondsLeft;
			String taskName = et_taskName.getText().toString().length() != 0
					? et_taskName.getText().toString()
					: getResources().getString(R.string.task_default_name);
			byte taskCompleted = totalSecondsLeft == 0
					? (byte) 1
					: (byte) 0;

			ContentValues contentValues = new ContentValues();
			contentValues.put(database.getTaskNameColumnName(), taskName);
			contentValues.put(database.getTaskLengthColumnName(), taskSecondsCompleted);
			contentValues.put(database.getTaskCompletedColumnName(), taskCompleted);

			Database.insert(contentValues);
		}

		// Do the settings update if such is pending
		if ( settingsUpdatePending ) {
			updateTimeFromSettings();
			settingsUpdatePending = false;
		}

		// Reset the clock to the initial length of the task
		totalSecondsLeft = taskLength / 1000;
		updateTime();

		// Animate the progressbar backwards to its beginning state
		ObjectAnimator animation = ObjectAnimator.ofInt(pb_timer, "progress", 0, 3600);
		animation.setDuration(500);
		animation.start();

		// If true -> the timer has ended uninterrupted
		if (notification) {
			playTimerEndNotification();
			showStatusBarNotification(isBreak);
		}

		// Restores the colours if it has been a break
		if ( isBreak ) {
			isBreak = false;
			restoreColors();
		}
	}

	/**
	 * Plays the melody and vibration pattern.
	 */
	private void playTimerEndNotification() {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.end_sound);
		mp.start();

		// Vibrate upon the end of the task/break
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] vibrationPattern = {0, 300, 100, 800};
		vibrator.vibrate(vibrationPattern, -1);
	}

	/**
	 * Creates and displays a notification in the status bar with the default notification
	 * title and text. Creates and bounds an intent to the notification so that when it is
	 * clicked the app will be put on top.
	 */
	private void showStatusBarNotification(boolean wasBreak) {
		// Builds the notification
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MAIN_NOTIFICATION_CHANNEL);
		notificationBuilder
				.setAutoCancel(true)
				.setSmallIcon(R.mipmap.ic_launcher_round);

		if ( wasBreak ) {
			notificationBuilder
					.setContentTitle(getResources().getString(R.string.notification_break_title))
					.setContentText(getResources().getString(R.string.notification_break_text));
		} else {
			notificationBuilder
				.setContentTitle(getResources().getString(R.string.notification_task_title))
				.setContentText(getResources().getString(R.string.notification_task_text));
		}

		// Creates an intent to this class
		Intent notificationIntent = new Intent(this, MainActivity.class);
		// Stacks the activity as the only activity open of the app even if it was open from another activity
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// Bounds the intent which to execute when the notification is clicked to the notification
		PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notificationBuilder.setContentIntent(intent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notificationBuilder.build());
	}

	/**
	 * Starts the timer with the PomodoroTimerTask.
	 */
	private void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new PomodoroTimerTask(), 1000, 1000);
			isTimerRunning = true;
		}
	}

	/**
	 * Stops the timer and gives the object a null value.
	 */
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			isTimerRunning = false;
		}
	}

	/**
	 * Sets the break colour to all the widgets that it should be applied on.
	 */
	private void setBreakColors() {
		int color = getResources().getColor(R.color.breakGreen, this.getTheme());
		setColors(color);
	}

	/**
	 * Restores the default colours of the widgets if they have been changed.
	 */
	private void restoreColors() {
		int color = getResources().getColor(R.color.colorPrimary, this.getTheme());
		setColors(color);
	}

	/**
	 * Sets the colours of the status bar, the delete button for the edit text widget,
	 * the progress bar and the two buttons (pause, stop) to a specific color.
	 *
	 * @param color in which to paint the widgets
	 */
	private void setColors(int color) {
		getWindow().setStatusBarColor(color);
		setShapeColor(b_deleteEditText, color);
		setShapeColor(pb_timer, color);
		setShapeColor(b_pause, color);
		setShapeColor(b_stop, color);

		// Change the tint colour of the delete button
		b_deleteEditText.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	}

	/**
	 * Changes the colour of a shape background
	 * @param view the view which colour to be changed
	 * @param color the colour to be set
	 */
	private void setShapeColor(View view, int color) {
		assert view != null;

		if (view instanceof ProgressBar) {
			((ProgressBar) view).getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		else if (view instanceof ImageButton) {
			((ImageButton) view).getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
	}

	/**
	 * Shows the start button and hides the pause and stop buttons if no task
	 * is currently running. Shows the pause and stop buttons and hides the start
	 * button if a task is currently running.
	 */
	private void showButtons() {
		if ( isTimerRunning ) {
			// Hide the start button
			b_start.setVisibility(View.INVISIBLE);

			// Hide the break and continue buttons if visible
			if ( b_break.getVisibility() != View.INVISIBLE ) {
				b_break.setVisibility(View.INVISIBLE);
				b_continue.setVisibility(View.INVISIBLE);
			}

			// Show the pause and break buttons
			b_pause.setVisibility(View.VISIBLE);
			b_stop.setVisibility(View.VISIBLE);
		}
		else {
			// Hide the pause and break buttons
			b_pause.setVisibility(View.INVISIBLE);
			b_stop.setVisibility(View.INVISIBLE);

			if ( hasEnded ) {
				// Show the `Break` and `Continue` buttons
				b_break.setVisibility(View.VISIBLE);
				b_continue.setVisibility(View.VISIBLE);
			}
			else {
				// Show the start button
				b_start.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * A TimerTask instance class that updates the timer clock every second.
	 * It is used by the Timer class instance.
	 */
	private class PomodoroTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(() -> {
				totalSecondsLeft--;
				updateTimerClock();
			});
		}
	}
}
