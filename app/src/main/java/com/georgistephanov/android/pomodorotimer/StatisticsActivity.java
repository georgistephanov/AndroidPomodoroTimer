package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		setTitle(getResources().getString(R.string.menu_statistics));

//		GraphView graph = (GraphView) findViewById(R.id.graph);
//		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//				new DataPoint(1, 5),
//				new DataPoint(2, 3),
//				new DataPoint(3, 2),
//				new DataPoint(5, 11),
//				new DataPoint(6, 35),
//				new DataPoint(7, 34),
//				new DataPoint(8, 55),
//				new DataPoint(9, 20),
//				new DataPoint(10, 18),
//				new DataPoint(11, 12),
//				new DataPoint(12, 16),
//				new DataPoint(13, 41),
//				new DataPoint(14, 55),
//				new DataPoint(15, 23),
//				new DataPoint(16, 32),
//				new DataPoint(17, 61),
//				new DataPoint(18, 35),
//				new DataPoint(19, 34),
//				new DataPoint(20, 55),
//				new DataPoint(21, 20),
//				new DataPoint(22, 18),
//				new DataPoint(23, 12),
//				new DataPoint(24, 16)
//		});
//		series.setColor(Color.rgb(230, 74, 25));
//		series.setDrawDataPoints(true);
//
//		graph.addSeries(series);
//		graph.setTitle(getResources().getString(R.string.stats, 30));
//
//		graph.getGridLabelRenderer().setHorizontalAxisTitle("Day");
//		graph.getGridLabelRenderer().setVerticalAxisTitle("Hours");
//
//		// set manual X bounds
//		graph.getViewport().setYAxisBoundsManual(true);
//		graph.getViewport().setMinY(0);
//		graph.getViewport().setMaxY(90);
//		graph.setPadding(
//				(int) getResources().getDisplayMetrics().density * 15,
//				(int) getResources().getDisplayMetrics().density * 15,
//				(int) getResources().getDisplayMetrics().density * 15,
//				(int) getResources().getDisplayMetrics().density * 15
//		);
//
//		graph.getViewport().setXAxisBoundsManual(true);
//		graph.getViewport().setMinX(1);
//		graph.getViewport().setMaxX(7);
//
//		graph.getViewport().scrollToEnd();
//
//		graph.getViewport().setScalable(true);


		// generate Dates
		Calendar calendar = Calendar.getInstance();
		Date d1 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d2 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d3 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d4 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d5 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d6 = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date d7 = calendar.getTime();


		GraphView graph = (GraphView) findViewById(R.id.graph);

// you can directly pass Date objects to DataPoint-Constructor
// this will convert the Date to double via Date#getTime()
		LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
				new DataPoint(d1, 1),
				new DataPoint(d2, 5),
				new DataPoint(d3, 3),
				new DataPoint(d4, 9),
				new DataPoint(d5, 2),
				new DataPoint(d6, 7),
				new DataPoint(d6, 3)
		});

		graph.addSeries(series);

// set date label formatter
		graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
		graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
		graph.getViewport().setMinX(d1.getTime());
		graph.getViewport().setMaxX(d3.getTime());
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setScrollable(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
		graph.getGridLabelRenderer().setHumanRounding(false);
	}

	private GraphView setGraphProperties(GraphView graphView,
			int firstDay, int lastDay, int numOfDays,
			int maxYValue) {
		GraphView graph = graphView;

		graph.setTitle(getResources().getString(R.string.stats, numOfDays));
		graph.getGridLabelRenderer().setHorizontalAxisTitle("Day");
		graph.getGridLabelRenderer().setVerticalAxisTitle("Hours");

		// set manual X bounds
		graph.getViewport().setYAxisBoundsManual(true);
		graph.getViewport().setMinY(0);
		graph.getViewport().setMaxY(maxYValue + 1);
		graph.setPadding(
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15
		);

		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMinX(0);
		graph.getViewport().setMaxX(7);

		graph.getViewport().scrollToEnd();

		graph.getViewport().setScalable(true);

		return graphView;
	}

	private LineGraphSeries<DataPoint> getData(int numOfDays) {
		DataPoint [] dataPoints = new DataPoint[numOfDays];

		for (int i = numOfDays - 1; i >= 0; i--) {
			// Get data from the database
		}

		return new LineGraphSeries<DataPoint>(dataPoints);
	}
}
