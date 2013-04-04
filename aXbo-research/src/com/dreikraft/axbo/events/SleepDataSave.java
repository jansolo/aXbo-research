/*
 * $Id: SleepDataSave.java,v 1.1 2010-12-13 10:24:09 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;

/**
 * SleepDataSave
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class SleepDataSave extends ApplicationEvent {

  private SleepData sleepData;

  public SleepDataSave(Object source, SleepData sleepData)
  {
    super(source);
    this.sleepData = sleepData;
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }
}
