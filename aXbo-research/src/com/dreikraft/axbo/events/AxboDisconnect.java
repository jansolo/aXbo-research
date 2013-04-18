package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboDisconnect
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboDisconnect extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboDisconnect(final Object source) {
    super(source);
  }
}
