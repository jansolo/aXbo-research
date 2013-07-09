package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.SwingWorker;

/**
 * AxboFind
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboFind extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  transient private final SwingWorker<?, ?> followUpTask;

  public AxboFind(final Object source, final SwingWorker<?, ?> followUpTask) {
    super(source);
    this.followUpTask = followUpTask;
  }

  public SwingWorker<?, ?> getFollowUpTask() {
    return followUpTask;
  }
}
