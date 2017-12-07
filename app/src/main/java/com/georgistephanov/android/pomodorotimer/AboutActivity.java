package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Georgi on 07-Dec-17.
 */

public class AboutActivity extends ListActivity {
	private static final String [] items = {
			"Version", "Credits", "Open Source Licenses"
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		setListAdapter(new ArrayAdapter<String>(
				this,
				R.layout.about_list_adapter,
				items ));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}
}
