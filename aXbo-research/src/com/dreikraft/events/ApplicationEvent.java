/*
 * $Id: ApplicationEvent.java,v 1.2 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.events;

import com.dreikraft.axbo.util.ReflectUtil;
import java.util.EventObject;

/**
 * Highlevel Events. Do not depend on the GUI.
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public abstract class ApplicationEvent extends EventObject {

  /**
   * Creates a new ApplicationEvent.
   *
   * @param source
   */
  public ApplicationEvent(final Object source) {
    super(source);
  }

  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }


}
