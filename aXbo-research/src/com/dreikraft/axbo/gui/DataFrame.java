package com.dreikraft.axbo.gui;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.timeseries.KeyTimeSeries;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.events.DiagramCopy;
import com.dreikraft.axbo.events.DiagramPrint;
import com.dreikraft.axbo.events.DiagramSaveAsPNG;
import com.dreikraft.axbo.events.DiagramClose;
import com.dreikraft.axbo.model.ChartType;
import com.dreikraft.axbo.timeseries.TimeSeriesUtil;
import com.dreikraft.axbo.util.BundleUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.border.TitledBorder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.Align;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

/**
 * DataFrame
 *
 * @author jan.illetschko@3kraft.com
 */
public class DataFrame extends JPanel implements Printable {

  public static final Log log = LogFactory.getLog(DataFrame.class);
  public static final Color CHART_BG_COLOR = new Color(0, 0, 0, 0);
  public static final Color AXIS_COLOR = new Color(0x7D, 0x7D, 0x7D, 0xFF);
  public static final Color GRID_COLOR = new Color(0x7D, 0x7D, 0x7D, 0xFF);
  public static final Color BAR_COLOR = new Color(255, 212, 107, 255);
  public static final Color BAR_COLOR2 = new Color(155, 112, 7, 255);
  public static final GradientPaint BAR_PAINT = new GradientPaint(0f, 0f,
      BAR_COLOR, 0f, 0f, BAR_COLOR2);
  public static final Color SLEEP_MARKER_PAINT = new Color(0xF0, 0x00, 0XFF,
      0xFF);
  public static final Color WAKE_PAINT = new Color(0x10, 0xCE, 0x15, 0xFF);
  public static final Color SNOOZE_PAINT = new Color(0x00, 0xFF, 0xFF, 0xFF);
  public static final Color WAKE_INTERVALL_PAINT = new Color(0x9D, 0x84, 0x47,
      0x40);
  public static final Color WAKE_INTERVALL_END_PAINT = new Color(0x9D, 0x84,
      0x47, 0xFF);
  public static final Color SLEEP_DURATION_PAINT = new Color(0x9E, 0x9E, 0x9E,
      0xFF);
  public static final Color KEY_PAINT = new Color(0x7D, 0x9B, 0xFF, 0xFF);
  public static final Stroke MARKER_STROKE = new BasicStroke(1.5f);
  public static final int INSET = 20;
  public static final int PRINT_FONT_SIZE = 8;
  private static final int DISTRIBUTION_MAX_LIMIT = 4;
  private static final int DISTRIBUTION_STEPS = 10;
  private ChartPanel chartPanel;
  private SleepData sleepData;

  public void init() {
    initComponents();
  }

  /**
   * Creates a new data chart from the sleep data. The chart is a combination of
   * a bar chart and a moving average distribution plot.
   *
   * @param sleepData the movement data of one sleep
   */
  public void createChartPanel(final SleepData sleepData) {

    this.sleepData = sleepData;

    // set border title
    ((TitledBorder) getBorder()).setTitle(getTitle());

    // create the chart
    final JFreeChart chart = createChart(ChartType.valueOf(Axbo
        .getApplicationPreferences().get(Axbo.CHART_TYPE_PREF, ChartType.BAR
            .name())));

    // create a new chart panel
    if (chartPanel != null) {
      pnlChart.removeAll();
    }
    chartPanel = new ChartPanel(chart, true);
    chartPanel.setBorder(null);
    chartPanel.setRangeZoomable(false);
    chartPanel.setPopupMenu(chartPanelPopupMenu);
    chartPanel.setMaximumDrawHeight(2000);
    chartPanel.setMaximumDrawWidth(2000);
    chartPanel.setMinimumDrawWidth(10);
    chartPanel.setMinimumDrawHeight(10);
    pnlChart.add(chartPanel);
  }

