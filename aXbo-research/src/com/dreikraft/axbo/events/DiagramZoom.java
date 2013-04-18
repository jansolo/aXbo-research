package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * DiagramZoom
 *
 * @author jan.illetschko@3kraft.com
 */
public final class DiagramZoom extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final String zoomStart;
  private final int zoomDuration;

  public DiagramZoom(final Object source, final String zoomStart,
      final int zoomDuration) {
    super(source);
    this.zoomStart = zoomStart;
    this.zoomDuration = zoomDuration;
  }

  public String getZoomStart() {
    return zoomStart;
  }

  public int getZoomDuration() {
    return zoomDuration;
  }
}
