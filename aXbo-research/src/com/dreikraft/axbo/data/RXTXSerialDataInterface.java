/*
 * © 2008 3kraft
 * $Id: RXTXSerialDataInterface.java,v 1.21 2010-12-16 23:13:53 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.events.AxboConnected;
import com.dreikraft.axbo.events.AxboDisconnected;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import org.apache.commons.logging.*;

/**
 * $Id: RXTXSerialDataInterface.java,v 1.21 2010-12-16 23:13:53 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.21 $
 */
public enum RXTXSerialDataInterface implements DataInterface
{
  INSTANCE;

  public static final Log log =
      LogFactory.getLog(RXTXSerialDataInterface.class);
  public static final String PORT_OWNER = "axbo";

  private class DataMonitor
  {
    private boolean dataReceived = false;

    public boolean isDataReceived()
    {
      return dataReceived;
    }

    public void setDataReceived(boolean dataReceived)
    {
      this.dataReceived = dataReceived;
    }
  }

  private SerialPort serialPort;
  private ProtocolHandler protocolHandler;
  private final DataMonitor dataMonitor = new DataMonitor();

  @Override
  public void start(final String portName)
      throws DataInterfaceException
  {
    if (log.isDebugEnabled())
    {
      log.debug("lookup serial port: " + portName);
    }
    CommPortIdentifier portId = getCommPortIdentifier(portName);

    if (isStarted(portName))
    {
      if (log.isDebugEnabled())
      {
        log.debug("serial port " + portId.getName() + " is started by " +
            portId.getCurrentOwner());
      }
      return;
    }

    // set the serial protocol handler
    if (log.isDebugEnabled())
    {
      log.debug("protocol handler: " + protocolHandler);
    }

    try
    {
      final DeviceType deviceType = DeviceContext.getDeviceType();
      // open the serial port, wait for x seconds
      serialPort = (SerialPort) portId.open(PORT_OWNER, deviceType.getTimeout());

      // set connection parameters
      serialPort.setSerialPortParams(deviceType.getBaudRate(),
          deviceType.getDataBits(), deviceType.getStopBits(),
          deviceType.getParity());
      serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

      // add event listener, enable events
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);

      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboConnected(
          this, true));
    }
    catch (PortInUseException ex)
    {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
    catch (UnsupportedCommOperationException ex)
    {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
    catch (TooManyListenersException ex)
    {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
  }

  @Override
  public void stop()
  {
    if (serialPort != null)
    {
      if (log.isDebugEnabled())
      {
        log.debug("closing serial port " + serialPort.getName());
      }
      serialPort.close();
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboDisconnected(
          this, true));
    }
  }

  @Override
  public boolean isStarted(final String portName)
      throws DataInterfaceException
  {
    CommPortIdentifier testedPortId = getCommPortIdentifier(portName);
    return testedPortId != null && testedPortId.isCurrentlyOwned() &&
        PORT_OWNER.equals(testedPortId.getCurrentOwner());
  }

  @Override
  public void writeData(final String portName, final byte[] data, int retries)
      throws DataInterfaceException
  {
    dataMonitor.setDataReceived(false);
    start(portName);
    int retryCount = 0;
    try
    {
      synchronized (dataMonitor)
      {
        while (!dataMonitor.isDataReceived() && retryCount < retries)
        {
          serialPort.setOutputBufferSize(data.length);
          serialPort.getOutputStream().write(data, 0, data.length);
          dataMonitor.wait(DeviceContext.getDeviceType().getTimeout());
          retryCount++;
        }
      }
    }
    catch (InterruptedException ex)
    {
      log.error(ex.getMessage(), ex);
    }
    catch (IOException ex)
    {
      log.error(ex.getMessage(), ex);
    }
    if (!dataMonitor.isDataReceived())
    {
      throw new DataInterfaceException("no data");
    }
    dataMonitor.setDataReceived(false);
  }

  @Override
  public void serialEvent(SerialPortEvent e)
  {
    // Determine type of event.
    if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE)
    {
      int newDataLength = 0;
      try
      {
        while ((newDataLength = serialPort.getInputStream().available()) > 0)
        {
          byte[] data = new byte[newDataLength];
          serialPort.getInputStream().read(data, 0, newDataLength);
          DeviceContext.getDeviceType().getProtocolHandler().parse(data);
        }
      }
      catch (Exception ex)
      {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  @Override
  public List<String> getCommPortNames()
  {
    final List<String> commPortNames = new ArrayList<String>();
    if (Axbo.MAC_OS_X)
    {
      commPortNames.add("/dev/tty.SLAB_USBtoUART");
    }
    else
    {
      for (Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
          e.hasMoreElements();)
      {
        CommPortIdentifier port = (CommPortIdentifier) e.nextElement();
        if (port.getPortType() == CommPortIdentifier.PORT_SERIAL)
        {
          commPortNames.add(port.getName());
        }
      }
    }
    return commPortNames;
  }

  private CommPortIdentifier getCommPortIdentifier(String portName)
      throws DataInterfaceException
  {
    try
    {
      return CommPortIdentifier.getPortIdentifier(portName);
    }
    catch (NoSuchPortException ex)
    {
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
  }

  @Override
  public void dataReceived()
  {
    try
    {
      synchronized (dataMonitor)
      {
        dataMonitor.setDataReceived(true);
        dataMonitor.notifyAll();
      }
    }
    catch (Throwable t)
    {
      log.error(t.getMessage(), t);
    }
  }
}
