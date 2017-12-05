package com.georgistephanov.android.pomodorotimer;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends ListActivity {
	String [] taskLengthOptions = {
		"Task duration", "Break duration"
	};

	SeekBar sb_taskDuration;
	SeekBar sb_breakDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setListAdapter(new SettingsAdapter());
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

	private class SettingsAdapter extends ArrayAdapter<String> {
		SettingsAdapter() {
			super(SettingsActivity.this, R.layout.settings_duration, R.id.settingName, taskLengthOptions);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			// Keep the current row
			View row = super.getView(position, convertView, parent);

			// Set the setting name of the row
			((TextView) row.findViewById(R.id.settingName)).setText(taskLengthOptions[position]);

			// Find the duration of the current setting from the database
			Cursor cursor = Database.getSettings();
			int duration = -1;

			if (cursor.moveToNext()) {
				switch (position) {
					case 0:
						duration = cursor.getInt(0);
						sb_taskDuration = row.findViewById(R.id.seekBar);
						break;
					case 1:
						duration = cursor.getInt(1);
						sb_breakDuration = row.findViewById(R.id.seekBar);
						break;
					default:
						throw new RuntimeException("The size of the array must match the switch cases");
				}
			}

			if ( duration != -1 ) {
				// Convert the duration from milliseconds to minutes
				duration = duration / 1000 / 60;
			} else {
				throw new RuntimeException("The database didn't return the settings");
			}

			// Set the seekbar and the textview to display the correct duration
			LinearLayout seekBarRow = (LinearLayout) row.findViewById(R.id.seekBarRow);
			//((SeekBar) seekBarRow.findViewById(R.id.seekBar)).setProgress(duration);
			((TextView) seekBarRow.findViewById(R.id.settingDuration)).setText(duration + " minutes");

			return row;
		}
	}

}
