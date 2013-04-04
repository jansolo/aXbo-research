/*
 * $Id: ApplicationMessageEvent.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;

/**
 * Fired when exception occurs. Can be used to pass exception messages to the
 * view.
 * 
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ApplicationMessageEvent extends ApplicationEvent
{
  private String message;
  private boolean error;

  /**
   * Creates a new ApplicationError EventObject
   * @param source the source object the generates the event
   * @param message the error message as localized string
   */
  public ApplicationMessageEvent(Object source, String message, boolean error)
  {
    super(source);
    this.message = message;
    this.error = error;
  }

  public String getMessage()
  {
    return message;
  }

  public Boolean isError()
  {
    return error;
  }
}
