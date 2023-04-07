package tech.hiddenproject.compaj.gui.component;

import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Builder for {@link LineChart}.
 */
public class LineChartBuilder {

  private final LineChart lineChart;
  private final NumberAxis xAxis;
  private final NumberAxis yAxis;

  /**
   * Creates new builder.
   *
   * @param xAxisLabel Name of x-axis
   * @param yAxisLabel name of y-axis
   * @param createSymbols If labels should be created
   */
  public LineChartBuilder(String xAxisLabel, String yAxisLabel, boolean createSymbols) {
    this(xAxisLabel, yAxisLabel);
    lineChart.setCreateSymbols(createSymbols);
  }

  /**
   * Creates new builder.
   *
   * @param xAxisLabel Name of x-axis
   * @param yAxisLabel name of y-axis
   */
  public LineChartBuilder(String xAxisLabel, String yAxisLabel) {
    xAxis = new NumberAxis();
    yAxis = new NumberAxis();
    xAxis.setLabel(xAxisLabel);
    yAxis.setLabel(yAxisLabel);
    lineChart = new LineChart(xAxis, yAxis);
  }

  /**
   * Adds new line on chart.
   *
   * @param name Line name
   * @param xData Data for x-axis
   * @param yData Data for y-axis
   * @return {@link LineChartBuilder}
   */
  public LineChartBuilder with(
      String name, List<? extends Number> xData, List<? extends Number> yData) {
    if (xData.size() != yData.size()) {
      throw new RuntimeException("Size of X axis values must be equal to size of Y axis values!");
    }
    XYChart.Series dataSeries = new XYChart.Series();
    dataSeries.setName(name);
    for (int i = 0; i < xData.size(); ++i) {
      dataSeries
          .getData()
          .add(new XYChart.Data<>(xData.get(i).doubleValue(), yData.get(i).doubleValue()));
    }
    lineChart.getData().add(dataSeries);
    return this;
  }

  /**
   * Builds {@link LineChart}.
   *
   * @return {@link LineChart}
   */
  public LineChart build() {
    return lineChart;
  }

}
