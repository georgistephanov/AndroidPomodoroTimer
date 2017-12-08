package com.georgistephanov.android.pomodorotimer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class StatisticsActivity extends Activity implements PopupMenu.OnMenuItemClickListener {

	// Activity views
	TextView tv_period;
	TextView tv_lastDaysTotal;
	TextView tv_lastDaysTotalLabel;
	TextView tv_lastDaysAverage;
	TextView tv_lastDaysAverageLabel;
	LinearLayout bulletListLayout;


	enum Period {
		WEEK, MONTH
	}

	Period defaultPeriod = Period.WEEK;

	int colourNumber = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		setTitle(getResources().getString(R.string.menu_statistics));

		// Get the views
		tv_period = findViewById(R.id.period);
		tv_lastDaysTotal = findViewById(R.id.last_days_total);
		tv_lastDaysTotalLabel = findViewById(R.id.last_days_total_label);
		tv_lastDaysAverage = findViewById(R.id.last_days_avg);
		tv_lastDaysAverageLabel = findViewById(R.id.last_days_avg_label);
		bulletListLayout = findViewById(R.id.bullet_list_layout);

		// Set the default period
		setPeriod(defaultPeriod);
	}

	public void onArrowDownClick(View view) {
		Context popupContext = new ContextThemeWrapper(this, R.style.PopupMenu);
		PopupMenu popupMenu = new PopupMenu(popupContext, view);

		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.inflate(R.menu.statistics_popup_menu);
		popupMenu.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_week:
				generateData(Period.WEEK);
				return true;
			case R.id.menu_month:
				generateData(Period.MONTH);
				return true;
			default:
				return false;
		}
	}

	public void setPeriod(Period period) {
		generateData(period);
	}

	/**
	 * Gets and displays the graph data for the previous amount of days.
	 * @param period to generate data for
	 */
	private void generateData(Period period) {
		// =========================================
		// Line Chart
		// =========================================
		makeLineGraph(period);

		// =========================================
		// Pie Chart
		// =========================================
		makePieChart(period);
	}

	private void makeLineGraph(Period period) {
		// Set the number of days that is going to be displayed info for
		int numOfDays;

		if (period == Period.WEEK) {
			tv_period.setText(getResources().getString(R.string.menu_week));
			numOfDays = 7;
		} else if (period == Period.MONTH) {
			tv_period.setText(getResources().getString(R.string.menu_month));
			numOfDays = 30;
		} else {
			throw new RuntimeException("No periods except week and month have been configured");
		}

		// An array in which the work duration of the particular day will be kept
		int[] daysWorkDuration = new int[numOfDays];
		int daysWorkDurationIndex = numOfDays - 1; // Index to use while populating the array

		// Get all tasks that had been done during the period
		{
			Cursor cursor = Database.getTasks(numOfDays);

			// Helper variables to extract the correct data for each day starting from the current day
			int day = getDayNumberFromTimestamp(System.currentTimeMillis());
			int dayWorkAmount = 0;

			while (cursor.moveToNext()) {
				long currentTaskTimestamp = cursor.getLong(2);
				int currentTaskDay = getDayNumberFromTimestamp(currentTaskTimestamp);
				int currentTaskDuration = cursor.getInt(1) / 60;

				if (day == currentTaskDay) {
					// Add the duration of this task to the current date's amount of work time
					dayWorkAmount += currentTaskDuration;
				} else {
					// Store the current day's progress and take the previous one
					daysWorkDuration[daysWorkDurationIndex--] = dayWorkAmount;

					day = currentTaskDay;
					dayWorkAmount = currentTaskDuration;
				}

				// If there is a record just for the last day -> store it to avoid leaks
				if (cursor.isLast()) {
					// Store the current day's progress as it is the last database record
					daysWorkDuration[daysWorkDurationIndex--] = dayWorkAmount;
				}
			}
		}

		// Set the X axis labels with the dates for the last days and the points of the graph
		List<AxisValue> axisValues = new ArrayList<>();
		List<PointValue> values = new ArrayList<>();
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.DAY_OF_MONTH, -(numOfDays - 1));

			for (int i = 0; i < numOfDays; i++) {
				// Add the value for the current day
				values.add(
						new PointValue(i, daysWorkDuration[i])
								.setLabel(formatTime(daysWorkDuration[i]))
				);

				// Add the X axis value for the current day
				axisValues.add(
						new AxisValue(i)
								.setLabel(getXAxisDateLabel(calendar.getTimeInMillis()))

				);

				// Increase the calendar to the next day
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		// Create the line object and set its properties
		Line line = new Line(values)
				.setColor(getResources().getColor(R.color.colorPrimary, this.getTheme()))
				.setHasLabelsOnlyForSelected(true);

		// Create the list of lines and add the line we're going to use
		List<Line> lines = new ArrayList<>();
		lines.add(line);

		// Create the line chart data object
		LineChartData lineData = new LineChartData(lines);
		lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
		lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

		// Get the chart view from the layout and set the line chart data and its properties
		LineChartView chartTop = findViewById(R.id.lineChart);
		chartTop.setLineChartData(lineData);
		chartTop.setValueSelectionEnabled(true); // Show points label when its clicked
		chartTop.setZoomType(ZoomType.HORIZONTAL);

		// Set the Y axis max value to the maximum time worked for a day of the days that are being displayed
		int yAxisMaxValue = -1;
		int totalWorkTime = 0; // Also get the total work time to display it under the graph
		for (int i = 0; i < numOfDays; i++) {
			if (yAxisMaxValue < daysWorkDuration[i]) {
				yAxisMaxValue = daysWorkDuration[i];
			}

			totalWorkTime += daysWorkDuration[i];
		}
		// Display total and average work time
		setTotalAndAverageWorkTimes(numOfDays, totalWorkTime);

		// Set the current and the maximum viewport
		{
			// If there is no work done prior set the top of the Y axis to an arbitrary value
			if (yAxisMaxValue == 0) {
				yAxisMaxValue = 60;
			} else {
				// Set the max Y axis value to a little bit more than the max value
				yAxisMaxValue += 10;
			}

			// Set the X axis max value depending on the period that is being displayed
			int xAxisMaxValue;

			if (period == Period.WEEK) {
				// Show all 7 days on the graph
				xAxisMaxValue = 6;
			} else if (period == Period.MONTH) {
				// Show maximum of 10 days at a time on the graph
				xAxisMaxValue = 9;
			} else {
				throw new RuntimeException("No periods except week and month have been configured");
			}

			chartTop.setCurrentViewport(
					new Viewport(numOfDays - xAxisMaxValue - 1, yAxisMaxValue, numOfDays, 0)
			);
			chartTop.setMaximumViewport(
					new Viewport(0, yAxisMaxValue, numOfDays - 1, 0)
			);
		}
	}

	/**
	 * Gets the information needed for populating the pie chart graph and sets its attributes
	 * and displays it.
	 */
	private void makePieChart(Period period) {
		// Set the number of days that is going to be displayed info for
		int numOfDays;

		if (period == Period.WEEK) {
			tv_period.setText(getResources().getString(R.string.menu_week));
			numOfDays = 7;
		} else if (period == Period.MONTH) {
			tv_period.setText(getResources().getString(R.string.menu_month));
			numOfDays = 30;
		} else {
			throw new RuntimeException("No periods except week and month have been configured");
		}

		for (int i = 0; i < 4; i++) {
			SliceValue sliceValues = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());

		}

		// Get the data from the database and populate the list of slice values
		List<SliceValue> sliceValues = new ArrayList<>();
		{
			Cursor cursor = Database.getTasks(numOfDays);
			Map<String, Integer> tasks = new HashMap<>();

			while (cursor.moveToNext()) {
				String taskName = cursor.getString(0);
				int taskDuration = cursor.getInt(1) / 60;

				if ( !tasks.containsKey(taskName) ) {
					tasks.put(taskName, taskDuration);
				} else {
					tasks.replace(taskName, tasks.get(taskName), tasks.get(taskName) + taskDuration);
				}
			}

			for (Map.Entry<String, Integer> e : tasks.entrySet()) {
				if (colourNumber < 8) {
					sliceValues.add(
							new SliceValue(e.getValue(), getNextColor())
									.setLabel(e.getKey())
					);
				} else {
					sliceValues.add(
							new SliceValue(e.getValue(), ChartUtils.pickColor())
									.setLabel(e.getKey())
					);
				}
			}

			if (tasks.isEmpty()) {
				makeDefaultPieChart();
				return;
			}
		}

		PieChartData pieChartData = new PieChartData(sliceValues);
		pieChartData.setHasLabels(false);
		pieChartData.setHasCenterCircle(true);
		pieChartData.setCenterText1FontSize((int) getResources().getDimension(R.dimen.pie_chart_textSize));

		PieChartView pieChartView = findViewById(R.id.pieChart);
		pieChartView.setPieChartData(pieChartData);
		pieChartView.setChartRotationEnabled(false);
		pieChartView.setOnValueTouchListener(new PieChartValueTouchListener(this));
		pieChartView.setValueSelectionEnabled(true);

		populateBulletList(sliceValues);
	}

	private void makeDefaultPieChart() {
		List<SliceValue> sliceValues = new ArrayList<>();

		sliceValues.add(
				new SliceValue(1, getResources().getColor(R.color.colorPrimary, this.getTheme()))
		);

		PieChartData pieChartData = new PieChartData(sliceValues);
		pieChartData.setCenterText1(getResources().getString(R.string.pie_chart_empty_label));
		pieChartData.setCenterText1FontSize((int) getResources().getDimension(R.dimen.pie_chart_textSize));

		PieChartView pieChartView = findViewById(R.id.pieChart);
		pieChartView.setPieChartData(pieChartData);

		// Hide the bullet list layout
		bulletListLayout.setVisibility(View.GONE);
	}

	private void populateBulletList(List<SliceValue> sliceValues) {
		for (SliceValue sv : sliceValues) {
			// Create the row layout
			LinearLayout row = new LinearLayout(this);
			{
				row.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
				));
				row.setOrientation(LinearLayout.HORIZONTAL);
			}

			// Create a new view for the bullet
			Button bullet = new Button(this);
			{
				int size = (int) (24 * getResources().getDisplayMetrics().density);
				int margin10 = (int) (10 * getResources().getDisplayMetrics().density);
				int margin5 = (int) (5 * getResources().getDisplayMetrics().density);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						size, size
				);
				params.setMargins(
						margin10,
						margin5,
						margin10,
						margin5
				);

				bullet.setLayoutParams(params);

				bullet.setBackground(getResources().getDrawable(R.drawable.bullet, this.getTheme()));
				bullet.getBackground().setColorFilter(sv.getColor(), PorterDuff.Mode.SRC_ATOP);
				bullet.setClickable(false);
			}

			// Create the text view
			TextView label = new TextView(this);
			{
				label.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
				));
				label.setTextSize(
					getResources().getDisplayMetrics().scaledDensity * 7
				);
				label.setText(String.valueOf(sv.getLabelAsChars()));
				label.setTextColor(getResources().getColor(R.color.gray, this.getTheme()));
			}

			// Add the bullet and the text to the row and add the row to the layout
			row.addView(bullet);
			row.addView(label);
			bulletListLayout.addView(row);
		}
	}

	/**
	 * Sets the correct values to the textviews which are located under the graph. They
	 * represent the total amount of timed worked for the period and the average time worked
	 * each day of that period.
	 * @param numOfDays the period
	 * @param totalWorkTime the total amount of time worked
	 */
	private void setTotalAndAverageWorkTimes(int numOfDays, int totalWorkTime) {
		Resources res = getResources();
		tv_lastDaysTotal.setText(res.getString(R.string.last_days_total, numOfDays));
		tv_lastDaysTotalLabel.setText(res.getString(R.string.plain_string, formatTime(totalWorkTime)));
		tv_lastDaysAverage.setText(res.getString(R.string.last_days_avg, numOfDays));
		tv_lastDaysAverageLabel.setText(res.getString(R.string.plain_string, formatTime(totalWorkTime / numOfDays)));
	}

	/**
	 * Finds and returns the day number of the month from a given timestamp
	 * @param timestamp to find the day of the month
	 * @return the day of the month
	 */
	private int getDayNumberFromTimestamp(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);

		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Formats a timestamp to a day and month, which are used for the X axis of the Line graph.
	 * @param timestamp to be formatted
	 * @return day and month in the format dd/mm
	 */
	private String getXAxisDateLabel(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);

		return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1);
	}

	/**
	 * Formats a time from minutes to the format 1h 20m
	 * @param minutes the minutes to format
	 * @return the formatted time
	 */
	private String formatTime(int minutes) {
		StringBuilder formattedTime = new StringBuilder();
		formattedTime.append(minutes / 60);
		formattedTime.append("h ");
		formattedTime.append(minutes % 60);
		formattedTime.append("m");

		return formattedTime.toString();
	}

	/**
	 * Returns the next colour id for the pie chart
	 * @return colour
	 */
	private int getNextColor() {
		return getResources().getIntArray(R.array.pieChart)[colourNumber++];
	}

	private class PieChartValueTouchListener implements PieChartOnValueSelectListener {
		PieChartView pieChartView;
		PieChartData pieChartData;

		PieChartValueTouchListener(Context context) {
			pieChartView = (PieChartView) ((Activity) context).findViewById(R.id.pieChart);
			pieChartData = pieChartView.getPieChartData();
		}

		@Override
		public void onValueSelected(int arcIndex, SliceValue value) {
			pieChartData.setCenterText1(formatTime((int) value.getValue()));
		}

		@Override
		public void onValueDeselected() {
			pieChartData.setCenterText1("");
		}
	}
}
