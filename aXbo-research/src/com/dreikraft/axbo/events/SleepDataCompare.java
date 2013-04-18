package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataCompare
 *
 * @author jan.illetschko@3kraft.com
 */
public final class SleepDataCompare extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public SleepDataCompare(final Object source) {
    super(source);
  }
}
