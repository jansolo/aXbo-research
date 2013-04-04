/*
 * © 2008 3kraft
 * $Id: AxboData.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import com.dreikraft.axbo.util.ReflectUtil;

/**
 * $Id: AxboData.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class AxboInfo
{
  private String serialNumber;
  private String hardwareVersion;
  private String softwareVersion;
  private String rtcCalibration;
  
  public AxboInfo()
  {
  }
  
  public AxboInfo(String serialNumber, String hardwareVersion,
      String softwareVersion, String rtcCalibration)
  {
    this.serialNumber = serialNumber;
    this.hardwareVersion = hardwareVersion;
    this.softwareVersion = softwareVersion;
    this.rtcCalibration = rtcCalibration;
  }
  
  public String getSerialNumber()
  {
    return serialNumber;
  }
  
  public void setSerialNumber(String serialNumber)
  {
    this.serialNumber = serialNumber;
  }
  
  public String getHardwareVersion()
  {
    return hardwareVersion;
  }
  
  public void setHardwareVersion(String hardwareVersion)
  {
    this.hardwareVersion = hardwareVersion;
  }
  
  public String getSoftwareVersion()
  {
    return softwareVersion;
  }
  
  public void setSoftwareVersion(String softwareVersion)
  {
    this.softwareVersion = softwareVersion;
  }
  
  public String getRtcCalibration()
  {
    return rtcCalibration;
  }
  
  public void setRtcCalibration(String rtcCalibration)
  {
    this.rtcCalibration = rtcCalibration;
  }
  
  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }
}
