/*
 * $Id: ApplicationExit.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;

/**
 * Application exits.
 * 
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ApplicationExit extends ApplicationEvent
{
  public ApplicationExit(Object source)
  {
    super(source);
  }
}
