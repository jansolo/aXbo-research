package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboDisconnected
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboDisconnected extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final boolean successful;

  public AxboDisconnected(final Object source, final boolean successful) {
    super(source);
    this.successful = successful;
  }

  public boolean isSuccessful() {
    return successful;
  }
}
