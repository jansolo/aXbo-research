/*
 * $Id: AxboFind.java,v 1.1 2010-11-29 15:42:23 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.SwingWorker;

/**
 * AxboConnect
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class AxboFind extends ApplicationEvent
{
  private final SwingWorker<?,?> followUpTask;

  public AxboFind(final Object source, final SwingWorker<?,?> followUpTask)
  {
    super(source);
    this.followUpTask = followUpTask;
  }

  public SwingWorker<?,?> getFollowUpTask()
  {
    return followUpTask;
  }
}
