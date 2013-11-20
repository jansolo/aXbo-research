/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ©2013 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.timeseries;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

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
    while (!(t = t.next()).equals(lastTimePeriod)) {

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
    }

    return result;
  }
  
  public static TimeSeries createIntervalAverage(final TimeSeries source,
      final int len) {

return null;
  }
    

  private static double getValue(final TimeSeries series, final RegularTimePeriod t) {
    return series.getDataItem(t) != null && series.getDataItem(t).getValue()
        != null ? series.getDataItem(t).getValue().doubleValue() : 0;
  }

}


