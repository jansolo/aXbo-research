/*
 * © 2008 3kraft
 * $Id: DataInterfaceException.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.data;

/**
 * $Id: DataInterfaceException.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class DataInterfaceException extends java.lang.Exception
{
  
  /**
   * Creates a new instance of <code>DataInterfaceException</code> without detail message.
   */
  public DataInterfaceException()
  {
  }
  
  
  /**
   * Constructs an instance of <code>DataInterfaceException</code> with the specified detail message.
   * @param msg the detail message.
   */
  public DataInterfaceException(String msg)
  {
    super(msg);
  }
  
  public DataInterfaceException(String msg, Throwable ex)
  {
    super(msg, ex);
  }
}
