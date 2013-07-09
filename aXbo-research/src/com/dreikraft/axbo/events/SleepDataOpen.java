package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;
import java.util.List;

/**
 * SleepDataOpen
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataOpen extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final List<SleepData> sleepDataList;

  public SleepDataOpen(final Object source, final List<SleepData> sleepDataList) {
    super(source);
    this.sleepDataList = sleepDataList;
  }

  public List<SleepData> getSleepDataList() {
    return sleepDataList;
  }
}
