/*
 * $Id: SleepDataOpen.java,v 1.1 2010-11-30 16:14:33 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;
import java.util.List;

/**
 * SleepDataOpen
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class SleepDataOpen extends ApplicationEvent
{

  private final List<SleepData> sleepDataList;

  public SleepDataOpen(final Object source, final List<SleepData> sleepDataList)
  {
    super(source);
    this.sleepDataList = sleepDataList;
  }

  public List<SleepData> getSleepDataList()
  {
    return sleepDataList;
  }
}
