package com.georgistephanov.android.pomodorotimer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.view.LineChartView;

public class StatisticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		setTitle(getResources().getString(R.string.menu_statistics));

		LineChartView chart = findViewById(R.id.chart);
		chart.setInteractive(true);
		chart.setZoomEnabled(false);

		List<PointValue> values = generateData(7);;

		//In most cased you can call data model methods in builder-pattern-like manner.
		Line line = new Line(values)
				.setColor(getResources()
				.getColor(R.color.colorPrimaryDark, this.getTheme()))
				.setCubic(false);

		// Show the label when a point has been pressed
		line.setHasLabelsOnlyForSelected(true);

		List<Line> lines = new ArrayList<Line>();
		lines.add(line);

		LineChartData data = new LineChartData();
		data.setLines(lines);

		// Set both axises
		Axis X = new Axis();
		Axis Y = new Axis().setHasLines(true);
		X.setName("Axis X");
		Y.setName("Axis Y");
		data.setAxisXBottom(X);
		data.setAxisYLeft(Y.setHasLines(true));

		chart.setLineChartData(data);
		chart.setBackgroundColor(getResources().getColor(R.color.white, this.getTheme()));
		chart.setValueSelectionEnabled(true);
	}

	private List<PointValue> generateData(int numOfDays) {
		List<PointValue> list = new ArrayList<>();

		list.add(new PointValue(0, 20));
		list.add(new PointValue(1, 70));
		list.add(new PointValue(2, 30));
		list.add(new PointValue(3, 40));
		list.add(new PointValue(4, 120));
		list.add(new PointValue(5, 40));
		list.add(new PointValue(6, 90));
		list.add(new PointValue(7, 40));

		return list;
	}
}
