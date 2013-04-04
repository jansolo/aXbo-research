/*
 * $Id: DiagramZoom.java,v 1.1 2010-12-03 18:10:02 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * ZoomEvent
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class DiagramZoom extends ApplicationEvent
{
  private final String zoomStart;
  private final int zoomDuration;

  public DiagramZoom(final Object source, final String zoomStart,
      final int zoomDuration)
  {
    super(source);
    this.zoomStart = zoomStart;
    this.zoomDuration = zoomDuration;
  }

  public String getZoomStart()
  {
    return zoomStart;
  }

  public int getZoomDuration()
  {
    return zoomDuration;
  }
}
