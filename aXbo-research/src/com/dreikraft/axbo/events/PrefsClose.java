/*
 * $Id: PrefsClose.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboConnect
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class PrefsClose extends ApplicationEvent
{

  private final boolean save;

  public PrefsClose(final Object source, final boolean save)
  {
    super(source);
    this.save = save;
  }

  public boolean isSave()
  {
    return save;
  }
}
