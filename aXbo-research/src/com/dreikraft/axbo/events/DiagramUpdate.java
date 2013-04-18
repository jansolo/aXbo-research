package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagramUpdate
 *
 * @author jan.illetschko@3kraft.com
 */
public class DiagramUpdate extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public DiagramUpdate(final Object source) {
    super(source);
  }
}
