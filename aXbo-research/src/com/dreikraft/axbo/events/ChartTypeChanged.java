/*
 * ©2014 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.events;

import com.dreikraft.axbo.model.ChartType;
import com.dreikraft.events.ApplicationEvent;

/**
 * Fires when the chart type has been changed in the preferences dialog.
 *
 * @author jan.illetschko@3kraft.com
 */
public final class ChartTypeChanged extends ApplicationEvent {

  private final ChartType chartType;

  /**
   * Creates ChartTypeChangedEvent.
   *
   * @param source the event source
   * @param chartType the selected chart type
   */
  public ChartTypeChanged(final Object source, final ChartType chartType) {
    super(source);
    this.chartType = chartType;
  }

  /**
   * Gets the selected chart type.
   *
   * @return the selected chart type
   */
  public ChartType getChartType() {
    return chartType;
  }
}
