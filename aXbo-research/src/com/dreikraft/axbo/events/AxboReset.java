package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboReset
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboReset extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboReset(final Object source) {
    super(source);
  }
}
