package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboStatusGet
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboStatusGet extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboStatusGet(final Object source) {
    super(source);
  }
}
