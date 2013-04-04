/*
 * $Id: AxboDisconnected.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboStart
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class AxboDisconnected extends ApplicationEvent
{
  private final boolean successful;

  public AxboDisconnected(final Object source, final boolean successful)
  {
    super(source);
    this.successful = successful;
  }

  public boolean isSuccessful()
  {
    return successful;
  }
}
