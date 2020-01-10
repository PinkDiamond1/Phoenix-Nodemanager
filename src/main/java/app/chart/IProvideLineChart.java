package app.chart;

import be.ceau.chart.LineChart;

import java.util.Optional;

public interface IProvideLineChart {

    Optional<LineChart> getChart(final String[] labels, final int[] points);

}
