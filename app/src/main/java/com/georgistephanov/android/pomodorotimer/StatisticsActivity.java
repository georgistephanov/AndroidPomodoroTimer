package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class StatisticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		setTitle(getResources().getString(R.string.menu_statistics));

		GraphView graph = (GraphView) findViewById(R.id.graph);
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
				new DataPoint(0, 1),
				new DataPoint(1, 5),
				new DataPoint(2, 3),
				new DataPoint(3, 2),
				new DataPoint(5, 11),
				new DataPoint(6, 35),
				new DataPoint(7, 34),
				new DataPoint(8, 55),
				new DataPoint(9, 20),
				new DataPoint(10, 18),
				new DataPoint(11, 12),
				new DataPoint(12, 16),
				new DataPoint(13, 41),
				new DataPoint(14, 55),
				new DataPoint(15, 23),
				new DataPoint(16, 32),
				new DataPoint(17, 61),
				new DataPoint(18, 35),
				new DataPoint(19, 34),
				new DataPoint(20, 55),
				new DataPoint(21, 20),
				new DataPoint(22, 18),
				new DataPoint(23, 12),
				new DataPoint(24, 16)
		});
		graph.addSeries(series);
		graph.setTitle("Statistics");

		// set manual X bounds
		graph.getViewport().setYAxisBoundsManual(true);
		graph.getViewport().setMinY(0);
		graph.getViewport().setMaxY(60);
		graph.setPadding(
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15,
				(int) getResources().getDisplayMetrics().density * 15
		);

		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMinX(0);
		graph.getViewport().setMaxX(7);

		graph.getViewport().setScalable(true);
	}
}
