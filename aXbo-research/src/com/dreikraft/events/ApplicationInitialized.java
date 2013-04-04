/*
 * $Id: ApplicationInitialized.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;


/**
 * The application inkluding the GUI has initialized.
 * 
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ApplicationInitialized extends ApplicationEvent {

  public ApplicationInitialized(Object source)
  {
    super(source);
  }
}
