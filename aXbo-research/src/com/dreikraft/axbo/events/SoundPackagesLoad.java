/*
 * $Id: SoundPackagesLoad.java,v 1.1 2010-11-30 16:14:32 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * LoadSoundPackages
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class SoundPackagesLoad extends ApplicationEvent
{
  public SoundPackagesLoad(Object source)
  {
    super(source);
  }
}
