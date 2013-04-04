/*
 * © 2008 3kraft
 * $Id: DataFrame.java,v 1.39 2010-12-29 15:20:34 illetsch Exp $
 */
package com.dreikraft.axbo.gui;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.timeseries.KeyTimeSeries;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.timeseries.SleepDataTimeSeries;
import com.dreikraft.axbo.events.DiagramCopy;
import com.dreikraft.axbo.events.DiagramPrint;
import com.dreikraft.axbo.events.DiagramSaveAsPNG;
import com.dreikraft.axbo.events.DiagramClose;
import com.dreikraft.axbo.util.BundleUtil;
import java.awt.BasicStroke;
import java.awt.Color;
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
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.Align;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

/**
 * $Id: DataFrame.java,v 1.39 2010-12-29 15:20:34 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.39 $
 */
public class DataFrame extends JPanel implements Printable
{

  public static final Log log = LogFactory.getLog(DataFrame.class);
  public static final Color CHART_BG_COLOR = new Color(0, 0, 0, 0);
  public static final Color AXIS_COLOR = new Color(80, 80, 80, 200);
  public static final Color GRID_COLOR = new Color(80, 80, 80, 200);
  public static final Color BAR_COLOR = new Color(255, 212, 107, 255);
  public static final Color BAR_COLOR2 = new Color(155, 112, 7, 255);
  public static final GradientPaint BAR_PAINT =
      new GradientPaint(0f, 0f, BAR_COLOR, 0f, 0f, BAR_COLOR2);
  public static final Color SLEEP_MARKER_PAINT = new Color(255, 107, 212, 255);
  public static final Color WAKE_PAINT = new Color(107, 255, 212, 255);
  public static final Color SNOOZE_PAINT = new Color(255, 255, 255, 255);
  public static final Color WAKE_INTERVALL_PAINT = new Color(155, 112, 7, 64);
  public static final Color KEY_PAINT = new Color(212, 107, 255, 255);
  public static final Stroke MARKER_STROKE = new BasicStroke(1.5f);
  private static final int INSET = 20;
  private static final int PRINT_FONT_SIZE = 8;
  private ChartPanel chartPanel;
  private SleepData sleepData;

  public void init()
  {
    initComponents();
  }

