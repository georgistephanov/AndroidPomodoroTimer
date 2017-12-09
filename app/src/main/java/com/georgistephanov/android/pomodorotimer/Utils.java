package com.georgistephanov.android.pomodorotimer;

/**
 * Created by Georgi on 09-Dec-17.
 */

import android.app.Activity;
import android.content.Intent;

/**
 * This class helps to set the correct theme on each
 * activity that is being created
 */
public class Utils {
	private static int currentTheme;
	public final static int THEME_DEFAULT = 0;
	public final static int THEME_DARK = 1;

	// Set the theme of the Activity and restart it
	public static void changeTheme(Activity activity, int theme) {
		currentTheme = theme;
		activity.recreate();
	}

	// Set the current theme of the activity
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (currentTheme) {
			case THEME_DEFAULT:
				activity.setTheme(R.style.AppTheme);
				break;
			case THEME_DARK:
				activity.setTheme(R.style.DarkTheme);
				break;
			default:
		}
	}
}
