package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagramStatsUpdate
 *
 * @author jan.illetschko@3kraft.com
 */
public class DiagramStatsUpdate extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public DiagramStatsUpdate(final Object source) {
    super(source);
  }
}
