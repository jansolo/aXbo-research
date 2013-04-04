// $Id: CryptoException.java,v 1.1 2008-05-13 15:11:09 illetsch Exp $
package com.dreikraft.axbo.crypto;

/**
 *
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class CryptoException extends Exception
{
  public CryptoException()
  {
    super();
  }

  public CryptoException(Throwable cause)
  {
    super(cause);
  }

  public CryptoException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public CryptoException(String message)
  {
    super(message);
  }
}
