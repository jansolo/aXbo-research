/*
 * $Id: DiagramPrint.java,v 1.2 2010-12-13 09:56:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import java.awt.print.Book;
import java.awt.print.PrinterJob;

/**
 * DiagramPrint
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public final class DiagramPrint extends ApplicationEvent
{

  private final PrinterJob printJob;
  private final Book book;

  public DiagramPrint(final Object source, final PrinterJob printJob,
      final Book book)
  {
    super(source);
    this.book = book;
    this.printJob = printJob;
  }

  public Book getBook()
  {
    return book;
  }

  public PrinterJob getPrintJob()
  {
    return printJob;
  }
}
