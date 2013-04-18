package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import java.util.Date;

/**
 * DataSearch
 * @author jan.illetschko@3kraft.com
 */
public final class DataSearch extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final String name;
  private final Date from;
  private final Date to;

  public DataSearch(final Object source, final String name, final Date from,
      final Date to) {
    super(source);
    this.name = name;
    this.from = from != null ? new Date(from.getTime()) : null;
    this.to = to != null ? new Date(to.getTime()) : null;
  }

  public String getName() {
    return name;
  }

  public Date getFrom() {
    return from != null ? new Date(from.getTime()) : null;
  }

  public Date getTo() {
    return to != null ? new Date(to.getTime()) : null;
  }
}
