/*
 * $Id: DataSearch.java,v 1.1 2010-12-14 14:41:29 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import java.util.Date;

/**
 * DataSearch
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public final class DataSearch extends ApplicationEvent
{

  private final String name;
  private final Date from;
  private final Date to;

  public DataSearch(final Object source, final String name, final Date from,
      final Date to)
  {
    super(source);
    this.name = name;
    this.from = from;
    this.to = to;
  }

  public String getName()
  {
    return name;
  }

  public Date getFrom()
  {
    return from;
  }

  public Date getTo()
  {
    return to;
  }
}
