/*
 * $Id: ApplicationDataLoaded.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;

/**
 * Loading data finished successfully.
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ApplicationDataLoaded extends ApplicationEvent
{
  public ApplicationDataLoaded(Object source)
  {
    super(source);
  }
}
