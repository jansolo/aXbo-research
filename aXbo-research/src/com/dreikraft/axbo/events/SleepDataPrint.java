package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;
import java.util.List;

/**
 * SleepDataPrint
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataPrint extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final List<SleepData> sleepDataList;

  public SleepDataPrint(final Object source, final List<SleepData> sleepDataList) {
    super(source);
    this.sleepDataList = sleepDataList;
  }

  public List<SleepData> getSleepDataList() {
    return sleepDataList;
  }
}
