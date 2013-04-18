package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboConnect
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboConnect extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboConnect(final Object source) {
    super(source);
  }
}
