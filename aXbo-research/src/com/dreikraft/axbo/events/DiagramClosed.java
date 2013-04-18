package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.controller.DataFrameController;

/**
 * DiagramClosed
 *
 * @author jan.illetschko@3kraft.com
 */
public class DiagramClosed extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private transient final DataFrameController dataViewController;

  public DiagramClosed(final Object source,
      final DataFrameController dataViewController) {
    super(source);
    this.dataViewController = dataViewController;
  }

  public DataFrameController getDataViewController() {
    return dataViewController;
  }
}
