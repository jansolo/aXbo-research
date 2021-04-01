/*
 * © 2014 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.model;

import com.dreikraft.axbo.util.BundleUtil;

/**
 * An enumeration with the supported chart types.
 *
 * @author jan.illetschko@3kraft.com
 */
public enum ChartType {

  /**
   * A bar chart.
   */
  BAR,
  /**
   * A moving average chart.
   */
  MOVING_AVG,
  /**
   * A combined chart (of the ones above).
   */
  COMBINED;

  /**
   * Gets the localized name of the chart type for the current locale.
   *
   * @return the localized name of the chart constant
   */
  public String getLocalizedName() {
    return BundleUtil.getMessage(name());
  }
}
