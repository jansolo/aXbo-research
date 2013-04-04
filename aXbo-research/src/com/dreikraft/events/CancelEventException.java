/*
 * $Id: CancelEventException.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.events;

/**
 * Thrown to cancel events
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class CancelEventException extends RuntimeException {

  /**
   *
   * @param cause
   */
  public CancelEventException(Throwable cause)
  {
    super(cause);
  }

  /**
   *
   * @param message
   * @param cause
   */
  public CancelEventException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   *
   * @param message
   */
  public CancelEventException(String message)
  {
    super(message);
  }

  /**
   *
   */
  public CancelEventException()
  {
    super();
  }
}
