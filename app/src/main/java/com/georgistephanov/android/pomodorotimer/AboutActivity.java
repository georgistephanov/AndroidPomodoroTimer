package com.georgistephanov.android.pomodorotimer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.CreativeCommonsAttribution30Unported;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

/**
 * Created by Georgi on 07-Dec-17.
 */

public class AboutActivity extends ListActivity {
	private static final String [] aboutNames = {
			"Version", "Credits", "Open Source Licenses"
	};

	private static final String [] aboutLabels = {
			"1.0", "", ""
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.onActivityCreateSetTheme(this);
		setContentView(R.layout.activity_about);
		setTitle(getResources().getString(R.string.menu_about));

		// Populate the list
		setListAdapter(new CustomAdapter(this));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (aboutNames[position].equals(aboutNames[1])) {

			CreditsFragment fragment = new CreditsFragment();
			fragment.setContext(this);
			Dialog dialog = fragment.onCreateDialog(null);
			dialog.show();

		} else if (aboutNames[position].equals(aboutNames[2])) {
			final Notices notices = new Notices();
			final License apacheLicense = new ApacheSoftwareLicense20();
			final License ccLicense = new CreativeCommonsAttribution30Unported();

			notices.addNotice(new Notice(
				"Notification sounds",
				"https://notificationsounds.com/",
				"Licensed under Creative Commons 4.0",
				ccLicense
			));

			notices.addNotice(new Notice(
					"LicensesDialog",
					"http://psdev.de",
					"Copyright 2013 Philip Schiffer <admin@psdev.de>",
					apacheLicense
			));

			notices.addNotice(new Notice(
				"HelloCharts",
				"https://github.com/lecho/hellocharts-android",
				"Copyright 2014 Leszek Wach",
				apacheLicense
			));


			new LicensesDialog.Builder(AboutActivity.this)
					.setNotices(notices)
					.setTitle("Open Source Licenses")
					.build()
					.show();
		}
	}

	private class CustomAdapter extends ArrayAdapter<String> {
		Context context;

		CustomAdapter(Context context) {
			super(AboutActivity.this, R.layout.about_list_adapter, R.id.aboutName, aboutNames);
			this.context = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			View row = super.getView(position, convertView, parent);

			// If there is an about label existing and it is not empty for the current row -> display it
			if (position < aboutLabels.length && aboutLabels[position].length() != 0) {

				// Remove the bottom padding of the name
				int padding = (int) context.getResources().getDisplayMetrics().density * 15;
				((TextView) row.findViewById(R.id.aboutName)).setPadding(
						padding,
						padding,
						padding,
						0
				);

				// Set the label text and its visibility
				TextView aboutLabel = row.findViewById(R.id.aboutLabel);
				aboutLabel.setText(context.getResources().getString(R.string.plain_string, "1.0"));
				aboutLabel.setVisibility(View.VISIBLE);
			}

			return row;
		}
	}
}
