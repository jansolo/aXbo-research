package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * AxboTest
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboTest extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private byte cmdType;

  public AxboTest(final Object source, final byte cmdType) {
    super(source);
    this.cmdType = cmdType;
  }

  public byte getCmdType() {
    return cmdType;
  }
}
