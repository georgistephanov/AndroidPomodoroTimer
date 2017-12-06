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
import android.widget.TableLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	String [] taskLengthOptions = {
		"Task duration", "Break duration"
	};

	SeekBar sb_taskDuration;
	TextView tv_taskDuration;

	SeekBar sb_breakDuration;
	TextView tv_breakDuration;

	enum SETTINGS {
		TASK_DURATION, BREAK_DURATION
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		inflateSettings();
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

	private void inflateSettings() {
		Cursor cursor = Database.getSettings();
		Resources res = getResources();

		if (cursor.moveToNext()) {
			int minutes_taskDuration = cursor.getInt(0) / 1000 / 60;
			int minutes_breakDuration = cursor.getInt(1) / 1000 / 60;

			sb_taskDuration = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_task_duration), minutes_taskDuration, tv_taskDuration);
			sb_breakDuration = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_break_duration), minutes_breakDuration, tv_breakDuration);
		}

		sb_taskDuration.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_taskDuration));

		//sb_breakDuration.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_breakDuration));
	}

	private ProgressBar makeProgressBarSetting(String taskName, int length, TextView durationLabel) {
		Resources res = getResources();
		final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
		final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
		LinearLayout parent = findViewById(R.id.mainSettingsLayout);

		// Create the parent layout which is going to hold the widgets
		LinearLayout linearLayoutParent = new LinearLayout(this);
		linearLayoutParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutParent.setOrientation(LinearLayout.VERTICAL);

		// Create the TextView widget for the setting name and bound it to the parent
		TextView name = new TextView(this);
		name.setText(taskName);
		name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		name.setTextSize(pixelsToScaledPixels(res.getDimension(R.dimen.setting_name_textSize)));
		name.setTextColor(res.getColor(R.color.black, this.getTheme()));
		name.setPadding(
				pixelsToDensityPixels(5),
				pixelsToDensityPixels(15),
				0,
				pixelsToDensityPixels(5));

		// Create the inner layout which holds the seek bar and the text view for the minutes
		LinearLayout seekBarRow = new LinearLayout(this);
		seekBarRow.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
		seekBarRow.setOrientation(LinearLayout.HORIZONTAL);

		// Create the seek bar inside the seekBarRow layout
		SeekBar seekBar = new SeekBar(this);
		seekBar.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 6f));
		seekBar.setPadding(
				pixelsToDensityPixels(5),
				pixelsToDensityPixels(5),
				pixelsToDensityPixels(10),
				0
		);
		seekBar.setMax(60);
		seekBar.setProgress(length);

		// Create the text view which holds the minutes
		durationLabel = new TextView(this);
		durationLabel.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2f));
		durationLabel.setPadding(
				pixelsToDensityPixels(10),
				0,
				0,
				pixelsToDensityPixels(5)
		);
		durationLabel.setTextSize(pixelsToScaledPixels(res.getDimension(R.dimen.separator_textSize)));
		durationLabel.setText(length + " minutes");

		parent.addView(linearLayoutParent);
		linearLayoutParent.addView(name);
		linearLayoutParent.addView(seekBarRow);
		seekBarRow.addView(seekBar);
		seekBarRow.addView(durationLabel);

		return seekBar;
	}

	private int pixelsToDensityPixels(float pixels) {
		return (int) (getResources().getDisplayMetrics().density * pixels + .5f);
	}

	private int pixelsToScaledPixels(float pixels) {
		return (int) (pixels / getResources().getDisplayMetrics().scaledDensity);
	}

	private class DurationSeekBarListener implements SeekBar.OnSeekBarChangeListener {
		TextView boundLabel;

		DurationSeekBarListener(TextView boundLabel) {
			this.boundLabel = boundLabel;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			boundLabel.setText(seekBar.getProgress() + " minutes");
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}
}
