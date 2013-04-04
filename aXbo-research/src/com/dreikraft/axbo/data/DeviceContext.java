/*
 * $Id$
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.data;

/**
 * DeviceContext
 *
 * @author jan_solo
 * @author $Author$
 * @version $Revision$
 */
public enum DeviceContext {

  INSTANCE;

  private DeviceType deviceType;

  public static DeviceType getDeviceType()
  {
    return INSTANCE.deviceType;
  }

  public static void setDeviceType(DeviceType deviceType)
  {
    INSTANCE.deviceType = deviceType;
  }
}
