package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.AxboInfo;

/**
 * AxboStatusGot
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboStatusGot extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private AxboInfo infoData;

  public AxboStatusGot(final Object source, final AxboInfo infoData) {
    super(source);
    this.infoData = infoData;
  }

  public AxboInfo getInfoData() {
    return infoData;
  }
}
