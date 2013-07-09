package com.dreikraft.events;

/**
 * Initial data starts to be loaded.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationDataLoad extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public ApplicationDataLoad(Object source) {
    super(source);
  }
}
