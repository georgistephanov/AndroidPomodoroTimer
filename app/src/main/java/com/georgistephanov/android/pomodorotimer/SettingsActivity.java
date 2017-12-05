package com.georgistephanov.android.pomodorotimer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	String [] taskLengthOptions = {
		"Task duration", "Break duration"
	};

	SeekBar sb_taskDuration;
	SeekBar sb_breakDuration;

	enum SETTINGS {
		TASK_DURATION, BREAK_DURATION
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void onDestroy() {
		DatabaseHelper db = Database.getInstance(this);

		// Set the changes made to the database
		ContentValues cv = new ContentValues();
		cv.put(db.getSettingsTaskLength(), sb_taskDuration.getProgress() * 60 * 1000);
		cv.put(db.getSettingsBreakLength(), sb_breakDuration.getProgress() * 60 * 1000);
		Database.insert(cv);

		super.onDestroy();
	}

	private ProgressBar makeProgressBarSetting(String taskName, int length) {
		Resources res = getResources();
		float scale = res.getDisplayMetrics().density;

		// Create the parent layout which is going to hold the widgets
		LinearLayout parent = new LinearLayout(this);
		parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		parent.setOrientation(LinearLayout.VERTICAL);

		// Create the TextView widget for the setting name
		TextView name = new TextView(this);
		name.setText(taskName);
		name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		name.setTextSize(res.getDimension(R.dimen.setting_name_textSize));
		name.setTextColor(res.getColor(R.color.black, this.getTheme()));
		name.setPadding(
				pixelsToDensityPixels(15),
				pixelsToDensityPixels(15),
				0,
				pixelsToDensityPixels(5));

		// TODO: Add linear layout with seekbar and textview for the minutes
	}

	private int pixelsToDensityPixels(float pixels) {
		return (int) (getResources().getDisplayMetrics().density * pixels + .5f);
	}
}
