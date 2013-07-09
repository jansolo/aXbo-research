package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;

/**
 * SleepDataSave
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataSave extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final SleepData sleepData;

  public SleepDataSave(final Object source, final SleepData sleepData) {
    super(source);
    this.sleepData = sleepData;
  }

  public SleepData getSleepData() {
    return sleepData;
  }
}
