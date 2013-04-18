package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboClear

 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboClear extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public AxboClear(final Object source) {
    super(source);
  }
}
