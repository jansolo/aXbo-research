/*
 * $Id: DiagramClose.java,v 1.1 2010-12-06 15:31:43 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.gui.DataFrame;

/**
 * DiagramClose
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class DiagramClose extends ApplicationEvent
{

  private final DataFrame dataView;

  public DiagramClose(final Object source,
      final DataFrame dataView)
  {
    super(source);
    this.dataView = dataView;
  }

  public DataFrame getDataView()
  {
    return dataView;
  }
}