  public void createNewChart(
      final IntervalXYDataset dataset,
      final Date start,
      final Date sleepStart,
      final Date wakeIntervalStart,
      final Date wakeupTime,
      final KeyTimeSeries keys,
      final KeyTimeSeries snoozes)
  {
    // set sleepData
    final TimeSeries timeSeries = ((TimeSeriesCollection) dataset).getSeries(0);
    sleepData = ((SleepDataTimeSeries) timeSeries).getSleepData();

    // set title
    ((TitledBorder) getBorder()).setTitle(getTitle());

    // create a new chart
    ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
    final JFreeChart chart = ChartFactory.createXYBarChart(null,
        null, true, null, dataset, PlotOrientation.VERTICAL, false, true, false);

    // customize the chart
    chart.setBackgroundPaint(CHART_BG_COLOR);
    chart.setBorderVisible(false);
    chart.setAntiAlias(true);
    chart.setTextAntiAlias(true);

    // customize the plot
    final XYPlot plot = chart.getXYPlot();
    plot.setInsets(new RectangleInsets(5, 5, 5, 5));
    plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
    plot.setBackgroundPaint(CHART_BG_COLOR);
    plot.setBackgroundImageAlpha(0.05f);
    plot.setBackgroundImageAlignment(Align.CENTER);
    try
    {
      plot.setBackgroundImage(ImageIO.read(getClass().
          getResource("/resources/images/aXbo-logo-software-small.png")));
    }
    catch (IOException ex)
    {
      log.error(ex.getMessage(), ex);
    }
    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlineStroke(new BasicStroke());
    plot.setRangeGridlinePaint(GRID_COLOR);
    plot.setDomainGridlinesVisible(false);
    plot.setOutlineVisible(false);

    // set domain axis
    final DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
    domainAxis.setTickLabelPaint(AXIS_COLOR);

    // set axis units
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setAxisLineVisible(false);
    rangeAxis.setTickLabelsVisible(true);
    rangeAxis.setTickMarksVisible(false);
    rangeAxis.setTickLabelPaint(AXIS_COLOR);

    // draw movements
    final XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setShadowVisible(false);
    renderer.setDrawBarOutline(false);
    renderer.setSeriesPaint(0, BAR_PAINT);

    // draw sleep start
    final Marker sleepStartMarker = new ValueMarker(sleepStart.getTime());
    sleepStartMarker.setPaint(SLEEP_MARKER_PAINT);
    sleepStartMarker.setStroke(MARKER_STROKE);
    sleepStartMarker.setOutlinePaint(null);
    plot.addDomainMarker(sleepStartMarker, Layer.FOREGROUND);

    // wake intervall marker
    if (wakeIntervalStart != null)
    {
      final Marker wakeInterval = new IntervalMarker(wakeIntervalStart.getTime(),
          wakeIntervalStart.getTime() + SleepData.WAKE_INTERVAL);
      wakeInterval.setPaint(WAKE_INTERVALL_PAINT);
      wakeInterval.setOutlinePaint(null);
      plot.addDomainMarker(wakeInterval, Layer.BACKGROUND);
    }

    // wakeup time
    if (wakeupTime != null)
    {
      final Marker wakeupMarker = new ValueMarker((double) wakeupTime.getTime());
      wakeupMarker.setPaint(WAKE_PAINT);
      wakeupMarker.setOutlinePaint(null);
      wakeupMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(wakeupMarker, Layer.FOREGROUND);
    }

    // sensor keys
    for (final Object series : keys.getItems())
    {
      final TimeSeriesDataItem timeSeriesItem = (TimeSeriesDataItem) series;
      final Marker keyMarker =
          new ValueMarker(timeSeriesItem.getPeriod().getMiddleMillisecond());
      keyMarker.setPaint(KEY_PAINT);
      keyMarker.setOutlinePaint(null);
      keyMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(keyMarker, Layer.BACKGROUND);
    }
    keys.close();

    // snooze keys
    for (final Object series : snoozes.getItems())
    {
      final TimeSeriesDataItem timeSeriesItem = (TimeSeriesDataItem) series;
      final Marker snoozeMarker =
          new ValueMarker(timeSeriesItem.getPeriod().getMiddleMillisecond());
      snoozeMarker.setPaint(SNOOZE_PAINT);
      snoozeMarker.setOutlinePaint(null);
      snoozeMarker.setStroke(MARKER_STROKE);
      plot.addDomainMarker(snoozeMarker, Layer.BACKGROUND);
    }
    snoozes.close();

    // create a new chart panel
    if (chartPanel != null)
    {
      pnlChart.removeAll();
    }
    chartPanel = new ChartPanel(chart, true);
    chartPanel.setBorder(null);
    chartPanel.setRangeZoomable(false);
    chartPanel.setPopupMenu(chartPanelPopupMenu);
    pnlChart.add(chartPanel);
  }

  private String getTitle()
  {
    // initialize view
    final StringBuffer titleBuf = new StringBuffer();
    if (sleepData.getName() != null)
    {
      titleBuf.append(sleepData.getName()).append(" - ");
    }
    titleBuf.append(DateFormat.getDateInstance(DateFormat.MEDIUM).
        format(sleepData.calculateStartTime()));
    return titleBuf.toString();
  }

  /**
   * Removes the PropertyChangeListeners from the TimeSeries objects. Otherwise
   * the Panels will not be removed from memory, because they are linked to
   * the SleepData objects in the SleepDataTableModel, which results in a nice
   * memory leak.
   */
  public void close()
  {
    final JFreeChart chart = chartPanel.getChart();
    final XYPlot plot = chart.getXYPlot();

    final TimeSeriesCollection dataset =
        (TimeSeriesCollection) plot.getDataset();
    for (final Object timeSeries : dataset.getSeries())
    {
      if (timeSeries instanceof SleepDataTimeSeries)
      {
        ((SleepDataTimeSeries) timeSeries).close();
      }
      else if (timeSeries instanceof KeyTimeSeries)
      {
        ((KeyTimeSeries) timeSeries).close();
      }
    }
    final Container parent = getParent();
    parent.remove(this);
  }

