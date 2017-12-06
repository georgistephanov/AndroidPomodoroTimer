package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	SeekBar sb_taskDuration;
	TextView tv_taskDuration;

	SeekBar sb_shortBreakDuration;
	TextView tv_shortBreakDuration;

	SeekBar sb_longBreakDuration;
	TextView tv_longBreakDuration;

	SeekBar sb_longBreakAfter;
	TextView tv_longBreakAfter;

	/**
	 * This is used to define the label name of the progress bar, as there are currently 4 settings
	 * with progress bar -> 3
	 */
	enum DurationUnit {
		MINUTES, SESSIONS
	};

	boolean settingsChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		inflateSettingViews();
	}

	@Override
	protected void onPause() {
		if ( settingsChanged ) {
			DatabaseHelper db = Database.getInstance(this);

			// Set the changes made to the database
			ContentValues cv = new ContentValues();
			cv.put(db.getSettingsTaskColumnName(), sb_taskDuration.getProgress() * 60 * 1000);
			cv.put(db.getSettingsShortBreakLengthColumnName(), sb_shortBreakDuration.getProgress() * 60 * 1000);
			cv.put(db.getSettingsLongBreakLengthColumnName(), sb_longBreakDuration.getProgress() * 60 * 1000);
			cv.put(db.getSettingsLongBreakAfterColumnName(), sb_longBreakAfter.getProgress());
			Database.updateSettings(cv);

			settingsChanged = false;
		}

		super.onPause();
	}

	/**
	 * Inflates the views on the activity
	 */
	private void inflateSettingViews() {
		Cursor cursor = Database.getSettings();
		Resources res = getResources();

		addSeparator(getResources().getString(R.string.settings_general_separator));

		if (cursor.moveToNext()) {
			// Create instances of the text views for the seek bars
			tv_taskDuration = new TextView(this);
			tv_shortBreakDuration = new TextView(this);
			tv_longBreakDuration = new TextView(this);
			tv_longBreakAfter = new TextView(this);

			// Get the correct length of the settings in minutes
			int minutes_taskDuration = cursor.getInt(0) / 1000 / 60;
			int minutes_shortBreakDuration = cursor.getInt(1) / 1000 / 60;
			int minutes_longBreakDuration = cursor.getInt(2) / 1000 / 60;
			int longBreakAfter = cursor.getInt(3);

			int taskMaxLength = 60;
			int shortBreakMaxLength = 30;
			int longBreakMaxLength = 60;
			int longBreakAfterMaxLength = 10;

			// Inflate the setting rows
			sb_taskDuration = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_task_duration),
					minutes_taskDuration,
					taskMaxLength,
					tv_taskDuration,
					DurationUnit.MINUTES);
			sb_shortBreakDuration = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_short_break_duration),
					minutes_shortBreakDuration,
					shortBreakMaxLength,
					tv_shortBreakDuration,
					DurationUnit.MINUTES);
			sb_longBreakDuration = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_long_break_duration),
					minutes_longBreakDuration,
					longBreakMaxLength,
					tv_longBreakDuration,
					DurationUnit.MINUTES);
			sb_longBreakAfter = (SeekBar) makeProgressBarSetting(res.getString(R.string.settings_long_break_after),
					longBreakAfter,
					longBreakAfterMaxLength,
					tv_longBreakAfter,
					DurationUnit.SESSIONS);


			// Set the change listeners to the seek bars
			sb_taskDuration.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_taskDuration, DurationUnit.MINUTES));
			sb_shortBreakDuration.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_shortBreakDuration, DurationUnit.MINUTES));
			sb_longBreakDuration.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_longBreakDuration, DurationUnit.MINUTES));
			sb_longBreakAfter.setOnSeekBarChangeListener(new DurationSeekBarListener(tv_longBreakAfter, DurationUnit.SESSIONS));
		}
		else {
			throw new RuntimeException("Database failed while getting the settings.");
		}
	}

	/**
	 * Creates a separator view to separate a group of settings
	 * @param separatorText the text to set to the separator
	 */
	private void addSeparator(String separatorText) {
		TextView separator = new TextView(this);
		separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		separator.setPadding(
				0,
				pixelsToDensityPixels(15),
				0,
				0
		);
		separator.setText(separatorText);
		separator.setTextSize(pixelsToScaledPixels(getResources().getDimension(R.dimen.separator_textSize)));
		separator.setTextColor(getResources().getColor(R.color.darkRed, this.getTheme()));
		separator.setTypeface(Typeface.create("sans-seeerif-medium", Typeface.BOLD));

		// Add the view to the main layout
		((LinearLayout) findViewById(R.id.mainSettingsLayout)).addView(separator);
	}

	/**
	 * Creates a setting layout and group of views to represent a progress bar setting. It consists
	 * of LinearLayout which holds the whole group of views, a setting name and a setting seekbar
	 * with a label, which is used to change the setting value.
	 * @param settingName the name of the setting
	 * @param length the current length which is set to the seekbar and the label
	 * @param durationLabel a textview which to be used as the seekbar label
	 * @return the seekbar which was created
	 */
	private ProgressBar makeProgressBarSetting(String settingName,
											   int length,
											   int maxLength,
											   TextView durationLabel,
											   DurationUnit durationUnit) {
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
		name.setText(settingName);
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
		seekBar.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 75f));
		seekBar.setPadding(
				pixelsToDensityPixels(5),
				pixelsToDensityPixels(5),
				pixelsToDensityPixels(10),
				0
		);
		seekBar.setMax(maxLength);
		seekBar.setProgress(length);

		// Create the text view which holds the minutes/sessions
		durationLabel.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 25f));
		durationLabel.setPadding(
				pixelsToDensityPixels(10),
				0,
				0,
				pixelsToDensityPixels(5)
		);
		durationLabel.setTextSize(pixelsToScaledPixels(res.getDimension(R.dimen.separator_textSize)));

		if (durationUnit == DurationUnit.MINUTES) {
			durationLabel.setText(res.getString(R.string.settings_duration_label_minutes, length));
		} else if (durationUnit == DurationUnit.SESSIONS) {
			durationLabel.setText(res.getString(R.string.settings_duration_label_sessions, length));
		}

		parent.addView(linearLayoutParent);
		linearLayoutParent.addView(name);
		linearLayoutParent.addView(seekBarRow);
		seekBarRow.addView(seekBar);
		seekBarRow.addView(durationLabel);

		return seekBar;
	}

	/**
	 * Converts pixels to density pixels (dp)
	 * @param pixels the amount of pixels to be converted
	 * @return the amount of pixels converted to density pixels
	 */
	private int pixelsToDensityPixels(float pixels) {
		return (int) (getResources().getDisplayMetrics().density * pixels + .5f);
	}

	/**
	 * Converts pixels to scaled pixels (sp)
	 * @param pixels the amount of pixels to be converted
	 * @return the amount of pixels converted to scaled pixels
	 */
	private int pixelsToScaledPixels(float pixels) {
		return (int) (pixels / getResources().getDisplayMetrics().scaledDensity);
	}

	/**
	 * A class which implements the SeekBar.OnSeekBarChangeListener interface. This class is used
	 * as a change listener for the seekbar settings.
	 */
	private class DurationSeekBarListener implements SeekBar.OnSeekBarChangeListener {
		TextView boundLabel;
		DurationUnit durationUnit;

		/**
		 * When constructed, the object keeps a reference of the label of the seekbar it is bound to
		 * @param boundLabel the label of the seekbar
		 */
		DurationSeekBarListener(TextView boundLabel, DurationUnit durationUnit) {
			this.boundLabel = boundLabel;
			this.durationUnit = durationUnit;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			// Change the seekbar's label to show the current progress
			if (durationUnit == DurationUnit.MINUTES) {
				boundLabel.setText(getResources().getString(R.string.settings_duration_label_minutes, seekBar.getProgress()));
			} else if (durationUnit == DurationUnit.SESSIONS) {
				boundLabel.setText(getResources().getString(R.string.settings_duration_label_sessions, seekBar.getProgress()));
			}

			settingsChanged = true;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}
}
