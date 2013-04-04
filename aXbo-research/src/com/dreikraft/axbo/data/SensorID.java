/*
 * © 2008 3kraft
 * $Id: SensorID.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 */
package com.dreikraft.axbo.data;

/**
 * $Id: SensorID.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public enum SensorID
{
  P1("Person 1"), P2("Person 2");
  
  private String defaultName;
  
  private SensorID(String defaultName)
  {
    this.defaultName = defaultName;
  }
  
  public String getDefaultName()
  {
    return this.defaultName;
  }
}
