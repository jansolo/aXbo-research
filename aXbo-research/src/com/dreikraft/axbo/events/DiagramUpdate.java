/*
 * $Id: DiagramUpdate.java,v 1.1 2010-12-03 18:10:02 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagrammIUpdate
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class DiagramUpdate extends ApplicationEvent
{

  public DiagramUpdate(Object source)
  {
    super(source);
  }
}
