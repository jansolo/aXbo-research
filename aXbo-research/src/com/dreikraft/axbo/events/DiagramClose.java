package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.gui.DataFrame;

/**
 * DiagramClose
 *
 * @author jan.illetschko@3kraft.com
 */
public final class DiagramClose extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private transient final DataFrame dataView;

  public DiagramClose(final Object source,
      final DataFrame dataView) {
    super(source);
    this.dataView = dataView;
  }

  public DataFrame getDataView() {
    return dataView;
  }
}
