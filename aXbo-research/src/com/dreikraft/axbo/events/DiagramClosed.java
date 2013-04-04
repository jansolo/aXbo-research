/*
 * $Id: DiagramClosed.java,v 1.1 2010-12-06 15:31:43 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.controller.DataFrameController;

/**
 * DiagramClosed
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class DiagramClosed extends ApplicationEvent
{

  private final DataFrameController dataViewController;

  public DiagramClosed(final Object source,
      final DataFrameController dataViewController)
  {
    super(source);
    this.dataViewController = dataViewController;
  }

  public DataFrameController getDataViewController()
  {
    return dataViewController;
  }
}
