package com.dreikraft.events;

/**
 * Loading data finished successfully.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ApplicationDataLoaded extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  public ApplicationDataLoaded(Object source) {
    super(source);
  }
}
