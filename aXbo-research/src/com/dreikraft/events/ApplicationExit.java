package com.dreikraft.events;

/**
 * Application exits.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationExit extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public ApplicationExit(Object source) {
    super(source);
  }
}
