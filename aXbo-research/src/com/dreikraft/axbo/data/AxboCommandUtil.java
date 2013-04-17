package com.dreikraft.axbo.data;

import com.dreikraft.axbo.sound.Sound;
import com.dreikraft.axbo.sound.SoundPackage;
import com.dreikraft.axbo.util.ByteUtil;
import com.dreikraft.axbo.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility functions to create and execute aXbo serial interface commands. The
 * aXbo protocol definition can be found here:
 * https://docs.google.com/document/d/1rQkp8XcMFh0PPenZfhWqaGXDfi2gdxutdeXtu-T6OTo/pub
 *
 * @author jan.illetschko@3kraft.com
 */
public class AxboCommandUtil {

  /**
   * The logger.
   */
  public static final Log log = LogFactory.getLog(AxboCommandUtil.class);
  /**
   * The default buffer size.
   */
  public static final int BUF_SIZE = 1024;
  /**
   * The size of an aXbo memory frame in bytes.
   */
  public static final int FRAME_SIZE = 66;
  /**
   * The size of an aXbo memory page in bytes.
   */
  public static final int PAGE_SIZE = 66 * 4;

  /**
   * Sleeps the current thread.
   *
   * @param milliSecs sleep time in msec.
   */
  public static void sleep(int milliSecs) {
    try {
      Thread.sleep(milliSecs);
    } catch (InterruptedException ex) {
      log.warn(ex.getMessage(), ex);
    }
  }

  /**
   * Creates the command to update the date on the aXbo.
   *
   * @return the command bytes.
   */
  public static byte[] getDateCmd() {
    Calendar cal = Calendar.getInstance();
    log.info("setting alarm clock date " + cal);

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xC8, // set date command
      (byte) (Math.floor((cal.get(Calendar.YEAR) - 2000) / 10)), // year 1. digit
      (byte) ((cal.get(Calendar.YEAR) - 2000) % 10), // year 2. digit
      (byte) (Math.floor((cal.get(Calendar.MONTH) + 1) / 10)), // month 1. digit
      (byte) ((cal.get(Calendar.MONTH) + 1) % 10), // month 2. digit
      (byte) (Math.floor(cal.get(Calendar.DAY_OF_MONTH) / 10)), // day 1.digit
      (byte) (cal.get(Calendar.DAY_OF_MONTH) % 10), // day 2.digit
      (byte) (Math.floor(cal.get(Calendar.HOUR_OF_DAY) / 10)), // hour 1.digit
      (byte) (cal.get(Calendar.HOUR_OF_DAY) % 10), // hour 2.digit
      (byte) (Math.floor(cal.get(Calendar.MINUTE) / 10)), // minute 1.digit
      (byte) (cal.get(Calendar.MINUTE) % 10), // minute 2.digit
      (byte) 0x00, (byte) 0x00, // second
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00 // protocal end + checksum
    };

    int checksum = ByteUtil.calcChecksum(cmd, 3, 16);
    cmd[cmd.length - 2] = (byte) (checksum >> 8 & 0x000000FF);
    cmd[cmd.length - 1] = (byte) (checksum & 0x000000FF);
    if (log.isDebugEnabled()) {
      log.debug("data: " + cmd);
    }

