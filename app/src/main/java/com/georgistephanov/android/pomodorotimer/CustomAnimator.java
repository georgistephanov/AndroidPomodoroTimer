package com.georgistephanov.android.pomodorotimer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Georgi on 12-Dec-17.
 */

public class CustomAnimator {
	static CustomAnimator instance;
	private ObjectAnimator pb_animation;
	ProgressBar pb_timer;

	private CustomAnimator(ProgressBar pb_timer) {
		this.pb_timer = pb_timer;
		pb_animation = ObjectAnimator.ofInt(pb_timer, "progress", 3600, 0);
	}

	static CustomAnimator getInstance(ProgressBar pb_timer) {
		if ( instance == null ) {
			instance = new CustomAnimator(pb_timer);
		}

		return instance;
	}

	void startAnimation(int duration, int startFrom) {
		if (pb_animation.isRunning()) {
			pb_animation.cancel();
		}

		pb_animation = ObjectAnimator.ofInt(pb_timer, "progress", startFrom, 0);
		pb_animation.setDuration(duration);
		pb_animation.setInterpolator(new LinearInterpolator());
		pb_animation.setAutoCancel(true);
		pb_animation.start();
	}

	void startAnimation(int duration) {
		pb_animation.setDuration(duration);
		pb_animation.setInterpolator(new LinearInterpolator());
		pb_animation.setAutoCancel(true);
		pb_animation.start();
	}

	void cancelAnimation() {
		if (pb_animation.isRunning()) {
			pb_animation.cancel();
		}

		// Restores the beginning state of the timer
		pb_animation = ObjectAnimator.ofInt(pb_timer, "progress", 3600, 0);
	}

	void pauseAnimation() {
		if ( pb_animation.isRunning() ) {
			pb_animation.pause();
		}
	}

	void resumeAnimation() {
		if ( pb_animation.isPaused() ) {
			pb_animation.resume();
		}
	}

	boolean isRunning() {
		return pb_animation.isRunning();
	}

	void setProgressBar(ProgressBar pb_timer) {
		this.pb_timer = pb_timer;
	}
}