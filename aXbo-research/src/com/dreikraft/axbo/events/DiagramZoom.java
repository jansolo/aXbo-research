package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import org.jfree.data.Range;

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
  private final Range zoomRange;

  public DiagramZoom(final Object source, final String zoomStart,
      final int zoomDuration, final Range zoomRange) {
    super(source);
    this.zoomStart = zoomStart;
    this.zoomDuration = zoomDuration;
    this.zoomRange = zoomRange;
  }

  public String getZoomStart() {
    return zoomStart;
  }

  public int getZoomDuration() {
    return zoomDuration;
  }

  public Range getZoomRange() {
    return zoomRange;
  }
}
