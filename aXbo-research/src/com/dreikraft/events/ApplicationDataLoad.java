/*
 * $Id: ApplicationDataLoad.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;

/**
 * Initial data starts to be loaded.
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ApplicationDataLoad extends ApplicationEvent
{
  /**
   * @param source
   * @param data empty
   */
  public ApplicationDataLoad(Object source)
  {
    super(source);
  }
}
