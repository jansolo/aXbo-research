package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboTimeSet
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboTimeSet extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboTimeSet(final Object source) {
    super(source);
  }
}