  /**
   * Creates a new chart from sleep data.
   *
   * @param chartType the requested chart type
   * @return a new chart instance
   */
  private JFreeChart createChart(final ChartType chartType) {

    // set domain axis
    final DateAxis dateAxis = new DateAxis();
    dateAxis.setTickLabelPaint(AXIS_COLOR);

    final Date startTime = sleepData.calculateSleepStart();
    final Date wakeIntervalStart = sleepData.getWakeIntervalStart();
    final Date wakeIntervalEnd = sleepData.calculateWakeIntervalEnd();
    final Date wakeupTime = sleepData.getWakeupTime();

    final KeyTimeSeries keyTimeSeries
        = TimeSeriesUtil.createKeyDataset(sleepData,
            BundleUtil.getMessage("chart.keyseries.label"));
    final KeyTimeSeries snoozeTimeSeries
        = TimeSeriesUtil.createSnoozeDataset(sleepData,
            BundleUtil.getMessage("chart.snoozeseries.label"));
    final IntervalXYDataset dataset
        = TimeSeriesUtil.createDataset(sleepData,
            BundleUtil.getMessage("chart.timeseries.label"));

    // create a data plot
    XYPlot plot = null;
    if (chartType.equals(ChartType.BAR)) {
      // create movement plot
      plot = createMovementsPlot(dataset, dateAxis);
      // add event markers
      addMarkers(plot, startTime, wakeIntervalStart, wakeIntervalEnd,
          keyTimeSeries, snoozeTimeSeries, wakeupTime);
    } else if (chartType.equals(ChartType.MOVING_AVG)) {
      // create moving average plot
      plot = createMovementDistributionPlot(TimeSeriesUtil.createMovingAverage(
          ((TimeSeriesCollection) dataset).getSeries(0), 1, 1), dateAxis,
          DISTRIBUTION_STEPS);
      // add event markers
      addMarkers(plot, startTime, wakeIntervalStart, wakeIntervalEnd,
          keyTimeSeries, snoozeTimeSeries, wakeupTime);
    } else {
      // create movement plot
      XYPlot barPlot = createMovementsPlot(dataset, dateAxis);
      addMarkers(barPlot, startTime, wakeIntervalStart, wakeIntervalEnd,
          keyTimeSeries, snoozeTimeSeries, wakeupTime);
      // create moving average plot
      XYPlot mvgAvgPlot = createMovementDistributionPlot(
          TimeSeriesUtil.createMovingAverage(
              ((TimeSeriesCollection) dataset).getSeries(0), 1, 1), dateAxis,
          DISTRIBUTION_STEPS);
      addMarkers(mvgAvgPlot, startTime, wakeIntervalStart, wakeIntervalEnd,
          keyTimeSeries, snoozeTimeSeries, wakeupTime);
      // create combined plot
      plot = createCombinedPlot(barPlot, mvgAvgPlot, dateAxis);
    }

    // create the chart
    final JFreeChart chart = new JFreeChart(plot);
    StandardChartTheme.createLegacyTheme().apply(chart);
    // customize the chart
    chart.setBackgroundPaint(CHART_BG_COLOR);
    chart.setBorderVisible(false);
    chart.setAntiAlias(false);
    chart.setTextAntiAlias(true);
    chart.removeLegend();

    return chart;
  }

  /**
   * Creates a plot with both chart types combined into one chart.
   *
   * @param movementPlot a movement plot
   * @param movingAvgPlot a moving average plot of the same sleep date
   * @param dateAxis the date axis for both diagrams
   * @return a combined plot
   */
  public CombinedDomainXYPlot createCombinedPlot(XYPlot movementPlot,
      XYPlot movingAvgPlot, final DateAxis dateAxis) {
    final CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(dateAxis);
    combinedPlot.setGap(1);
    combinedPlot.add(movementPlot, 12);
    combinedPlot.add(movingAvgPlot, 1);
    return combinedPlot;
  }

  /**
   * Creates the moving average plot from the movement data.
   *
   * @param timeSeries a moving average timeseries
   * @param dateAxis the date axis (x)
   * @param steps the number of shades of the chart
   * @return a moving average plot
   */
  private XYPlot createMovementDistributionPlot(final TimeSeries timeSeries,
      final DateAxis dateAxis, int steps) {

    final NumberAxis numAxis = new NumberAxis();
    numAxis.setVisible(false);
    numAxis.setFixedAutoRange(1);

    final XYBlockRenderer renderer = new XYBlockRenderer();
    renderer.setBlockWidth(1000 * 60);
    renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);
    renderer.setSeriesOutlinePaint(0, null);

    final double max = timeSeries.getMaxY() / DISTRIBUTION_MAX_LIMIT;
    int diffR = getBackground().getRed() - BAR_COLOR.getRed();
    int diffG = getBackground().getGreen() - BAR_COLOR.getGreen();
    int diffB = getBackground().getBlue() - BAR_COLOR.getBlue();
    final LookupPaintScale paintScale = new LookupPaintScale(0,
        timeSeries.getMaxY(), new Color(
          getBackground().getRed() - (diffR / (steps * 3)),
          getBackground().getGreen() - (diffG / (steps * 3)),
          getBackground().getBlue() - (diffB / (steps * 3))));
    for (int i = 2; i <= steps; i++) {
      paintScale.add(max / steps * i, new Color(
          getBackground().getRed() - (diffR * i / steps),
          getBackground().getGreen() - (diffG * i / steps),
          getBackground().getBlue() - (diffB * i / steps)));
    }

    renderer.setPaintScale(paintScale);

    final XYPlot plot = new XYPlot(
        TimeSeriesUtil.createXYZTimeSeries(timeSeries), dateAxis, numAxis,
        renderer);
    plot.setBackgroundPaint(CHART_BG_COLOR);
    plot.setRangeGridlinesVisible(false);
    plot.setDomainGridlinesVisible(false);
    plot.setOutlineVisible(false);

