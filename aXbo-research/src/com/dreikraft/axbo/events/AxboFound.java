package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.SwingWorker;

/**
 * AxboFound
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboFound extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final String portName;
  transient private final SwingWorker<?, ?> followUpTask;

  public AxboFound(final Object source, final String portName,
      final SwingWorker<?, ?> followUpTask) {
    super(source);
    this.portName = portName;
    this.followUpTask = followUpTask;
  }

  public String getPortName() {
    return portName;
  }

  public SwingWorker<?, ?> getFollowUpTask() {
    return followUpTask;
  }
}
