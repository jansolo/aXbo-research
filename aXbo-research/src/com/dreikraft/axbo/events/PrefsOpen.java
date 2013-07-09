package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import javax.swing.JFrame;

/**
 * PrefsOpen
 *
 * @author jan.illetschko@3kraft.com
 */
public final class PrefsOpen extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final JFrame parentFrame;

  public PrefsOpen(final Object source, final JFrame parentFrame) {
    super(source);
    this.parentFrame = parentFrame;
  }

  public JFrame getParentFrame() {
    return parentFrame;
  }
}
