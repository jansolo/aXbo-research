/*
 * $Id: PrefsOpen.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.JFrame;

/**
 * AxboConnect
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class PrefsOpen extends ApplicationEvent
{
  private final JFrame parentFrame;

  public PrefsOpen(final Object source, final JFrame parentFrame)
  {
    super(source);
    this.parentFrame = parentFrame;
  }

  public JFrame getParentFrame()
  {
    return parentFrame;
  }
}
