package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataImport
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataImport extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public SleepDataImport(final Object source) {
    super(source);
  }
}
