/*
 * © 2008 3kraft
 * $Id: ProtocolHandler.java,v 1.6 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.data;


/**
 * $Id: ProtocolHandler.java,v 1.6 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.6 $
 */
public interface ProtocolHandler
{
  public void reset();
  public void parse(byte[] record);
}
