package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboConnected
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboConnected extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final boolean successful;

  public AxboConnected(final Object source, final boolean successful) {
    super(source);
    this.successful = successful;
  }

  public boolean isSuccessful() {
    return successful;
  }
}
