package com.dreikraft.axbo.data;

import com.dreikraft.axbo.util.ReflectUtil;
import gnu.io.SerialPort;

/**
 * The DeviceType enum defines the connection parameters of an interface type.
 * 
 * @author jan.illetschko@3kraft.com
 */
public enum DeviceType
{
  /**
   * The aXbo interface connection parameters.
   */
  AXBO(AxboDataParser.INSTANCE, RXTXSerialDataInterface.INSTANCE,
  115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, 125);

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

  /**
   * Gets the data interface implementation.
   * @return the data interface
   */
  public DataInterface getDataInterface()
  {
    return dataInterface;
  }

  /**
   * Gets the data parser implementation.
   * @return the data parser
   */
  public ProtocolHandler getProtocolHandler()
  {
    return protocolHandler;
  }

  /**
   * Gets the baud rate configuration.
   * @return the configured baud rate
   */
  public int getBaudRate()
  {
    return baudRate;
  }

  /**
   * Gets the data bits configuration.
   * @return the configured data bits
   */
  public int getDataBits()
  {
    return dataBits;
  }

  /**
   * Gets the Stop bits configuration.
   * @return the configured stop bits 
   */
  public int getStopBits()
  {
    return stopBits;
  }

  /**
   * Gets the parity configuration.
   * @return the configured parity
   */
  public int getParity()
  {
    return parity;
  }

  /**
   * Gets the connection timeout.
   * @return the timeout
   */
  public int getTimeout()
  {
    return timeout;
  }

  /**
   * Return a long string representation.
   * @return a long description
   */
  public String toLongString()
  {
    return super.toString() + ", " + ReflectUtil.toString(this);
  }
}
