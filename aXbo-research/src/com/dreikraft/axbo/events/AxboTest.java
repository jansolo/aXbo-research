/*
 * $Id: AxboTest.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
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
public final class AxboTest extends ApplicationEvent
{
  private byte cmdType;

  public AxboTest(final Object source, final byte cmdType)
  {
    super(source);
    this.cmdType = cmdType;
  }

  public byte getCmdType()
  {
    return cmdType;
  }
}
