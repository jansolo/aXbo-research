package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataLoaded
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataLoaded extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final Integer count;

  public SleepDataLoaded(final Object source, final Integer count) {
    super(source);
    this.count = count;
  }

  public Integer getCount() {
    return count;
  }
}
