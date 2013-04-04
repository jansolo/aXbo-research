/*
 * $Id: ApplicationInitialize.java,v 1.1 2010-11-29 15:42:24 illetsch Exp $
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
public class ApplicationInitialize extends ApplicationEvent {

  public ApplicationInitialize(Object source)
  {
    super(source);
  }
}
