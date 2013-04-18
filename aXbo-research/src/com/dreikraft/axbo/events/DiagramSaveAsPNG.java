package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagramSaveAsPNG
 * 
 * @author jan.illetschko@3kraft.com
 */
public class DiagramSaveAsPNG extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public DiagramSaveAsPNG(final Object source)
  {
    super(source);
  }

}
