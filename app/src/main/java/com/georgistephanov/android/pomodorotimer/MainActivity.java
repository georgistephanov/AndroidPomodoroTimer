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
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

//TODO: Implement breaks
//TODO: Make it as a service running on the background as well
//TODO: Add notifications

public class MainActivity extends AppCompatActivity {
	// Task length in milliseconds
	private static final int TASK_LENGTH_MS = 15000;
	private int totalSecondsLeft;

	private Timer timer;
	private boolean isTimerRunning = false;

	ObjectAnimator pb_animation;

	EditText et_taskName;

	ProgressBar pb_timer;
	TextView v_minutes;
	TextView v_seconds;

	ImageButton b_start;
	ImageButton b_pause;
	ImageButton b_stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		totalSecondsLeft = TASK_LENGTH_MS / 1000;

		et_taskName = findViewById(R.id.taskName);

		pb_timer = findViewById(R.id.pb_timer);
		v_minutes = findViewById(R.id.minutes);
		v_seconds = findViewById(R.id.seconds);

		b_start = findViewById(R.id.start_button);
		b_pause = findViewById(R.id.pause_button);
		b_stop = findViewById(R.id.stop_button);

		updateTime();
	}

	/**
	 * This method is called when the play button has been pressed.
	 * @param view the play button
	 */
	public void onStartButtonClick(View view) {
		if (!isTimerRunning) {
			// Start the timer
			startTimer();
			showButtons();

			// Hide the start button
			b_start.setVisibility(View.INVISIBLE);

			// Show the pause and stop buttons
			b_pause.setVisibility(View.VISIBLE);
			b_stop.setVisibility(View.VISIBLE);

			// Start the progress bar pb_animation
			pb_animation = ObjectAnimator.ofInt(pb_timer, "progress", 500, 0);
			pb_animation.setDuration(TASK_LENGTH_MS);
			pb_animation.start();

			// If the edit text is active make it inactive to avoid distraction of the blinking cursor
			if( et_taskName.hasFocus() ) {
				et_taskName.clearFocus();
			}
		}
	}

	/**
	 * This method is called when the pause button has been clicked during a
	 * task. It toggles between paused and running state of the timer.
	 * @param view
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
		pb_animation.end();
		onTimerEnd(false);
	}

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
	 * This method controls which method to call - updateTime() or onTimerEnd()
	 * depending on whether the timer has reached its end or it is still running.
	 */
	private void updateTimerClock() {
		if (totalSecondsLeft > 0) {
			updateTime();
		}
		else {
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

		// Update the clock to the initial length of the task
		totalSecondsLeft = TASK_LENGTH_MS / 1000;
		updateTime();

		// Animate the progressbar backwards to the beginning
		ObjectAnimator animation = ObjectAnimator.ofInt(pb_timer, "progress", 0, 500);
		animation.setDuration(500);
		animation.start();

		if (notification) {
			playTimerEndNotification();
		}
	}

	private void playTimerEndNotification() {
		MediaPlayer mp = MediaPlayer.create(this, R.raw.end_sound);
		mp.start();

		// Vibrate upon the end of the task/break
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] vibrationPattern = {0, 300, 100, 800};
		vibrator.vibrate(vibrationPattern, -1);
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
	 * Shows the start button and hides the pause and stop buttons if no task
	 * is currently running. Shows the pause and stop buttons and hides the start
	 * button if a task is currently running.
	 */
	private void showButtons() {
		if ( isTimerRunning ) {
			// Hide the start button
			b_start.setVisibility(View.INVISIBLE);

			// Show the pause and break buttons
			b_pause.setVisibility(View.VISIBLE);
			b_stop.setVisibility(View.VISIBLE);
		}
		else {
			// Show the start button
			b_start.setVisibility(View.VISIBLE);

			// Hide the pause and break buttons
			b_pause.setVisibility(View.INVISIBLE);
			b_stop.setVisibility(View.INVISIBLE);
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
