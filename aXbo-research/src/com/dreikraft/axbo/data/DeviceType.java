/*
 * © 2008 3kraft
 * $Id: DeviceType.java,v 1.5 2010-12-16 23:13:53 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import com.dreikraft.axbo.util.ReflectUtil;
import gnu.io.SerialPort;

/**
 * $Id: DeviceType.java,v 1.5 2010-12-16 23:13:53 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.5 $
 */
public enum DeviceType
{
  AXBO(AxboDataParser.INSTANCE, RXTXSerialDataInterface.INSTANCE,
  115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, 500);

  private final DataInterface dataInterface;
  private final ProtocolHandler protocolHandler;
  private final int baudRate;
  private final int dataBits;
  private final int stopBits;
  private final int parity;
  private final int timeout;

  private DeviceType(final ProtocolHandler protocolHandler,
      final DataInterface dataInterface, int baudRate, int dataBits,
      int stopBits, int parity, int timeout)
  {
    this.protocolHandler = protocolHandler;
    this.dataInterface = dataInterface;
    this.baudRate = baudRate;
    this.dataBits = dataBits;
    this.stopBits = stopBits;
    this.parity = parity;
    this.timeout = timeout;
  }

  public DataInterface getDataInterface()
  {
    return dataInterface;
  }

  public ProtocolHandler getProtocolHandler()
  {
    return protocolHandler;
  }

  public int getBaudRate()
  {
    return baudRate;
  }

  public int getDataBits()
  {
    return dataBits;
  }

  public int getStopBits()
  {
    return stopBits;
  }

  public int getParity()
  {
    return parity;
  }

  public int getTimeout()
  {
    return timeout;
  }

  public String toLongString()
  {
    return super.toString() + ", " + ReflectUtil.toString(this);
  }
}
