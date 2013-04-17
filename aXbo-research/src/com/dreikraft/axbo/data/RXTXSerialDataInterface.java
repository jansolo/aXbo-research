package com.dreikraft.axbo.data;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.events.AxboConnected;
import com.dreikraft.axbo.events.AxboDisconnected;
import com.dreikraft.axbo.util.ByteUtil;
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
 * The RXTXSerialDataInterface handles serial interface communication via the
 * RXTX comm port implementation. The class is implemented as an enum singleton
 * (thread safe singleton).
 *
 * @author jan.illetschko@3kraft.com
 */
public enum RXTXSerialDataInterface implements DataInterface {

  /**
   * Singleton instance.
   */
  INSTANCE;
  /**
   * Logger.
   */
  public static final Log log =
      LogFactory.getLog(RXTXSerialDataInterface.class);
  /**
   * Serial port owner name.
   */
  public static final String PORT_OWNER = "axbo";

  /**
   * Serial interface data recipient thread monitor.
   */
  private class DataMonitor {

    private boolean dataReceived = false;

    /**
     * Retrieves Data received flag.
     *
     * @return true if data was received.
     */
    public boolean isDataReceived() {
      return dataReceived;
    }

    /**
     * Sets data received flag.
     *
     * @param dataReceived true if data was received.
     */
    public void setDataReceived(boolean dataReceived) {
      this.dataReceived = dataReceived;
    }
  }
  private SerialPort serialPort;
  private final DataMonitor dataMonitor = new DataMonitor();

  /**
   * Starts the serial interface if it has not been already started.
   *
   * @param portName the name of the serial interface.
   * @throws DataInterfaceException if aXbo can not be connected.
   */
  @Override
  public void start(final String portName)
      throws DataInterfaceException {

    CommPortIdentifier portId = getCommPortIdentifier(portName);
    // the port has been started already and can be reused
    if (isStarted(portName)) {
      return;
    }

    // configure a new conection
    try {
      if (log.isDebugEnabled()) {
        log.debug("lookup serial port: " + portName);
      }

      // get the default serial device data
      final DeviceType deviceType = DeviceContext.getDeviceType();
      // try to open the serial port, wait for x seconds
      serialPort = (SerialPort) portId.open(PORT_OWNER, deviceType.getTimeout());

      // set connection parameters
      serialPort.setSerialPortParams(deviceType.getBaudRate(),
          deviceType.getDataBits(), deviceType.getStopBits(),
          deviceType.getParity());
      serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

      // add event listener, enable events
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);

      // notify gui about successful connection
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboConnected(
          this, true));
    } catch (PortInUseException ex) {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    } catch (UnsupportedCommOperationException ex) {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    } catch (TooManyListenersException ex) {
      serialPort.close();
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
  }

  /**
   * Stops and releases the serial port.
   */
  @Override
  public void stop() {
    if (serialPort != null) {
      if (log.isDebugEnabled()) {
        log.debug("closing serial port " + serialPort.getName());
      }
      serialPort.close();

      // send gui notification
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboDisconnected(
          this, true));
    }
  }

  /**
   * Checks if the serial port with the given name has already been started.
   *
   * @param portName the name of the serial port
   * @return true, if the port has been started.
   * @throws DataInterfaceException if the serial port fails
   */
  @Override
  public boolean isStarted(final String portName)
      throws DataInterfaceException {
    CommPortIdentifier testedPortId = getCommPortIdentifier(portName);
    return testedPortId != null && testedPortId.isCurrentlyOwned()
        && PORT_OWNER.equals(testedPortId.getCurrentOwner());
  }

  /**
   * Writes data to a connected serial port. Waits for confirmation of receipt
   * from aXbo (which is processed in a different thread). If confirmation
   * misses within a configured time on the interface, it will try to rewrite
   * the data.
   *
   * @param portName the name of the serial port.
   * @param data the data bytes to write
   * @param retries number of retries, if the writing fails.
   *
   * @throws DataInterfaceException if data cannot be written successfully onto
   * the serial interface or no data recipient confirmation was received from
   * aXbo
   */
  @Override
  public void writeData(final String portName, final byte[] data, int retries)
      throws DataInterfaceException {
    if (log.isDebugEnabled()) {
      log.debug("write data: " + ByteUtil.dumpByteArray(data));
    }

    // try to start serial port always
    start(portName);
    try {
      // serialize data writes 
      synchronized (dataMonitor) {
        int retryCount = 0;
        // reset the data monitor
        dataMonitor.setDataReceived(false);
        // try to write the data onto the connected interface
        while (!dataMonitor.isDataReceived() && retryCount < retries) {
          serialPort.setOutputBufferSize(data.length);
          serialPort.getOutputStream().write(data, 0, data.length);
          // wait for confirmation from aXbo in different thread
          dataMonitor.wait(DeviceContext.getDeviceType().getTimeout());
          retryCount++;
        }
      }
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
    }

    // no confirmation has been received
    if (!dataMonitor.isDataReceived()) {
      throw new DataInterfaceException("no data");
    }
  }

  /**
   * Process incoming data from aXbo asynchronous (thread).
   *
   * @param e a data event from the serial interface.
   */
  @Override
  public void serialEvent(SerialPortEvent e) {

    if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      // new data is available
      int newDataLength;
      try {
        // read data from serial port into a byte array buffer
        while ((newDataLength = serialPort.getInputStream().available()) > 0) {
          byte[] data = new byte[newDataLength];
          serialPort.getInputStream().read(data, 0, newDataLength);
          // send byte array to singleton parser for further processing
          DeviceContext.getDeviceType().getProtocolHandler().parse(data);
        }
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  /**
   * Retrieve available COM ports from operating system.
   *
   * @return the List of COM ports.
   */
  @Override
  public List<String> getCommPortNames() {

    final List<String> commPortNames = new ArrayList<String>();
    if (Axbo.MAC_OS_X) {
      // on mac os there is only this device
      commPortNames.add("/dev/tty.SLAB_USBtoUART");
    } else {
      // on windows and linux search available comm ports
      for (Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
          e.hasMoreElements();) {
        CommPortIdentifier port = (CommPortIdentifier) e.nextElement();
        if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
          commPortNames.add(port.getName());
        }
      }
    }
    return commPortNames;
  }

  private CommPortIdentifier getCommPortIdentifier(String portName)
      throws DataInterfaceException {
    try {
      return CommPortIdentifier.getPortIdentifier(portName);
    } catch (NoSuchPortException ex) {
      throw new DataInterfaceException(ex.getMessage(), ex);
    }
  }

  /**
   * Notifies that the interface received data and is ready to write new data.
   */
  @Override
  public void dataReceived() {
    try {
      synchronized (dataMonitor) {
        if (!dataMonitor.isDataReceived()) {
          dataMonitor.setDataReceived(true);
          // notify waiting writer thread
          dataMonitor.notifyAll();
        }
      }
    } catch (Throwable t) {
      log.error(t.getMessage(), t);
    }
  }
}
