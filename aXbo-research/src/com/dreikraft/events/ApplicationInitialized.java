package com.dreikraft.events;

/**
 * The application including the GUI has initialized.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationInitialized extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public ApplicationInitialized(Object source) {
    super(source);
  }
}
