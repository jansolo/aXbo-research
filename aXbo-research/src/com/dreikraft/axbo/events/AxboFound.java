/*
 * $Id: AxboFound.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.SwingWorker;

/**
 * AxboStart
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class AxboFound extends ApplicationEvent
{

  private final String portName;
  private final SwingWorker<?,?> followUpTask;

  public AxboFound(final Object source, final String portName,
      final SwingWorker<?,?> followUpTask)
  {
    super(source);
    this.portName = portName;
    this.followUpTask = followUpTask;
  }

  public String getPortName()
  {
    return portName;
  }

  public SwingWorker<?,?> getFollowUpTask()
  {
    return followUpTask;
  }
}
