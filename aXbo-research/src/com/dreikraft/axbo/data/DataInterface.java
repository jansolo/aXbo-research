/*
 * © 2008 3kraft
 * $Id: DataInterface.java,v 1.14 2008-12-31 11:09:23 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import gnu.io.SerialPortEventListener;
import java.util.List;

/**
 * Encapsulates the serial communication to a arbitrary device.
 *
 * $Id: DataInterface.java,v 1.14 2008-12-31 11:09:23 illetsch Exp $
 *
 * @author  3Kraft - $Author: illetsch $
 * @version $Revision: 1.14 $
 */
public interface DataInterface extends SerialPortEventListener
{
  /**
   * start the serial device on a given portname. Valid portnames on the current
   * system can be retrived from {@getCommPortNames()@ }
   * @param portName the portname
   * @throws DataInterfaceException
   */
  public void start(String portName)
      throws DataInterfaceException;

  /**
   * stop the current serial port
   */
  public void stop();

  /**
   * checks if a serial communication is started on the current port.
   * @param portName 
   * @return true if the connection is established
   * @throws DataInterfaceException
   */
  public boolean isStarted(String portName)
      throws DataInterfaceException;

  /**
   * write a byte array on the serial port
   * @param portName 
   * @param data the byte encoded data
   * @param retries
   * @throws DataInterfaceException 
   */
  public void writeData(String portName, byte[] data,
      int retries)
      throws DataInterfaceException;

  /**
   * returns all comm port names currently available on the system.
   * @return all comm port names currently available on the system.
   */
  public List<String> getCommPortNames();

  public void dataReceived();
}
