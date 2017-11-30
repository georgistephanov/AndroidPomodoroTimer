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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
	// Task length in milliseconds
	private static final int TASK_LENGTH_MS = 1500000;
	private int totalSecondsLeft;

	private Timer timer;
	private Handler handler;
	private boolean isTimerRunning = false;

	ProgressBar pb_timer;
	TextView v_minutes;
	TextView v_seconds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();
		timer = new Timer();
		totalSecondsLeft = TASK_LENGTH_MS / 1000;

		pb_timer = findViewById(R.id.pb_timer);
		v_minutes = findViewById(R.id.minutes);
		v_seconds = findViewById(R.id.seconds);
	}

	public void onStartButtonPressed(View view) {
		if (!isTimerRunning) {
			ObjectAnimator animation = ObjectAnimator.ofInt(pb_timer, "progress", 500, 0);
			animation.setDuration(TASK_LENGTH_MS);
			animation.setInterpolator(new DecelerateInterpolator());
			animation.start();

			timer.schedule(new PomodoroTimerTask(), 0, 1000);

			isTimerRunning = true;
		}
	}

	private void updateTimerClock() {
		int minutesLeft = totalSecondsLeft / 60;
		int secondsLeft = totalSecondsLeft % 60;

		v_minutes.setText(minutesLeft + "");
		v_seconds.setText(secondsLeft + "");
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
}
