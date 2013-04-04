/*
 * $Id: AxboStatusGot.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.AxboInfo;

/**
 * AxboStart
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class AxboStatusGot extends ApplicationEvent
{
  private AxboInfo infoData;

  public AxboStatusGot(final Object source, final AxboInfo infoData)
  {
    super(source);
    this.infoData = infoData;
  }

  public AxboInfo getInfoData()
  {
    return infoData;
  }
}
