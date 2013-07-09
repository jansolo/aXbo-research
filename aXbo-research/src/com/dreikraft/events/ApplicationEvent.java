package com.dreikraft.events;

import com.dreikraft.axbo.util.ReflectUtil;
import java.util.EventObject;

/**
 * Represents a high level application event. Does not depend on the GUI.
 *
 * @author jan.illetschko@3kraft.com
 */
public abstract class ApplicationEvent extends EventObject {

  /**
   * Creates a new ApplicationEvent.
   *
   * @param source
   */
  public ApplicationEvent(final Object source) {
    super(source);
  }

  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }
}
