package com.georgistephanov.android.pomodorotimer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Georgi on 09-Dec-17.
 */

public class TimerService extends IntentService {

	private int taskDuration;

	public TimerService() {
		super("TimerService");
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
		Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}
}