    return plot;
  }

  /**
   * Creates a bar chart from the movement data.
   *
   * @param dataset the movement data set
   * @param dateAxis the date axis
   * @return a bar chart
   */
  private XYPlot createMovementsPlot(final IntervalXYDataset dataset,
      final DateAxis dateAxis) {

    // connfigure value axis
    final NumberAxis valueAxis = new NumberAxis();
    valueAxis.setStandardTickUnits(NumberAxis.
        createIntegerTickUnits());
    valueAxis.setAxisLineVisible(false);
    valueAxis.setTickLabelsVisible(true);
    valueAxis.setTickMarksVisible(false);
    valueAxis.setTickLabelPaint(AXIS_COLOR);

    // movements renderer
    final XYBarRenderer renderer = new XYBarRenderer();
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setShadowVisible(false);
    renderer.setDrawBarOutline(false);
    renderer.setSeriesPaint(0, BAR_PAINT);

    // tool tips
    final XYToolTipGenerator tooltipGenerator = StandardXYToolTipGenerator.
        getTimeSeriesInstance();
    renderer.setBaseToolTipGenerator(tooltipGenerator);

    // customize the plot
    final XYPlot plot = new XYPlot(dataset, dateAxis,
        valueAxis, renderer);
    plot.setOrientation(PlotOrientation.VERTICAL);
    plot.setInsets(new RectangleInsets(5, 5, 5, 5));
    plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
    plot.setBackgroundPaint(CHART_BG_COLOR);
    plot.setBackgroundImageAlpha(0.05f);
    plot.setBackgroundImageAlignment(Align.CENTER);
    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlineStroke(new BasicStroke());
    plot.setRangeGridlinePaint(GRID_COLOR);
    plot.setDomainGridlinesVisible(false);
    plot.setOutlineVisible(false);
    plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);

    return plot;
  }

  /**
   * Creates markers for various sleep events in xy plot.
   *
   * @param plot a XYPlot
   * @param sleepStart the calculated start time of the sleep displayed in the
   * chart or null
   * @param wakeIntervalStart the received start time of the wake interval or
   * null
   * @param wakeIntervalEnd the calculated wake interval or null
   * @param keys any key presses during the sleep interval as time series
   * @param snoozes snooze key presses during the wakeup interval
   * @param wakeupTime the calculated wakeup time or null
   */
  private void addMarkers(final XYPlot plot, final Date sleepStart,
      final Date wakeIntervalStart, final Date wakeIntervalEnd,
      final KeyTimeSeries keys, final KeyTimeSeries snoozes,
      final Date wakeupTime) {
    // draw sleep start
    final Marker sleepStartMarker = new ValueMarker(sleepStart.getTime());
    sleepStartMarker.setPaint(SLEEP_MARKER_PAINT);
    sleepStartMarker.setStroke(MARKER_STROKE);
    sleepStartMarker.setOutlinePaint(null);
    plot.addDomainMarker(sleepStartMarker, Layer.FOREGROUND);

    // wake intervall marker
    if (wakeIntervalStart != null) {
      final Marker wakeInterval = new IntervalMarker(
          wakeIntervalStart.getTime(), wakeIntervalEnd.getTime());
      wakeInterval.setPaint(WAKE_INTERVALL_PAINT);
      wakeInterval.setOutlinePaint(null);
      plot.addDomainMarker(wakeInterval, Layer.BACKGROUND);
    }
    // sensor keys
    for (final Object series : keys.getItems()) {
      final TimeSeriesDataItem timeSeriesItem = (TimeSeriesDataItem) series;
      final Marker keyMarker = new ValueMarker(timeSeriesItem.getPeriod()
          .getMiddleMillisecond());
      keyMarker.setPaint(KEY_PAINT);
      keyMarker.setOutlinePaint(null);
      keyMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(keyMarker, Layer.FOREGROUND);
    }
    // snooze keys
    for (final Object series : snoozes.getItems()) {
      final TimeSeriesDataItem timeSeriesItem = (TimeSeriesDataItem) series;
      final Marker snoozeMarker = new ValueMarker(timeSeriesItem.getPeriod()
          .getMiddleMillisecond());
      snoozeMarker.setPaint(SNOOZE_PAINT);
      snoozeMarker.setOutlinePaint(null);
      snoozeMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(snoozeMarker, Layer.FOREGROUND);
    }
    // wakeup time
    if (wakeupTime != null) {
      final Marker wakeupMarker = new ValueMarker((double) wakeupTime.getTime());
      wakeupMarker.setPaint(WAKE_PAINT);
      wakeupMarker.setOutlinePaint(null);
      wakeupMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(wakeupMarker, Layer.FOREGROUND);
    }
  }

  private String getTitle() {
    // initialize view
    final StringBuffer titleBuf = new StringBuffer();
    if (sleepData.getName() != null) {
      titleBuf.append(sleepData.getName()).append(" - ");
    }
    titleBuf.append(DateFormat.getDateInstance(DateFormat.MEDIUM).
        format(sleepData.calculateStartTime()));
    return titleBuf.toString();
  }

  /**
   * Removes the PropertyChangeListeners from the TimeSeries objects. Otherwise
   * the Panels will not be removed from memory, because they are linked to the
   * SleepData objects in the SleepDataTableModel, which results in a nice
   * memory leak.
   */
  public void close() {
    final Container parent = getParent();
    parent.remove(this);
  }

  public void doCopy() {
    chartPanel.doCopy();
  }

  public void doSaveAsPNG() {
    try {
      chartPanel.doSaveAs();
    } catch (IOException ex) {
      log.error("failed to save chart as png", ex);
    }
  }

  public void zoom(final SleepData sleepData,
      final String fromText,
      final Integer duration,
      final Range range) {
    try {
      long sleepDataStart = sleepData.calculateStartTime().getTime();
      long sleepDataEnd = sleepData.calculateEndTime().getTime();

      int startHours = Integer.parseInt(fromText.split(":")[0]);
      int minutes = Integer.parseInt(fromText.split(":")[1]);
      if (startHours < 0 || startHours > 23 || minutes < 0 || minutes > 59) {
        throw new NumberFormatException();
      }
      int startMinutes = startHours * 60 + minutes;

      long start = getZoomTimeMillis(sleepDataStart, startMinutes);
      long end = (1000L * duration * 60) + start;

      if (end < sleepDataStart) {
        start += 24 * 60 * 60 * 1000;
        end = start + (duration * 60 * 1000);
      }

      if (start > sleepDataEnd) {
        start -= 24 * 60 * 60 * 1000;
        end = start + (duration * 60 * 1000);
      }

      // perform the the zooming
      XYPlot plot = chartPanel.getChart().getXYPlot();
      if (plot instanceof CombinedDomainXYPlot) {
        plot = (XYPlot) ((CombinedDomainXYPlot) plot).getSubplots().get(0);
      }
      if (plot.getRenderer() instanceof XYBarRenderer) {
        plot.getRangeAxis().setAutoRange(false);
        plot.getRangeAxis().setRange(range);
      }
      plot.getDomainAxis().setRange(start - 1000, end + 1000);

    } catch (NumberFormatException | NullPointerException ex) {
      log.warn("invalid zoom input", ex);
      chartPanel.restoreAutoBounds();
    }
  }

  private long getZoomTimeMillis(final long sleepDataStart,
      final int timeOfDayInMinutes) {
    Calendar calRecorded = Calendar.getInstance();
    calRecorded.setTime(new Date(sleepDataStart));
    calRecorded.set(Calendar.HOUR_OF_DAY, 0);
    calRecorded.set(Calendar.MINUTE, 0);
    calRecorded.set(Calendar.SECOND, 0);
    calRecorded.set(Calendar.MILLISECOND, 0);

    long zoomTime = (1000L * timeOfDayInMinutes * 60) + calRecorded
        .getTimeInMillis();
    return zoomTime;
  }

  @Override
  public int print(final Graphics graphics, final PageFormat pageFormat,
      final int pageIndex) throws PrinterException {

    if (graphics instanceof Graphics2D) {
      final Graphics2D printer = (Graphics2D) graphics;

      printer.setFont(new Font("SansSerif", Font.PLAIN, PRINT_FONT_SIZE));
      int lineHeight = printer.getFontMetrics().getHeight() + 4;

      double x = pageFormat.getImageableX() + INSET;
      double y = pageFormat.getImageableY() + INSET;
      double w = pageFormat.getImageableWidth() - 2 * INSET;
      double h = pageFormat.getImageableHeight() - 2 * INSET;

      disableDoubleBuffering(chartPanel);
      createChart(ChartType.COMBINED).draw(printer, new Rectangle2D.Double(x, y
          + lineHeight * 3, w, h - lineHeight * 6));
      enableDoubleBuffering(chartPanel);

      x = x + INSET;
      w = w - 2 * INSET;

      printer.setColor(GRID_COLOR);
      printer.drawString(getTitle(), (int) (x), (int) y + lineHeight * 2);
      printer.setColor(SLEEP_MARKER_PAINT);
      printer.drawString(lblSleepStart.getText() + " " + lblSleepStartValue.
          getText(), (int) (x), (int) (y + h - lineHeight * 2));
      printer.setColor(GRID_COLOR);
      printer.drawString(lblLatency.getText() + " " + lblLatencyValue.getText(),
          (int) (x + w / 4 * 1), (int) (y + h - lineHeight * 2));
      printer.setColor(GRID_COLOR);
      printer.drawString(lblDuration.getText() + " " + lblDurationValue
          .getText(), (int) (x + w / 4 * 2), (int) (y + h - lineHeight * 2));
      printer.setColor(WAKE_PAINT);
      printer.drawString(lblWakeupTime.getText() + " " + lblWakeupTimeValue.
          getText(), (int) (x + w / 4 * 3), (int) (y + h - lineHeight * 2));
      printer.setColor(WAKE_INTERVALL_END_PAINT);
      printer.drawString(lblLatest.getText() + " " + lblLatestValue.getText(),
          (int) (x), (int) (y + h - lineHeight * 1));
      printer.setColor(GRID_COLOR);
      printer.drawString(lblTimeSaving.getText() + " " + lblTimeSavingValue.
          getText(), (int) (x + w / 4 * 1), (int) (y + h - lineHeight * 1));
      printer.setColor(BAR_COLOR);
      printer.drawString(lblMovementsCount.getText() + " "
          + lblMovementsCountValue.
          getText(), (int) (x + w / 4 * 2), (int) (y + h - lineHeight * 1));
      printer.setColor(GRID_COLOR);
      printer.drawString(lblMovementsAverage.getText() + " "
          + lblMovementsAverageValue.
          getText(), (int) (x + w / 4 * 3), (int) (y + h - lineHeight * 1));

      if (lblCommentValue.getText() != null && lblCommentValue.getText().trim()
          .length() > 0) {
        printer.setColor(GRID_COLOR);
        printer.drawString(BundleUtil.getMessage("dataframe.label.comment")
            + " " + lblCommentValue.getText(), (int) (x + w / 4 * 0),
            (int) (y + h - lineHeight * 0));
      }
    }
    return PAGE_EXISTS;
  }

  private static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  private static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }

  public void updateChart() {
    chartPanel.restoreAutoRangeBounds();
  }

  public void updateStats(final SleepData sleepData) {
    // set title
    ((TitledBorder) getBorder()).setTitle(getTitle());

    lblIdValue.setText(sleepData.getId());
    lblNameValue.setText(sleepData.getName());

    if (sleepData.getWakeIntervalStart() != null || sleepData.getWakeupTime()
        != null) {
      final Date sleepStart = sleepData.calculateSleepStart();
      lblSleepStartValue.setText(String.format("%tR", sleepStart));
      lblSleepStart.setVisible(true);
      lblSleepStartValue.setVisible(true);
      lblSpacerSleepStart.setVisible(true);

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(new Date(sleepData.calculateLatency()));
      lblLatencyValue.setText(String.format("%tT", cal));
      lblLatency.setVisible(true);
      lblLatencyValue.setVisible(true);
      lblSpacerLatency.setVisible(true);

      cal.setTime(new Date(sleepData.calculateDuration()));
      lblDurationValue.setText(String.format("%tT", cal));
      lblDuration.setVisible(true);
      lblDurationValue.setVisible(true);
      lblSpacerDuration.setVisible(true);

      if (sleepData.getWakeupTime() != null) {
        lblWakeupTime.setVisible(true);
        lblWakeupTimeValue.setVisible(true);
        lblWakeupTimeValue.setText(String.format("%tR",
            sleepData.getWakeupTime()));
        lblSpacerWakeupTime.setVisible(true);
      } else {
        lblWakeupTime.setVisible(false);
        lblWakeupTimeValue.setVisible(false);
        lblSpacerWakeupTime.setVisible(false);
      }

      if (sleepData.calculateTimeSaving() != 0) {
        cal.setTime(new Date(sleepData.calculateTimeSaving()));
        lblTimeSavingValue.setText(String.format("%tR", cal));
        lblTimeSaving.setVisible(true);
        lblTimeSavingValue.setVisible(true);
        lblSpacerTimeSaving.setVisible(true);
      } else {
        lblTimeSaving.setVisible(false);
        lblTimeSavingValue.setVisible(false);
        lblSpacerTimeSaving.setVisible(false);
      }

      if (sleepData.getWakeIntervalStart() != null) {
        long end = sleepData.calculateWakeIntervalEnd().getTime();
        lblLatestValue.setText(String.format("%tR", end));
        lblLatest.setVisible(true);
        lblLatestValue.setVisible(true);
        lblSpacerLatest.setVisible(true);
      } else {
        lblLatest.setVisible(false);
        lblLatestValue.setVisible(false);
        lblSpacerLatest.setVisible(false);
      }
    } else {
      lblSleepStart.setVisible(false);
      lblSleepStartValue.setVisible(false);
      lblSpacerSleepStart.setVisible(false);
      lblLatency.setVisible(false);
      lblLatencyValue.setVisible(false);
      lblSpacerLatency.setVisible(false);
      lblDuration.setVisible(false);
      lblDurationValue.setVisible(false);
      lblSpacerDuration.setVisible(false);
      lblLatest.setVisible(false);
      lblLatestValue.setVisible(false);
      lblSpacerLatest.setVisible(false);
      lblTimeSaving.setVisible(false);
      lblTimeSavingValue.setVisible(false);
      lblSpacerTimeSaving.setVisible(false);
      lblWakeupTime.setVisible(false);
      lblWakeupTimeValue.setVisible(false);
      lblSpacerWakeupTime.setVisible(false);
    }

    if (sleepData.isPowerNap()) {
      lblSleepStart.setText(BundleUtil.getMessage("label.powerNapStart"));
      lblTimeSaving.setVisible(false);
      lblTimeSavingValue.setVisible(false);
      lblSpacerTimeSaving.setVisible(false);
      lblMovementsAverage.setVisible(false);
      lblMovementsAverageValue.setVisible(false);
      lblLatency.setVisible(false);
      lblLatencyValue.setVisible(false);
      lblSpacerLatency.setVisible(false);
      lblSpacerMovementsCount.setVisible(false);
    }

    lblMovementsCountValue.setText(String.format("%d",
        sleepData.calculateMovementCount()));
    lblMovementsAverageValue.setText(String.format("%.1f",
        sleepData.calculateMovementsPerHour()));

    if (sleepData.getComment() == null || sleepData.getComment().trim().length()
        == 0) {
      lblComment.setVisible(false);
      lblCommentValue.setVisible(false);
    } else {
      lblComment.setVisible(true);
      lblCommentValue.setVisible(true);
      lblCommentValue.setText(sleepData.getComment());
    }

    repaint();
  }

  public SleepData getSleepData() {
    return sleepData;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    chartPanelPopupMenu = new javax.swing.JPopupMenu();
    pmniCopy = new javax.swing.JMenuItem();
    pmniPrint = new javax.swing.JMenuItem();
    pmniSaveAsPNG = new javax.swing.JMenuItem();
    pnlChart = new javax.swing.JPanel();
    pnlInfo = new javax.swing.JPanel();
    pnlStats = new javax.swing.JPanel();
    lblDuration = new javax.swing.JLabel();
    lblDurationValue = new javax.swing.JLabel();
    lblSpacerDuration = new javax.swing.JLabel();
    lblSleepStart = new javax.swing.JLabel();
    lblSleepStartValue = new javax.swing.JLabel();
    lblSpacerSleepStart = new javax.swing.JLabel();
    lblLatency = new javax.swing.JLabel();
    lblLatencyValue = new javax.swing.JLabel();
    lblSpacerLatency = new javax.swing.JLabel();
    lblWakeupTime = new javax.swing.JLabel();
    lblWakeupTimeValue = new javax.swing.JLabel();
    lblSpacerWakeupTime = new javax.swing.JLabel();
    lblLatest = new javax.swing.JLabel();
    lblLatestValue = new javax.swing.JLabel();
    lblSpacerLatest = new javax.swing.JLabel();
    lblTimeSaving = new javax.swing.JLabel();
    lblTimeSavingValue = new javax.swing.JLabel();
    lblSpacerTimeSaving = new javax.swing.JLabel();
    lblMovementsCount = new javax.swing.JLabel();
    lblMovementsCountValue = new javax.swing.JLabel();
    lblSpacerMovementsCount = new javax.swing.JLabel();
    lblMovementsAverage = new javax.swing.JLabel();
    lblMovementsAverageValue = new javax.swing.JLabel();
    pnlComment = new javax.swing.JPanel();
    lblComment = new javax.swing.JLabel();
    lblCommentValue = new javax.swing.JLabel();
    lblIdValue = new javax.swing.JLabel();
    lblNameValue = new javax.swing.JLabel();
    pnlClose = new javax.swing.JPanel();
    btnClose = new javax.swing.JButton();

    pmniCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/clipboard-16.png"))); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/default"); // NOI18N
    pmniCopy.setText(bundle.getString("chart.popup.copy")); // NOI18N
    pmniCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pmniCopyActionPerformed(evt);
      }
    });
    chartPanelPopupMenu.add(pmniCopy);

    pmniPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer-16.png"))); // NOI18N
    pmniPrint.setText(bundle.getString("chart.popup.print")); // NOI18N
    pmniPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pmniPrintActionPerformed(evt);
      }
    });
    chartPanelPopupMenu.add(pmniPrint);

    pmniSaveAsPNG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/desktop-16.png"))); // NOI18N
    pmniSaveAsPNG.setText(bundle.getString("chart.popup.save")); // NOI18N
    pmniSaveAsPNG.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pmniSaveAsPNGActionPerformed(evt);
      }
    });
    chartPanelPopupMenu.add(pmniSaveAsPNG);

    setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    setLayout(new java.awt.BorderLayout());

    pnlChart.setMaximumSize(new java.awt.Dimension(10, 10));
    pnlChart.setLayout(new java.awt.BorderLayout());
    add(pnlChart, java.awt.BorderLayout.CENTER);

    pnlInfo.setLayout(new java.awt.BorderLayout());

    pnlStats.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

    lblDuration.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblDuration.setForeground(SLEEP_DURATION_PAINT);
    lblDuration.setText(bundle.getString("label.sleepDuration")); // NOI18N
    pnlStats.add(lblDuration);

    lblDurationValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblDurationValue.setForeground(SLEEP_DURATION_PAINT);
    lblDurationValue.setText("--:--:--");
    pnlStats.add(lblDurationValue);

    lblSpacerDuration.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerDuration.setForeground(AXIS_COLOR);
    lblSpacerDuration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerDuration.setText("|");
    lblSpacerDuration.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerDuration.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerDuration.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerDuration);

    lblSleepStart.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepStart.setForeground(SLEEP_MARKER_PAINT);
    lblSleepStart.setText(bundle.getString("label.sleepStart")); // NOI18N
    pnlStats.add(lblSleepStart);

    lblSleepStartValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepStartValue.setForeground(SLEEP_MARKER_PAINT);
    lblSleepStartValue.setText("--:--");
    pnlStats.add(lblSleepStartValue);

    lblSpacerSleepStart.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerSleepStart.setForeground(new java.awt.Color(174, 173, 173));
    lblSpacerSleepStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerSleepStart.setText("|");
    lblSpacerSleepStart.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerSleepStart.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerSleepStart.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerSleepStart);

    lblLatency.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLatency.setForeground(AXIS_COLOR);
    lblLatency.setText(bundle.getString("dataFrame.label.latency")); // NOI18N
    lblLatency.setToolTipText("");
    pnlStats.add(lblLatency);

    lblLatencyValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLatencyValue.setForeground(AXIS_COLOR);
    lblLatencyValue.setText("--:--");
    pnlStats.add(lblLatencyValue);

    lblSpacerLatency.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerLatency.setForeground(AXIS_COLOR);
    lblSpacerLatency.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerLatency.setText("|");
    lblSpacerLatency.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerLatency.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerLatency.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerLatency);

    lblWakeupTime.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblWakeupTime.setForeground(WAKE_PAINT);
    lblWakeupTime.setText(bundle.getString("label.wakeupTime")); // NOI18N
    pnlStats.add(lblWakeupTime);

    lblWakeupTimeValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblWakeupTimeValue.setForeground(WAKE_PAINT);
    lblWakeupTimeValue.setText("--:--");
    pnlStats.add(lblWakeupTimeValue);

    lblSpacerWakeupTime.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerWakeupTime.setForeground(AXIS_COLOR);
    lblSpacerWakeupTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerWakeupTime.setText("|");
    lblSpacerWakeupTime.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerWakeupTime.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerWakeupTime.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerWakeupTime);

    lblLatest.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLatest.setForeground(WAKE_INTERVALL_END_PAINT);
    lblLatest.setText(bundle.getString("label.latest")); // NOI18N
    pnlStats.add(lblLatest);

    lblLatestValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLatestValue.setForeground(WAKE_INTERVALL_END_PAINT);
    lblLatestValue.setText("--:--");
    pnlStats.add(lblLatestValue);

    lblSpacerLatest.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerLatest.setForeground(AXIS_COLOR);
    lblSpacerLatest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerLatest.setText("|");
    lblSpacerLatest.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerLatest.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerLatest.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerLatest);

    lblTimeSaving.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblTimeSaving.setForeground(WAKE_PAINT);
    lblTimeSaving.setText(bundle.getString("label.timeSaving")); // NOI18N
    pnlStats.add(lblTimeSaving);

    lblTimeSavingValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblTimeSavingValue.setForeground(WAKE_PAINT);
    lblTimeSavingValue.setText("--:--");
    pnlStats.add(lblTimeSavingValue);

    lblSpacerTimeSaving.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerTimeSaving.setForeground(AXIS_COLOR);
    lblSpacerTimeSaving.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerTimeSaving.setText("|");
    lblSpacerTimeSaving.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerTimeSaving.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerTimeSaving.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerTimeSaving);

    lblMovementsCount.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblMovementsCount.setForeground(BAR_COLOR);
    lblMovementsCount.setText(bundle.getString("dataFrame.label.movementsCount")); // NOI18N
    pnlStats.add(lblMovementsCount);

    lblMovementsCountValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblMovementsCountValue.setForeground(BAR_COLOR);
    lblMovementsCountValue.setText("-");
    pnlStats.add(lblMovementsCountValue);

    lblSpacerMovementsCount.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacerMovementsCount.setForeground(AXIS_COLOR);
    lblSpacerMovementsCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacerMovementsCount.setText("|");
    lblSpacerMovementsCount.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacerMovementsCount.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacerMovementsCount.setPreferredSize(new java.awt.Dimension(14, 14));
    pnlStats.add(lblSpacerMovementsCount);

    lblMovementsAverage.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblMovementsAverage.setForeground(new java.awt.Color(174, 173, 173));
    lblMovementsAverage.setText(bundle.getString("dataFrame.label.movementsAverage")); // NOI18N
    pnlStats.add(lblMovementsAverage);

    lblMovementsAverageValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblMovementsAverageValue.setForeground(new java.awt.Color(174, 173, 173));
    lblMovementsAverageValue.setText("-");
    pnlStats.add(lblMovementsAverageValue);

    pnlInfo.add(pnlStats, java.awt.BorderLayout.CENTER);

    pnlComment.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

    lblComment.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblComment.setForeground(AXIS_COLOR);
    lblComment.setText(bundle.getString("dataframe.label.comment")); // NOI18N
    pnlComment.add(lblComment);

    lblCommentValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblCommentValue.setForeground(new java.awt.Color(174, 173, 173));
    pnlComment.add(lblCommentValue);

    lblIdValue.setVisible(false);
    lblIdValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblIdValue.setForeground(AXIS_COLOR);
    lblIdValue.setText(bundle.getString("label.id")); // NOI18N
    pnlComment.add(lblIdValue);

    lblNameValue.setVisible(false);
    lblNameValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblNameValue.setForeground(AXIS_COLOR);
    lblNameValue.setText(bundle.getString("label.name")); // NOI18N
    pnlComment.add(lblNameValue);

    pnlInfo.add(pnlComment, java.awt.BorderLayout.SOUTH);

    add(pnlInfo, java.awt.BorderLayout.SOUTH);

    pnlClose.setPreferredSize(new java.awt.Dimension(16, 16));
    pnlClose.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

    btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/button-cross-16.png"))); // NOI18N
    btnClose.setBorderPainted(false);
    btnClose.setContentAreaFilled(false);
    btnClose.setFocusable(false);
    btnClose.setIconTextGap(2);
    btnClose.setMultiClickThreshhold(1000L);
    btnClose.setPreferredSize(new java.awt.Dimension(16, 16));
    btnClose.setRequestFocusEnabled(false);
    btnClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCloseActionPerformed(evt);
      }
    });
    pnlClose.add(btnClose);

    add(pnlClose, java.awt.BorderLayout.EAST);
  }// </editor-fold>//GEN-END:initComponents

  private void pmniCopyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmniCopyActionPerformed
  {//GEN-HEADEREND:event_pmniCopyActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramCopy(
        this));
  }//GEN-LAST:event_pmniCopyActionPerformed

  private void pmniPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmniPrintActionPerformed
  {//GEN-HEADEREND:event_pmniPrintActionPerformed
    final PrinterJob job = PrinterJob.getPrinterJob();
    if (job.printDialog()) {
      final Book book = new Book();
      job.setPageable(book);
      job.setJobName(getSleepData().getSleepDataFilename());
      ApplicationEventDispatcher.getInstance().dispatchEvent(new DiagramPrint(
          this, job, book));
      try {
        job.print();
      } catch (PrinterException ex) {
        final String msg = BundleUtil.getErrorMessage(
            "globalError.printingFailed");
        log.error(msg, ex);
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new ApplicationMessageEvent(
                this, msg, true));
      }
    }
  }//GEN-LAST:event_pmniPrintActionPerformed

  private void pmniSaveAsPNGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmniSaveAsPNGActionPerformed
  {//GEN-HEADEREND:event_pmniSaveAsPNGActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new DiagramSaveAsPNG(
            this));
  }//GEN-LAST:event_pmniSaveAsPNGActionPerformed

  private void btnCloseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCloseActionPerformed
  {//GEN-HEADEREND:event_btnCloseActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramClose(
        this, this));
  }//GEN-LAST:event_btnCloseActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnClose;
  private javax.swing.JPopupMenu chartPanelPopupMenu;
  private javax.swing.JLabel lblComment;
  private javax.swing.JLabel lblCommentValue;
  private javax.swing.JLabel lblDuration;
  private javax.swing.JLabel lblDurationValue;
  private javax.swing.JLabel lblIdValue;
  private javax.swing.JLabel lblLatency;
  private javax.swing.JLabel lblLatencyValue;
  private javax.swing.JLabel lblLatest;
  private javax.swing.JLabel lblLatestValue;
  private javax.swing.JLabel lblMovementsAverage;
  private javax.swing.JLabel lblMovementsAverageValue;
  private javax.swing.JLabel lblMovementsCount;
  private javax.swing.JLabel lblMovementsCountValue;
  private javax.swing.JLabel lblNameValue;
  private javax.swing.JLabel lblSleepStart;
  private javax.swing.JLabel lblSleepStartValue;
  private javax.swing.JLabel lblSpacerDuration;
  private javax.swing.JLabel lblSpacerLatency;
  private javax.swing.JLabel lblSpacerLatest;
  private javax.swing.JLabel lblSpacerMovementsCount;
  private javax.swing.JLabel lblSpacerSleepStart;
  private javax.swing.JLabel lblSpacerTimeSaving;
  private javax.swing.JLabel lblSpacerWakeupTime;
  private javax.swing.JLabel lblTimeSaving;
  private javax.swing.JLabel lblTimeSavingValue;
  private javax.swing.JLabel lblWakeupTime;
  private javax.swing.JLabel lblWakeupTimeValue;
  private javax.swing.JMenuItem pmniCopy;
  private javax.swing.JMenuItem pmniPrint;
  private javax.swing.JMenuItem pmniSaveAsPNG;
  private javax.swing.JPanel pnlChart;
  private javax.swing.JPanel pnlClose;
  private javax.swing.JPanel pnlComment;
  private javax.swing.JPanel pnlInfo;
  private javax.swing.JPanel pnlStats;
  // End of variables declaration//GEN-END:variables
}
