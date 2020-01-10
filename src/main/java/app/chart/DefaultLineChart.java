package app.chart;

import be.ceau.chart.LineChart;
import be.ceau.chart.color.Color;
import be.ceau.chart.data.LineData;
import be.ceau.chart.dataset.LineDataset;
import be.ceau.chart.enums.BorderCapStyle;
import be.ceau.chart.enums.BorderJoinStyle;
import be.ceau.chart.options.elements.Fill;

import java.util.Optional;

public class DefaultLineChart implements IProvideLineChart {

    @Override
    public Optional<LineChart> getChart(String[] labels, int[] points) {

        if(labels.length != points.length) return Optional.empty();

        final LineDataset dataset = new LineDataset()
                .setLabel("LineData")
                .setFill(new Fill(true))
                .setLineTension(0.1f)
                .setBackgroundColor(new Color(75, 192, 192, 0.4))
                .setBorderColor(new Color(75,192,192,1))
                .setBorderCapStyle(BorderCapStyle.BUTT)
                .setBorderDashOffset(0.0f)
                .setBorderJoinStyle(BorderJoinStyle.MITER)
                .addPointBorderColor(new Color(75, 192, 192, 1))
                .addPointBackgroundColor(new Color(255, 255, 255, 1))
                .addPointBorderWidth(1)
                .addPointHoverRadius(5)
                .addPointHoverBackgroundColor(new Color(75,192,192,1))
                .addPointHoverBorderColor(new Color(220,220,220,1))
                .addPointHoverBorderWidth(2)
                .addPointRadius(1)
                .addPointHitRadius(10)
                .setSpanGaps(false)
                .setData(points);

        final LineData data = new LineData()
                .addDataset(dataset)
                .addLabels(labels);

        final LineChart chart = new LineChart()
                .setData(data);

        return chart.isDrawable() ? Optional.of(chart) : Optional.empty();

    }

}
