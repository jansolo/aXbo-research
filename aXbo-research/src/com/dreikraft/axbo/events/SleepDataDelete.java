package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;

/**
 * SleepDataDelete
 *
 * @author jan.illetschko@3kraft.com
 */
public final class SleepDataDelete extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final SleepData sleepData;

  public SleepDataDelete(final Object source, final SleepData sleepData) {
    super(source);
    this.sleepData = sleepData;
  }

  public SleepData getSleepData() {
    return sleepData;
  }
}