  public void doCopy()
  {
    chartPanel.doCopy();
  }

  public void doSaveAsPNG()
  {
    try
    {
      chartPanel.doSaveAs();
    }
    catch (IOException ex)
    {
      log.error("failed to save chart as png", ex);
    }
  }

  public void zoom(final SleepData sleepData,
      final String fromText,
      final Integer range)
  {
    try
    {
      long sleepDataStart = sleepData.calculateStartTime().getTime();
      long sleepDataEnd = sleepData.calculateEndTime().getTime();

      int startHours = Integer.valueOf(fromText.split(":")[0]).intValue();
      int minutes = Integer.valueOf(fromText.split(":")[1]).intValue();
      if (startHours < 0 || startHours > 23 || minutes < 0 || minutes > 59)
      {
        throw new NumberFormatException();
      }
      int startMinutes = startHours * 60 + minutes;

      long start = getZoomTimeMillis(sleepDataStart, startMinutes);
      long end = start + (range * 60 * 1000);

      if (end < sleepDataStart)
      {
        start += 24 * 60 * 60 * 1000;
        end = start + (range * 60 * 1000);
      }

      if (start > sleepDataEnd)
      {
        start -= 24 * 60 * 60 * 1000;
        end = start + (range * 60 * 1000);
      }

      chartPanel.getChart().getXYPlot().getDomainAxis().setRange(start - 1000,
          end + 1000);
    }
    catch (Exception ex)
    {
      log.warn("invalid zoom input", ex);
      chartPanel.restoreAutoBounds();
    }
  }

  private long getZoomTimeMillis(final long sleepDataStart,
      final int timeOfDayInMinutes)
  {
    Calendar calRecorded = Calendar.getInstance();
    calRecorded.setTime(new Date(sleepDataStart));
    calRecorded.set(Calendar.HOUR_OF_DAY, 0);
    calRecorded.set(Calendar.MINUTE, 0);
    calRecorded.set(Calendar.SECOND, 0);
    calRecorded.set(Calendar.MILLISECOND, 0);

    long zoomTime = calRecorded.getTimeInMillis() + (timeOfDayInMinutes * 60
        * 1000);
    return zoomTime;
  }

  @Override
  public int print(final Graphics graphics, final PageFormat pageFormat,
      final int pageIndex) throws PrinterException
  {
    final Graphics2D printer = (Graphics2D) graphics;

    double x = pageFormat.getImageableX() + INSET;
    double y = pageFormat.getImageableY() + INSET;
    double w = pageFormat.getImageableWidth() - 2 * INSET;
    double h = pageFormat.getImageableHeight() - 2 * INSET;

    chartPanel.getChart().draw(printer, new Rectangle2D.Double(x, y, w, h - PRINT_FONT_SIZE
        * 7));

    printer.setColor(new Color(80, 80, 80, 200));
    printer.setFont(new Font("SansSerif", Font.PLAIN, PRINT_FONT_SIZE));
    int lineHeight = printer.getFontMetrics().getHeight();

    x = x + INSET;
    w = w - 2 * INSET;

    printer.drawString(BundleUtil.getMessage("label.name") + " " + lblNameValue.
        getText(), (int) (x + w / 4 * 0), (int) (y + h - lineHeight * 4));
    printer.drawString(BundleUtil.getMessage("label.id") + " " + lblIdValue.
        getText(),
        (int) (x + w / 4 * 1), (int) (y + h - lineHeight * 4));
    printer.drawString(lblSleepStart.getText() + " " + lblSleepStartValue.
        getText(),
        (int) (x + w / 4 * 0), (int) (y + h - lineHeight * 3));
    printer.drawString(lblLatency.getText() + " " + lblLatencyValue.getText(),
        (int) (x + w / 4 * 1), (int) (y + h - lineHeight * 3));
    printer.drawString(lblDuration.getText() + " " + lblDurationValue.getText(),
        (int) (x + w / 4 * 2), (int) (y + h - lineHeight * 3));
    printer.drawString(lblWakeupTime.getText() + " " + lblWakeupTimeValue.
        getText(),
        (int) (x + w / 4 * 3), (int) (y + h - lineHeight * 3));
    printer.drawString(lblLatest.getText() + " " + lblLatestValue.getText(),
        (int) (x + w / 4 * 0), (int) (y + h - lineHeight * 2));
    printer.drawString(lblTimeSaving.getText() + " " + lblTimeSavingValue.
        getText(),
        (int) (x + w / 4 * 1), (int) (y + h - lineHeight * 2));
    printer.drawString(lblMovementsCount.getText() + " " + lblMovementsCountValue.
        getText(), (int) (x + w / 4 * 2), (int) (y + h - lineHeight * 2));
    printer.drawString(lblMovementsAverage.getText() + " " + lblMovementsAverageValue.
        getText(), (int) (x + w / 4 * 3), (int) (y + h - lineHeight * 2));
    printer.drawString(BundleUtil.getMessage("dataframe.label.comment") + " " + lblCommentValue.
        getText(), (int) (x + w / 4 * 0), (int) (y + h - lineHeight * 1));

    return PAGE_EXISTS;
  }

