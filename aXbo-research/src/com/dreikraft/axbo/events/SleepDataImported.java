package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataImported
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataImported extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final Integer newSleepDataCount;

  public SleepDataImported(final Object source, final Integer newSleepDataCount) {
    super(source);
    this.newSleepDataCount = newSleepDataCount;
  }

  public Integer getNewSleepDataCount() {
    return newSleepDataCount;
  }
}