    return escapeDLE(cmd);
  }

  /**
   * Executes a date/time update on aXbo
   *
   * @param portName the name of the serial interface.
   * @throws DataInterfaceException the update failed
   */
  public static void runSetClockDate(final String portName) throws
      DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    // execute command
    getDataInterface().writeData(portName, AxboCommandUtil.getDateCmd(), 1);
  }

  /**
   * Creates the command to retrieve all movement data from aXbo.
   *
   * @return
   */
  public static byte[] getLogDataCmd() {
    log.info("retrieving log data");

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCD, // get log command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCE // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Executes the command to retrieve movement data from aXbo
   *
   * @param portName the name of the serial interface
   * @throws DataInterfaceException if movement data retrieval fails.
   */
  public static void runLogDataCmd(final String portName) throws
      DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getLogDataCmd(), 1);
  }

  /**
   * Creates a command to clear the movement data memory on aXbo.
   *
   * @return the command bytes
   */
  public static byte[] getClearLogDataCmd() {
    log.info("clear log data");

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCE, // clear log command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCF // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Executes the command to clear the movement data on aXbo.
   *
   * @param portName the name of the serial interface.
   * @throws DataInterfaceException the command failed.
   */
  public static void runClearClockData(final String portName) throws
      DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getClearLogDataCmd(), 1);
  }

  /**
   * Creates a command to retrieve the version info from aXbo.
   *
   * @return the command bytes
   */
  public static byte[] getStatusCmd() {
    log.info("reading status");

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0x36, // get status command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x37 // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Executes the status command.
   *
   * @param portName the serial interface name.
   * @throws DataInterfaceException status retrieval failed.
   */
  public static void runReadStatus(final String portName)
      throws DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getStatusCmd(), 1);
  }

  /**
   * Creates a dummy command required for syncing the communication with aXbo.
   *
   * @param toggleBit toogle bit enabled/disabled
   * @return the command bytes
   */
  public static byte[] getDummyCmd(boolean toggleBit) {
    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, // protocol start
      (toggleBit ? (byte) 0x81 : (byte) 0x01), // toggle bit
      (byte) 0x39, // dummy command
      (byte) 0x10, (byte) 0x03, (byte) 0x00,
      (byte) (0x39 + (toggleBit ? 0x81 : 0x01)) // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Creates the aXbo check command.
   *
   * @return the command bytes.
   */
  public static byte[] getCheckCmd() {
    log.info("reading status");

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCB, // get check command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCC // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Executes the check command.
   *
   * @param portName the serial port name.
   * @throws DataInterfaceException chack command failed.
   */
  public static void runCheckCmd(final String portName)
      throws DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getCheckCmd(), 1);
  }

  /**
   * Create a aXbo test command.
   *
   * @param commandId the command id. Possible values are:
   * <ul>
   * <li>Test Mode: 0x00
   * <li>Start Alarm: 0x01
   * <li>RTC Kalibration: 0x02
   * <li>Clear Active: 0x04
   * <li>Software Reset: 0x08
   * </ul>
   * @return the test command bytes
   */
  public static byte[] getTestCmd(final byte commandId) {
    log.info("running test command");
    byte checksum = ((byte) 0xCB);
    checksum += commandId;
    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xC0, commandId, (byte) 0x0A, // get test command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, checksum // protocal end + checksum
    };
    return cmd;
  }

  /**
   * Executes the test command
   *
   * @param portName the serial port name.
   * @param commandId the command id. Possible values are:
   * <ul>
   * <li>Test Mode: 0x00
   * <li>Start Alarm: 0x01
   * <li>RTC Kalibration: 0x02
   * <li>Clear Active: 0x04
   * <li>Software Reset: 0x08
   * </ul>
   * @throws DataInterfaceException test command failed.
   */
  public static void runTestCmd(final String portName, final byte commandId)
      throws DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName, AxboCommandUtil.getTestCmd(
        commandId), 1);
  }

  /**
   * Creates anAxbo command to set the serial number of the aXbo.
   *
   * @param serialNumber the serial number (8 digits, [0-9]{8})
   * @return the command
   */
  public static byte[] getSetSerialNumberCmd(String serialNumber) {
    log.info("setting serial number: " + serialNumber);
    if (serialNumber.length() != 8) {
      throw new IllegalArgumentException("invalid length: " + serialNumber.
          length());
    }
    char[] digits = new char[8];
    int i = 0;
    for (char digit : serialNumber.toCharArray()) {
      digits[i] = (char) (digit < 48 ? digit + 48 : digit);
      //digits[i] = (char)(digit - 48);
      i++;
    }
    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xC5, // get set serial number command
      (byte) digits[0],
      (byte) digits[1],
      (byte) digits[2],
      (byte) digits[3],
      (byte) digits[4],
      (byte) digits[5],
      (byte) digits[6],
      (byte) digits[7],
      (byte) 0x10,
      (byte) 0x03,
      (byte) 0x00,
      (byte) 0x00 // protocal end + checksum
    // protocal end + checksum
    };
    int checksum = ByteUtil.calcChecksum(cmd, 3, 13);
    cmd[cmd.length - 2] = ByteUtil.highByte(checksum);
    cmd[cmd.length - 1] = ByteUtil.lowByte(checksum);

    return cmd;
  }

  /**
   * Executes the setting of the serial number.
   *
   * @param portName the serial port name.
   * @param serialNumber the serial number (8 digits, [0-9]{8})
   * @throws DataInterfaceException setting of the serial number failed.
   */
  public static void runSetSerialNumberCmd(final String portName,
      final String serialNumber)
      throws DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getSetSerialNumberCmd(serialNumber), 1);
  }

  /**
   * Creates the command to clear the serial number.
   *
   * @return the command.
   */
  public static byte[] getClearSerialNumberCmd() {
    log.info("clearing serial number");
    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xC5, // get set serial number command
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0xFF,
      (byte) 0x10,
      (byte) 0x03,
      (byte) 0x00,
      (byte) 0x00 // protocol end + checksum
    };
    int checksum = ByteUtil.calcChecksum(cmd, 3, 13);
    cmd[cmd.length - 2] = ByteUtil.highByte(checksum);
    cmd[cmd.length - 1] = ByteUtil.lowByte(checksum);

    return cmd;
  }

  /**
   * Clears the serial number of aXbo.
   *
   * @param portName the serial port name.
   * @throws DataInterfaceException clearing failed.
   */
  public static void runClearSerialNumberCmd(final String portName)
      throws DataInterfaceException {
    // sync communication first
    syncInterface(portName);
    // clear serial number
    getDataInterface().writeData(portName, AxboCommandUtil.getClearSerialNumberCmd(), 1);
  }

  /**
   * Synchronizes the data interface. The data interface to aXbo must be in
   * sync, before successful issuing of commands is possible.
   *
   * @param portName the name of the serial interface.
   */
  public static void syncInterface(final String portName) {
    // send dummy command with toggle bit off
    try {
      getDataInterface().writeData(portName, AxboCommandUtil.getDummyCmd(false),
          1);
    } catch (DataInterfaceException ex) {
      if (log.isDebugEnabled())
        log.debug(ex.getMessage(), ex);
    }
    // send dummy command with toogle bit on
    try {
      getDataInterface().writeData(portName, AxboCommandUtil.getDummyCmd(true),
          1);
    } catch (DataInterfaceException ex) {
      if (log.isDebugEnabled())
        log.debug(ex.getMessage(), ex);
    }
  }

  /**
   * Clears the data header memory segment in aXbo flash memory.
   *
   * @param portName the name of the serial interface.
   * @throws DataInterfaceException the clearing failed.
   */
  public static void clearHeader(final String portName)
      throws DataInterfaceException {
    int bufferPos = 0;
    boolean toggleBit = false;
    for (int i = 0; i < 16; i++) {
      final byte[] protocol = {
        (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) (toggleBit ? 0x81 : 0x01), // protocol start
        (byte) 0xBC, // write buffer
        (byte) 0x00, // buffer pos high byte
        (byte) bufferPos,
        (byte) 0x10, // data len with stuffing
        (byte) 0x22, // header start
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00, // empty
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // name placeholder
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // name placeholder
        (byte) 0x10, // protocol end
        (byte) 0x03,
        (byte) 0x00, // checksum
        (byte) 0x00 // checksum
      };

      // checksum
      int checksum = ByteUtil.calcChecksum(protocol, 3, 24); // substract stuffing
      protocol[protocol.length - 2] = ByteUtil.highByte(checksum);
      protocol[protocol.length - 1] = ByteUtil.lowByte(checksum);

      // write data to buffer
      getDataInterface().writeData(portName, escapeDLE(protocol), 3);
      toggleBit = !toggleBit;
      bufferPos += 16;
    }
    // synchronize toggle bit
    getDataInterface().writeData(portName, getDummyCmd(true), 3);

    // write buffer to first page
    writeBufferToPage(portName, 0);
  }

  /**
   * Writes a new sound data header to aXbo flash memory. It contains the
   * pointers to the memory area of each sound data. The header is in the first
   * page of the flash memory.
   *
   * @param portName the name of the serial interface
   * @param soundPackage the currently uploaded sound package
   * @throws DataInterfaceException if writing of the header fails
   */
  public static void writeHeader(final String portName,
      final SoundPackage soundPackage)
      throws DataInterfaceException {

    int bufferPos = 16;
    boolean toggleBit = false;
    for (final Sound sound : soundPackage.getSounds()) {
      int endPage = sound.getStartPage() + sound.getPageCount() - 1;
      final byte[] protocol = {
        (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) (toggleBit ? 0x81 : 0x01), // protocol start
        (byte) 0xBC, // write buffer
        (byte) 0x00, // buffer pos high byte
        (byte) bufferPos,
        (byte) 0x10, // data len with stuffing
        (byte) 0x22, // header start
        ByteUtil.lowByte(sound.getStartPage()),
        ByteUtil.highByte(sound.getStartPage()),
        ByteUtil.lowByte(endPage),
        ByteUtil.highByte(endPage),
        (byte) 0x00, // empty
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // name placeholder
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // name placeholder
        (byte) 0x10, // protocol end
        (byte) 0x03,
        (byte) 0x00, // checksum
        (byte) 0x00 // checksum
      };

      // write name
      char[] nameChars = StringUtil.rpad(sound.getName(), ' ', 10).toCharArray();
      for (int i = 0; i < nameChars.length; i++) {
        protocol[i + 14] = (byte) nameChars[i];
      }
      // checksum
      int checksum = ByteUtil.calcChecksum(protocol, 3, 24); // substract stuffing
      protocol[protocol.length - 2] = ByteUtil.highByte(checksum);
      protocol[protocol.length - 1] = ByteUtil.lowByte(checksum);

      // write data to buffer
      getDataInterface().writeData(portName, escapeDLE(protocol), 3);
      toggleBit = !toggleBit;
      bufferPos += 16;
    }
    // synchronize toggle bit
    getDataInterface().writeData(portName, getDummyCmd(true), 3);

    // write buffer to first page
    writeBufferToPage(portName, 0);
  }

  /**
   * Writes sound data to buffer memory.
   *
   * @param portName the serial port name.
   * @param soundData the sound date bytes.
   * @param page the memory page number.
   * @throws DataInterfaceException writing of data failed.
   */
  public static void writePage(final String portName, final byte[] soundData,
      final int page) throws DataInterfaceException {
    //final byte highByte = bufferToggle ? (byte)0x00 : (byte)0x80;
    final byte highByte = (byte) 0x00;
    boolean protocolToggle = true;
    // write frames to page buffer
    for (int frame = 0; frame < PAGE_SIZE / FRAME_SIZE; frame++) {
      final int framePos = frame * FRAME_SIZE;
      // protocol len = prefix len + frame len + suffix len
      byte[] protocol = new byte[8 + FRAME_SIZE + 4];
      // prefix
      protocol[0] = (byte) 0x00;
      protocol[1] = (byte) 0x10;
      protocol[2] = (byte) 0x02;
      protocol[3] = protocolToggle ? (byte) 0x01 : (byte) 0x81;
      protocol[4] = (byte) 0xBC;
      protocol[5] = highByte;
      protocol[6] = (byte) (framePos);
      protocol[7] = (byte) FRAME_SIZE;
      // copy frame data to protocol
      System.arraycopy(soundData, (page * PAGE_SIZE) + framePos, protocol, 8,
          FRAME_SIZE);
      // suffix
      protocol[FRAME_SIZE + 8] = (byte) 0x10;
      protocol[FRAME_SIZE + 9] = (byte) 0x03;
      // checksum
      int checksum = ByteUtil.calcChecksum(protocol, 3, 8 + FRAME_SIZE);
      protocol[FRAME_SIZE + 10] = ByteUtil.highByte(checksum);
      protocol[FRAME_SIZE + 11] = ByteUtil.lowByte(checksum);

      // write frame to buffer
      getDataInterface().writeData(portName, escapeDLE(protocol), 3);
      // toggle protocol bit
      protocolToggle = !protocolToggle;
    }
  }

  /**
   * Writing the buffer memory to flash memory page.
   *
   * @param portName the serial port name.
   * @param page the number of the memory page.
   * @throws DataInterfaceException writing failed
   */
  public static void writeBufferToPage(String portName, int page)
      throws DataInterfaceException {
    byte pageHighByte = ByteUtil.highByte(page);
    byte pageLowByte = ByteUtil.lowByte(page);

    byte[] cmd = {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xBD, // get set serial number command
      pageHighByte, pageLowByte, // page address
      (byte) 0x10, // protocol end
      (byte) 0x03,
      (byte) 0x00, // checksum
      (byte) 0x00 // checksum
    };
    final int checksum = ByteUtil.calcChecksum(cmd, 3, 7);
    cmd[cmd.length - 2] = ByteUtil.highByte(checksum);
    cmd[cmd.length - 1] = ByteUtil.lowByte(checksum);

    getDataInterface().writeData(portName, escapeDLE(cmd), 3);

    // synchronize toggle bit
    getDataInterface().writeData(portName, getDummyCmd(true), 3);
  }

  /**
   * Escapes data link escape characters in the byte array.
   *
   * @param data the data array
   * @return the data array with escaped DLEs
   */
  public static byte[] escapeDLE(byte[] data) {
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    @SuppressWarnings("UnusedAssignment")
    int b = 0;
    int pos = 0;
    while ((b = in.read()) != -1) {
      if (b == 0x10 && pos > 4 && pos < data.length - 4) {
        out.write(b);
      }
      out.write(b);
      pos++;
    }
    return out.toByteArray();
  }

  /**
   * Retrieve the data interface.
   *
   * @return the data interface of the current device type.
   */
  private static DataInterface getDataInterface() {
    return DeviceContext.getDeviceType().getDataInterface();
  }
}
