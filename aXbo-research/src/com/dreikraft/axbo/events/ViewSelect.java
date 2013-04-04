/*
 * $Id: ViewSelect.java,v 1.1 2010-11-30 16:14:33 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SelectView
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ViewSelect extends ApplicationEvent {

  private final String view;

  public ViewSelect(final Object source, final String view)
  {
    super(source);
    this.view = view;
  }

  public String getView()
  {
    return view;
  }
}
