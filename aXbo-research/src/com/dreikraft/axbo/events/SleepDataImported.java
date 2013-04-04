/*
 * $Id: SleepDataImported.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.SleepData;
import java.util.List;

/**
 * SleepDataImported
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class SleepDataImported extends ApplicationEvent{

  private final Integer newSleepDataCount;

  public SleepDataImported(final Object source, final Integer newSleepDataCount)
  {
    super(source);
    this.newSleepDataCount = newSleepDataCount;
  }

  public Integer getNewSleepDataCount()
  {
    return newSleepDataCount;
  }
}
