package com.dreikraft.events;

/**
 * Fired when exception occurs. Can be used to pass exception messages to the
 * view.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationMessageEvent extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private String message;
  private boolean error;

  /**
   * Creates a new ApplicationError EventObject
   *
   * @param source the source object the generates the event
   * @param message the error message as localized string
   */
  public ApplicationMessageEvent(Object source, String message, boolean error) {
    super(source);
    this.message = message;
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public Boolean isError() {
    return error;
  }
}
