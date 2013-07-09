package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import java.awt.print.Book;
import java.awt.print.PrinterJob;

/**
 * DiagramPrint
 *
 * @author jan.illetschko@3kraft.com
 */
public final class DiagramPrint extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private transient final PrinterJob printJob;
  private transient final Book book;

  public DiagramPrint(final Object source, final PrinterJob printJob,
      final Book book) {
    super(source);
    this.book = book;
    this.printJob = printJob;
  }

  public Book getBook() {
    return book;
  }

  public PrinterJob getPrintJob() {
    return printJob;
  }
}
