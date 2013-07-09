package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataLoad
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataLoad extends ApplicationEvent {
  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;


  public SleepDataLoad(final Object source)
  {
    super(source);
  }
}
