package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagramCopy
 *
 * @author jan.illetschko@3kraft.com
 */
public class DiagramCopy extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public DiagramCopy(Object source) {
    super(source);
  }
}
