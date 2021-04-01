/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ©2013 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.timeseries;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYZDataset;

/**
 *
 * @author jan.illetschko@3kraft.com
 */
public class TimeSeriesUtil {

  public static final Log log = LogFactory.getLog(TimeSeriesUtil.class);

  /**
   * Prevent instantiation.
   */
  private TimeSeriesUtil() {
  }

  /**
   * Create a moving average time series from a given source series.
   *
   * @param source the source timeseries
   * @param preLen number of timeperiods before current time period included in
   * moving average calculation
   * @param postLen number of timeperiods after current time period included in
   * moving average calculation
   * @return a moving average time series
   */
  public static TimeSeries createMovingAverage(final TimeSeries source,
      final int preLen, final int postLen) {

    final int len = preLen + postLen + 1;
    final TimeSeries result = new TimeSeries(source.getKey());
    final RegularTimePeriod lastTimePeriod = source.getTimePeriod(
        source.getItemCount() - 1);

    // process all timeperiods including empty ones
    RegularTimePeriod t = source.getTimePeriod(0);
    while (!(t.getFirstMillisecond() > lastTimePeriod.getFirstMillisecond())) {

      // calculate the moving avg value for the current time period
      double value = getValue(source, t);
      RegularTimePeriod ti = t;
      for (int i = 0; i < preLen; i++) {
        ti = ti.previous();
        value += getValue(source, ti);
      }
      ti = t;
      for (int i = 0; i < postLen; i++) {
        ti = ti.next();
        value += getValue(source, ti);
      }

      // add the moving avg value to the included time periods
      result.addOrUpdate(t, value / len);
      t = t.next();
    }

    return result;
  }

  /**
   * Create a XYZ dataset from a time series with Y.
   *
   * @param source
   * @return
   */
  public static final XYZDataset createXYZTimeSeries(final TimeSeries source) {

    final RegularTimePeriod lastTimePeriod = source.getTimePeriod(
        source.getItemCount() - 1);
    // process all timeperiods including empty ones
    RegularTimePeriod t = source.getTimePeriod(0);
    final List<Double> zValuesList = new LinkedList<>();
    while (!(t.getFirstMillisecond() > lastTimePeriod.getFirstMillisecond())) {
      zValuesList.add(getValue(source, t));
      t = t.next();
    }
    final double[] xValues = new double[zValuesList.size()];
    final double[] yValues = new double[zValuesList.size()];
    final double[] zValues = new double[zValuesList.size()];
    t = source.getTimePeriod(0);
    for (int i = 0; i < zValuesList.size(); i++) {
      xValues[i] = t.getFirstMillisecond();
      yValues[i] = 0;
      zValues[i] = zValuesList.get(i);
      t = t.next();
    }
    final DefaultXYZDataset target = new DefaultXYZDataset();
    target.addSeries(0, new double[][]{xValues, yValues, zValues});

    return target;
  }

  /**
   * Create a XYIntervalDataset from sleep data.
   *
   * @param sleepData the sleep data
   * @param title the dataset title
   * @return a dataset
   */
  public static final IntervalXYDataset createDataset(final SleepData sleepData,
      final String title) {
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    final SleepDataTimeSeries sleepDataTimeSeries = new SleepDataTimeSeries(
        title, sleepData, Minute.class, Axbo.MAX_MOVEMENTS_DEFAULT);
    dataset.addSeries(sleepDataTimeSeries);
    return dataset;
  }

  /**
   * Create a KeyTimeSeries object from sleep data.
   *
   * @param sleepData the sleep data
   * @param title the dataset title
   * @return a time series
   */
  public static final KeyTimeSeries createKeyDataset(final SleepData sleepData,
      final String title) {
    final KeyTimeSeries keyTimeSeries = new KeyTimeSeries(title, sleepData,
        Second.class, MovementData.KEY);
    return keyTimeSeries;
  }

  /**
   * Create a KeyTimeSeries object from sleep data.
   *
   * @param sleepData the sleep data
   * @param title the dataset title
   * @return a time series
   */
  public static final KeyTimeSeries createSnoozeDataset(final SleepData sleepData,
      final String title) {
    final KeyTimeSeries keyTimeSeries = new KeyTimeSeries(title, sleepData,
        Second.class, MovementData.SNOOZE);
    return keyTimeSeries;
  }

  private static double getValue(final TimeSeries series,
      final RegularTimePeriod t) {
    return series.getDataItem(t) != null && series.getDataItem(t).getValue()
        != null ? series.getDataItem(t).getValue().doubleValue() : 0;
  }

}
