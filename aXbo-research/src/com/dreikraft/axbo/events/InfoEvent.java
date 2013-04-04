/*
 * © 2008 3kraft
 * $Id: InfoEvent.java,v 1.5 2010-11-29 15:42:23 illetsch Exp $
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.AxboInfo;
import com.dreikraft.axbo.util.ReflectUtil;

/**
 * $Id: InfoEvent.java,v 1.5 2010-11-29 15:42:23 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.5 $
 */
public class InfoEvent extends ApplicationEvent
{
  private AxboInfo data;
  
  /** Creates a new instance of InfoEvent */
  public InfoEvent(Object source, AxboInfo data)
  {
    super(source);
    this.data = data;
  }

  public AxboInfo getData()
  {
    return data;
  }
  
  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }
}