  public void updateChart()
  {
    chartPanel.restoreAutoRangeBounds();
  }

  public void updateStats(final SleepData sleepData)
  {
    // set title
    ((TitledBorder) getBorder()).setTitle(getTitle());

    lblIdValue.setText(sleepData.getId());
    lblNameValue.setText(sleepData.getName());

    if (sleepData.getWakeIntervalStart() != null || sleepData.getWakeupTime()
        != null)
    {
      final Date sleepStart = sleepData.calculateSleepStart();
      lblSleepStartValue.setText(String.format("%tR", sleepStart));
      lblSleepStart.setVisible(true);
      lblSleepStartValue.setVisible(true);

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      cal.setTime(new Date(sleepData.calculateLatency()));
      lblLatencyValue.setText(String.format("%tT", cal));
      lblLatency.setVisible(true);
      lblLatencyValue.setVisible(true);

      cal.setTime(new Date(sleepData.calculateDuration()));
      lblDurationValue.setText(String.format("%tT", cal));
      lblDuration.setVisible(true);
      lblDurationValue.setVisible(true);

      if (sleepData.getWakeupTime() != null)
      {
        lblWakeupTime.setVisible(true);
        lblWakeupTimeValue.setVisible(true);
        lblWakeupTimeValue.setText(String.format("%tR",
            sleepData.getWakeupTime()));
      }
      else
      {
        lblWakeupTime.setVisible(false);
        lblWakeupTimeValue.setVisible(false);
      }

      if (sleepData.calculateTimeSaving() != SleepData.UNSET)
      {
        cal.setTime(new Date(sleepData.calculateTimeSaving()));
        lblTimeSavingValue.setText(String.format("%tR", cal));
        lblTimeSaving.setVisible(true);
        lblTimeSavingValue.setVisible(true);
      }
      else
      {
        lblTimeSaving.setVisible(false);
        lblTimeSavingValue.setVisible(false);
      }

      if (sleepData.getWakeIntervalStart() != null)
      {
        long end = sleepData.getWakeIntervalStart().getTime()
            + SleepData.WAKE_INTERVAL;
        lblLatestValue.setText(String.format("%tR", end));
        lblLatest.setVisible(true);
        lblLatestValue.setVisible(true);
      }
      else
      {
        lblLatest.setVisible(false);
        lblLatestValue.setVisible(false);
      }
    }
    else
    {
      lblSleepStart.setVisible(false);
      lblSleepStartValue.setVisible(false);
      lblLatency.setVisible(false);
      lblLatencyValue.setVisible(false);
      lblDuration.setVisible(false);
      lblDurationValue.setVisible(false);
      lblLatest.setVisible(false);
      lblLatestValue.setVisible(false);
      lblTimeSaving.setVisible(false);
      lblTimeSavingValue.setVisible(false);
      lblWakeupTime.setVisible(false);
      lblWakeupTimeValue.setVisible(false);
    }

    if (sleepData.isPowerNap())
    {
      lblSleepStart.setText(BundleUtil.getMessage("label.powerNapStart"));
      lblTimeSaving.setVisible(false);
      lblTimeSavingValue.setVisible(false);
      lblMovementsAverage.setVisible(false);
      lblMovementsAverageValue.setVisible(false);
      lblLatency.setVisible(false);
      lblLatencyValue.setVisible(false);
    }

    lblMovementsCountValue.setText(String.format("%d",
        sleepData.calculateMovementCount()));
    lblMovementsAverageValue.setText(String.format("%.1f",
        sleepData.calculateMovementsPerHour()));


    if (sleepData.getComment() == null || sleepData.getComment().trim().length()
        == 0)
    {
      lblComment.setVisible(false);
      lblCommentValue.setVisible(false);
    }
    else
    {
      lblComment.setVisible(true);
      lblCommentValue.setVisible(true);
      lblCommentValue.setText(sleepData.getComment());
    }

    repaint();
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
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
    lblSleepStart = new javax.swing.JLabel();
    lblSleepStartValue = new javax.swing.JLabel();
    lblLatency = new javax.swing.JLabel();
    lblLatencyValue = new javax.swing.JLabel();
    lblWakeupTime = new javax.swing.JLabel();
    lblWakeupTimeValue = new javax.swing.JLabel();
    lblLatest = new javax.swing.JLabel();
    lblLatestValue = new javax.swing.JLabel();
    lblTimeSaving = new javax.swing.JLabel();
    lblTimeSavingValue = new javax.swing.JLabel();
    lblMovementsCount = new javax.swing.JLabel();
    lblMovementsCountValue = new javax.swing.JLabel();
    lblMovementsAverage = new javax.swing.JLabel();
    lblMovementsAverageValue = new javax.swing.JLabel();
    pnlComment = new javax.swing.JPanel();
    lblComment = new javax.swing.JLabel();
    lblCommentValue = new javax.swing.JLabel();
    lblIdValue = new javax.swing.JLabel();
    lblNameValue = new javax.swing.JLabel();
    pnlClose = new javax.swing.JPanel();
    btnClose = new javax.swing.JButton();

    pmniCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/page_white_copy.png"))); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/default"); // NOI18N
    pmniCopy.setText(bundle.getString("chart.popup.copy")); // NOI18N
    pmniCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pmniCopyActionPerformed(evt);
      }
    });
    chartPanelPopupMenu.add(pmniCopy);

    pmniPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
    pmniPrint.setText(bundle.getString("chart.popup.print")); // NOI18N
    pmniPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pmniPrintActionPerformed(evt);
      }
    });
    chartPanelPopupMenu.add(pmniPrint);

    pmniSaveAsPNG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/save.png"))); // NOI18N
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

    lblDuration.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblDuration.setForeground(new java.awt.Color(100, 100, 100));
    lblDuration.setText(bundle.getString("label.sleepDuration")); // NOI18N
    pnlStats.add(lblDuration);

    lblDurationValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblDurationValue.setForeground(new java.awt.Color(100, 100, 100));
    lblDurationValue.setText("--:--:--");
    pnlStats.add(lblDurationValue);

    lblSleepStart.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblSleepStart.setForeground(new java.awt.Color(100, 100, 100));
    lblSleepStart.setText(bundle.getString("label.sleepStart")); // NOI18N
    pnlStats.add(lblSleepStart);

    lblSleepStartValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblSleepStartValue.setForeground(new java.awt.Color(100, 100, 100));
    lblSleepStartValue.setText("--:--");
    pnlStats.add(lblSleepStartValue);

    lblLatency.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblLatency.setForeground(new java.awt.Color(100, 100, 100));
    lblLatency.setText(bundle.getString("dataFrame.label.latency")); // NOI18N
    lblLatency.setToolTipText("");
    pnlStats.add(lblLatency);

    lblLatencyValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblLatencyValue.setForeground(new java.awt.Color(100, 100, 100));
    lblLatencyValue.setText("--:--");
    pnlStats.add(lblLatencyValue);

    lblWakeupTime.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblWakeupTime.setForeground(new java.awt.Color(100, 100, 100));
    lblWakeupTime.setText(bundle.getString("label.wakeupTime")); // NOI18N
    pnlStats.add(lblWakeupTime);

    lblWakeupTimeValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblWakeupTimeValue.setForeground(new java.awt.Color(100, 100, 100));
    lblWakeupTimeValue.setText("--:--");
    pnlStats.add(lblWakeupTimeValue);

    lblLatest.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLatest.setForeground(new java.awt.Color(100, 100, 100));
    lblLatest.setText(bundle.getString("label.latest")); // NOI18N
    pnlStats.add(lblLatest);

    lblLatestValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblLatestValue.setForeground(new java.awt.Color(100, 100, 100));
    lblLatestValue.setText("--:--");
    pnlStats.add(lblLatestValue);

    lblTimeSaving.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblTimeSaving.setForeground(new java.awt.Color(100, 100, 100));
    lblTimeSaving.setText(bundle.getString("label.timeSaving")); // NOI18N
    pnlStats.add(lblTimeSaving);

    lblTimeSavingValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblTimeSavingValue.setForeground(new java.awt.Color(100, 100, 100));
    lblTimeSavingValue.setText("--:--");
    pnlStats.add(lblTimeSavingValue);

    lblMovementsCount.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblMovementsCount.setForeground(new java.awt.Color(100, 100, 100));
    lblMovementsCount.setText(bundle.getString("dataFrame.label.movementsCount")); // NOI18N
    pnlStats.add(lblMovementsCount);

    lblMovementsCountValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblMovementsCountValue.setForeground(new java.awt.Color(100, 100, 100));
    lblMovementsCountValue.setText("-");
    pnlStats.add(lblMovementsCountValue);

    lblMovementsAverage.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblMovementsAverage.setForeground(new java.awt.Color(100, 100, 100));
    lblMovementsAverage.setText(bundle.getString("dataFrame.label.movementsAverage")); // NOI18N
    pnlStats.add(lblMovementsAverage);

    lblMovementsAverageValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblMovementsAverageValue.setForeground(new java.awt.Color(100, 100, 100));
    lblMovementsAverageValue.setText("-");
    pnlStats.add(lblMovementsAverageValue);

    pnlInfo.add(pnlStats, java.awt.BorderLayout.CENTER);

    pnlComment.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

    lblComment.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblComment.setForeground(new java.awt.Color(100, 100, 100));
    lblComment.setText(bundle.getString("dataframe.label.comment")); // NOI18N
    pnlComment.add(lblComment);

    lblCommentValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblCommentValue.setForeground(new java.awt.Color(100, 100, 100));
    pnlComment.add(lblCommentValue);

    lblIdValue.setVisible(false);
    lblIdValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblIdValue.setForeground(new java.awt.Color(100, 100, 100));
    lblIdValue.setText(bundle.getString("label.id")); // NOI18N
    pnlComment.add(lblIdValue);

    lblNameValue.setVisible(false);
    lblNameValue.setFont(new java.awt.Font("Lucida Grande", 0, 9));
    lblNameValue.setForeground(new java.awt.Color(100, 100, 100));
    lblNameValue.setText(bundle.getString("label.name")); // NOI18N
    pnlComment.add(lblNameValue);

    pnlInfo.add(pnlComment, java.awt.BorderLayout.SOUTH);

    add(pnlInfo, java.awt.BorderLayout.SOUTH);

    pnlClose.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

    btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cross.png"))); // NOI18N
    btnClose.setBorder(null);
    btnClose.setBorderPainted(false);
    btnClose.setFocusable(false);
    btnClose.setIconTextGap(2);
    btnClose.setMultiClickThreshhold(1000L);
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
    if (job.printDialog())
    {
      final Book book = new Book();
      job.setPageable(book);
      job.setJobName(getSleepData().getSleepDataFilename());
      ApplicationEventDispatcher.getInstance().dispatchEvent(new DiagramPrint(
          this, job, book));
      try
      {
        job.print();
      }
      catch (PrinterException ex)
      {
        final String msg = BundleUtil.getErrorMessage(
            "globalError.printingFailed");
        log.error(msg, ex);
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationMessageEvent(
            this, msg, true));
      }
    }
  }//GEN-LAST:event_pmniPrintActionPerformed

  private void pmniSaveAsPNGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmniSaveAsPNGActionPerformed
  {//GEN-HEADEREND:event_pmniSaveAsPNGActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramSaveAsPNG(
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
