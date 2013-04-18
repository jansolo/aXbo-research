package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * PrefsClose
 *
 * @author jan.illetschko@3kraft.com
 */
public final class PrefsClose extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final boolean save;

  public PrefsClose(final Object source, final boolean save) {
    super(source);
    this.save = save;
  }

  public boolean isSave() {
    return save;
  }
}
