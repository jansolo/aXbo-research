/*
 * $Id: SleepDataAdded.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;

/**
 * SleepDataAdded
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class SleepDataAdded extends ApplicationEvent {

  private final SleepData sleepData;

  public SleepDataAdded(final Object source, final SleepData sleepData)
  {
    super(source);
    this.sleepData = sleepData;
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }
}
