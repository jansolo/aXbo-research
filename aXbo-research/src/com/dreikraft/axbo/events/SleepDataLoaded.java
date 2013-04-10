/*
 * $Id: SleepDataLoaded.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SleepDataLoadedEvent
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class SleepDataLoaded extends ApplicationEvent
{
 private Integer count;

  public SleepDataLoaded(final Object source, final Integer count)
  {
    super(source);
    this.count = count;
  }

  public Integer getCount()
  {
    return count;
  }
}
