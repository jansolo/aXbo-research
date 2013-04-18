package com.dreikraft.events;

/**
 * The application including the GUI has initialized.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationInitialize extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public ApplicationInitialize(Object source) {
    super(source);
  }
}
