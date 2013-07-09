package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.AxboInfo;
import com.dreikraft.axbo.util.ReflectUtil;

/**
 * InfoEvent
 *
 * @author jan.illetschko@3kraft.com
 */
public class InfoEvent extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final AxboInfo data;

  /**
   * Creates a new instance of InfoEvent
   */
  public InfoEvent(final Object source, final AxboInfo data) {
    super(source);
    this.data = data;
  }

  public AxboInfo getData() {
    return data;
  }

  @Override
  public String toString() {
    return ReflectUtil.toString(this);
  }
}
