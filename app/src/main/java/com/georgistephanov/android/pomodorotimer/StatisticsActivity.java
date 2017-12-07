package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class StatisticsActivity extends Activity {

	List<AxisValue> axisValues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		setTitle(getResources().getString(R.string.menu_statistics));

//		LineChartView chart = findViewById(R.id.chart);
//		chart.setInteractive(true);
//		chart.setZoomEnabled(false);

		generateData(7);

//		//In most cased you can call data model methods in builder-pattern-like manner.
//		Line line = new Line(values)
//				.setColor(getResources()
//				.getColor(R.color.colorPrimaryDark, this.getTheme()))
//				.setCubic(false);
//
//		// Show the label when a point has been pressed
//		line.setHasLabelsOnlyForSelected(true);
//
//		List<Line> lines = new ArrayList<Line>();
//		lines.add(line);
//
//		LineChartData data = new LineChartData();
//		data.setLines(lines);
//
//		// Set both axes
//		Axis X = new Axis(axisValues).setHasLines(true);
//		Axis Y = new Axis().setHasLines(true);
//
//		data.setAxisXBottom(X);
//		data.setAxisYLeft(Y);
//
//		chart.setLineChartData(data);
//		chart.setViewportCalculationEnabled(false); // Build-up animation
//		chart.setZoomType(ZoomType.HORIZONTAL);
//		chart.setBackgroundColor(getResources().getColor(R.color.white, this.getTheme()));
//		chart.setValueSelectionEnabled(true);
	}

	private void generateData(int numOfDays) {

		List<AxisValue> axisValues = new ArrayList<>();
		List<PointValue> values = new ArrayList<>();

//		for (int i = 0; i < numOfDays; i++) {
//			values.add(new PointValue(i, new Random().nextInt(120)));
//			axisValues.add(new AxisValue(i).setLabel(getXAxisDateLabel(System.currentTimeMillis())));
//		}

		Cursor cursor = Database.getTasks(7);
		long lastDayTimestamp = System.currentTimeMillis();
		int lastDay = getDayNumberFromTimestamp(System.currentTimeMillis());
		int lastDayDuration = 0;
		int [] daysWorkDuration = new int[numOfDays];
		int daysWorkDurationIndex = numOfDays - 1;

		while (cursor.moveToNext()) {
			long currentDayTimestamp = cursor.getLong(2);
			int currentDay = getDayNumberFromTimestamp(currentDayTimestamp);
			int currentDayDuration = cursor.getInt(1) / 60;

			if (lastDay == currentDay) {
				// Add the duration of this task to the current date's amount of work time
				lastDayDuration += currentDayDuration;
			} else {
				// Store the current day's progress and take the previous one
				daysWorkDuration[daysWorkDurationIndex--] = lastDayDuration;

				lastDay = currentDay;
				lastDayTimestamp = currentDayTimestamp;
				lastDayDuration = currentDayDuration;
			}

			// If there is a record just for the last day -> store it to avoid leaks
			if ( cursor.isLast() ) {
				if ( lastDay == currentDay ) {
					// Store the current day's progress and take the previous one
					daysWorkDuration[daysWorkDurationIndex--] = lastDayDuration;

					lastDay = currentDay;
					lastDayTimestamp = currentDayTimestamp;
					lastDayDuration = currentDayDuration;
				}
			}
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.DAY_OF_MONTH, -(numOfDays - 1));

		for (int i = 0; i < numOfDays; i++) {
			values.add(new PointValue(i, daysWorkDuration[i]));
			axisValues.add(new AxisValue(i).setLabel(getXAxisDateLabel(calendar.getTimeInMillis())));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		Line line = new Line(values);
		line.setColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
		line.setHasLabelsOnlyForSelected(true);

		List<Line> lines = new ArrayList<>();
		lines.add(line);

		LineChartData lineData = new LineChartData(lines);
		lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
		lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

		LineChartView chartTop = findViewById(R.id.chart);
		chartTop.setLineChartData(lineData);

		chartTop.setViewportCalculationEnabled(false);
		Viewport v = new Viewport(0, 120, 6, 0);
		chartTop.setMaximumViewport(v);
		chartTop.setCurrentViewport(v);
		chartTop.setValueSelectionEnabled(true);


		chartTop.setZoomType(ZoomType.HORIZONTAL);

//		List<PointValue> pointValues = new ArrayList<>();
//		axisValues = new ArrayList<>();
//		int index = 0;
//
//		// Array to keep empty days
//		int [] nonEmptyDays = new int[numOfDays];
//		int nonEmptyDaysIndex = 0;
//
//		Cursor cursor = Database.getTasks(numOfDays);
//
//		long lastDayTimestamp = System.currentTimeMillis();
//		int lastDay = getDayNumberFromTimestamp(System.currentTimeMillis());
//		int lastDayDuration = 0;
//
//		while (cursor.moveToNext()) {
//			long currentDayTimestamp = cursor.getLong(2);
//			int currentDay = getDayNumberFromTimestamp(currentDayTimestamp);
//			int currentDayDuration = cursor.getInt(1);
//
//			if (lastDay == currentDay) {
//				// Add the duration of this task to the current date's amount of work time
//				lastDayDuration += currentDayDuration;
//			}
//			else {
//				// Store the current day's progress and take the previous one
//				pointValues.add(new PointValue(index++, lastDayDuration));
//				axisValues.add(new AxisValue(index).setLabel(getXAxisDateLabel(lastDayTimestamp)));
//				nonEmptyDays[nonEmptyDaysIndex++] = lastDayDuration;
//
//				lastDay = currentDay;
//				lastDayTimestamp = currentDayTimestamp;
//				lastDayDuration = currentDayDuration;
//			}
//		}
	}

	private int getDayNumberFromTimestamp(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);

		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	private String getXAxisDateLabel(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);

		return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1);
	}
}
