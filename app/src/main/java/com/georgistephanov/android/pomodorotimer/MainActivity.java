package com.georgistephanov.android.pomodorotimer;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
	ProgressBar progressBarTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressBarTimer = findViewById(R.id.pb_timer);
		ObjectAnimator animation = ObjectAnimator.ofInt(progressBarTimer, "progress", 500, 0);
		animation.setDuration(10000);
		animation.setInterpolator(new DecelerateInterpolator());
		//animation.start();
	}
}
