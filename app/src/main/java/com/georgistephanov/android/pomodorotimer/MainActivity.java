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
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

//TODO: Make it as a service running on the background as well
//TODO: Add notifications

public class MainActivity extends AppCompatActivity {
	// Task length in milliseconds
	private static final int TASK_LENGTH_MS = 3000;
	private int totalSecondsLeft;

	private Timer timer;
	private boolean isTimerRunning = false;

	ProgressBar pb_timer;
	TextView v_minutes;
	TextView v_seconds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timer = new Timer();
		totalSecondsLeft = TASK_LENGTH_MS / 1000;

		pb_timer = findViewById(R.id.pb_timer);
		v_minutes = findViewById(R.id.minutes);
		v_seconds = findViewById(R.id.seconds);

		updateTime();
	}

	public void onStartButtonPressed(View view) {
		if (!isTimerRunning) {
			ObjectAnimator animation = ObjectAnimator.ofInt(pb_timer, "progress", 500, 0);
			animation.setDuration(TASK_LENGTH_MS);
			animation.start();

			timer.schedule(new PomodoroTimerTask(), 1000, 1000);

			isTimerRunning = true;
		}
	}

	private void updateTimerClock() {
		if (totalSecondsLeft > 0) {
			updateTime();
		}
		else {
			onTimerEnd();
		}
	}

	private void updateTime() {
		int minutesLeft = totalSecondsLeft / 60;
		int secondsLeft = totalSecondsLeft % 60;

		v_minutes.setText(minutesLeft < 10 ? "0" + minutesLeft : String.valueOf(minutesLeft));
		v_seconds.setText(secondsLeft < 10 ? "0" + secondsLeft : String.valueOf(secondsLeft));
	}

	private class PomodoroTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(() -> {
				totalSecondsLeft--;
				updateTimerClock();
			});
		}
	}

	private void onTimerEnd() {
		isTimerRunning = false;
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] vibrationPattern = {0, 300, 100, 800};
		vibrator.vibrate(vibrationPattern, -1);

		timer.cancel();

		v_minutes.setText("00");
		v_seconds.setText("00");

		ObjectAnimator animation = ObjectAnimator.ofInt(pb_timer, "progress", 0, 500);
		animation.setDuration(500);
		animation.start();
	}
}
