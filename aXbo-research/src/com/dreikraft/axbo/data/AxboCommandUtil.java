/*
 * © 2008 3kraft
 * $Id: AxboCommandUtil.java,v 1.18 2009-07-20 11:26:15 illetsch Exp $
 */
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
 * $Id: AxboCommandUtil.java,v 1.18 2009-07-20 11:26:15 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.18 $
 */
 public class AxboCommandUtil
{

  public static final Log log = LogFactory.getLog(AxboCommandUtil.class);
  public static final int BUF_SIZE = 1024;
  public static final int FRAME_SIZE = 66;
  public static final int PAGE_SIZE = 66 * 4;

  public static void sleep(int milliSecs)
  {
    try
    {
      Thread.sleep(milliSecs);
    }
    catch (InterruptedException ex)
    {
      log.warn(ex.getMessage(), ex);
    }
  }

  public static byte[] getDateCmd()
  {
    Calendar cal = Calendar.getInstance();
    log.info("setting alarm clock date " + cal);

    byte[] cmd =
    {
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
    if (log.isDebugEnabled())
    {
      log.debug("data: " + cmd);
    }

    return escapeDLE(cmd);
  }

  public static void runSetClockDate(final String portName) throws
      DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName, AxboCommandUtil.getDateCmd(), 1);
  }

  public static byte[] getLogDataCmd()
  {
    log.info("retrieving log data");

    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCD, // get log command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCE      // protocal end + checksum
    };
    return cmd;
  }

  public static void runLogDataCmd(final String portName) throws
      DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getLogDataCmd(), 1);
  }

  public static byte[] getClearLogDataCmd()
  {
    log.info("clear log data");

    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCE, // clear log command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCF    // protocal end + checksum
    };
    return cmd;
  }

  public static void runClearClockData(final String portName) throws
      DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getClearLogDataCmd(), 1);
  }

  public static byte[] getStatusCmd()
  {
    log.info("reading status");

    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0x36, // get status command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x37  // protocal end + checksum
    };
    return cmd;
  }

  public static void runReadStatus(final String portName) throws DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getStatusCmd(), 1);
  }

  public static byte[] getDummyCmd(boolean toggleBit)
  {
    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, // protocol start
      (toggleBit ? (byte) 0x81 : (byte) 0x01), // toggle bit
      (byte) 0x39, // dummy command
      (byte) 0x10, (byte) 0x03, (byte) 0x00,
      (byte) (0x39 + (toggleBit ? 0x81 : 0x01))  // protocal end + checksum
    };
    return cmd;
  }

  public static byte[] getCheckCmd()
  {
    log.info("reading status");

    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xCB, // get check command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xCC  // protocal end + checksum
    };
    return cmd;
  }

  public static void runCheckCmd(final String portName) throws DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName,
        AxboCommandUtil.getCheckCmd(), 1);
  }

  public static byte[] getTestCmd(final byte commandId)
  {
    log.info("running test command");
    byte checksum = ((byte) 0xCB);
    checksum += commandId;
    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xC0, commandId, (byte) 0x0A, // get test command
      (byte) 0x10, (byte) 0x03, (byte) 0x00, checksum // protocal end + checksum
    };
    return cmd;
  }

  public static void runTestCmd(final String portName, final byte commandId)
      throws DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName, AxboCommandUtil.getTestCmd(
        commandId), 1);
  }

  public static byte[] getSetSerialNumberCmd(String serialNumber)
  {
    log.info("setting serial number: " + serialNumber);
    if (serialNumber.length() != 8)
    {
      throw new IllegalArgumentException("invalid length: " + serialNumber.
          length());
    }
    char[] digits = new char[8];
    int i = 0;
    for (char digit : serialNumber.toCharArray())
    {
      digits[i] = (char) (digit < 48 ? digit + 48 : digit);
      //digits[i] = (char)(digit - 48);
      i++;
    }
    byte[] cmd =
    {
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

  public static void runSetSerialNumberCmd(final String portName, final String serialNumber)
      throws DataInterfaceException
  {
    syncInterface(portName);

    try
    {
      getDataInterface().writeData(portName, AxboCommandUtil.getSetSerialNumberCmd(serialNumber), 1);
    }
    catch (Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }
  }

  public static byte[] getClearSerialNumberCmd()
  {
    log.info("clearing serial number");
    byte[] cmd =
    {
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

  public static void runClearSerialNumberCmd(final String portName)
      throws DataInterfaceException
  {
    syncInterface(portName);

    try
    {
      getDataInterface().writeData(portName, AxboCommandUtil.getClearSerialNumberCmd(), 1);
    }
    catch (Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }
  }

  public static byte[] getFlashPageToBuffer()
  {
    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0xBB, // get set serial number command
      (byte) 0x00, (byte) 0x00, // page address
      (byte) 0x10, // protocol end
      (byte) 0x03,
      (byte) 0x00, // checksum
      (byte) 0x00 // checksum
    };
    int checksum = ByteUtil.calcChecksum(cmd, 3, 7);
    cmd[cmd.length - 2] = ByteUtil.highByte(checksum);
    cmd[cmd.length - 1] = ByteUtil.lowByte(checksum);
    return cmd;
  }

  public static byte[] getBufferRead(int start)
  {
    byte[] cmd =
    {
      (byte) 0x00, (byte) 0x10, (byte) 0x02, (byte) 0x01, // protocol start
      (byte) 0x3E, // get set serial number command
      (byte) 0x00, (byte) start, // byte address
      (byte) FRAME_SIZE, // number of bytes
      (byte) 0x10, // protocol end
      (byte) 0x03,
      (byte) 0x00, // checksum
      (byte) 0x00 // checksum
    };
    int checksum = ByteUtil.calcChecksum(cmd, 3, 8);
    cmd[cmd.length - 2] = ByteUtil.highByte(checksum);
    cmd[cmd.length - 1] = ByteUtil.lowByte(checksum);
    return cmd;
  }

  public static void runReadFlash(final String portName, int start) throws DataInterfaceException
  {
    syncInterface(portName);
    getDataInterface().writeData(portName, AxboCommandUtil.getFlashPageToBuffer(), 1);
    getDataInterface().writeData(portName, AxboCommandUtil.getBufferRead(start), 1);
  }

  public static void syncInterface(final String portName)
  {
    try
    {
      getDataInterface().writeData(portName, AxboCommandUtil.getDummyCmd(false),
          1);
    }
    catch (DataInterfaceException ex)
    {
    }
    try
    {
      getDataInterface().writeData(portName, AxboCommandUtil.getDummyCmd(true),
          1);
    }
    catch (DataInterfaceException ex)
    {
    }
  }

  public static void clearHeader(final String portName)
      throws DataInterfaceException
  {
    int bufferPos = 0;
    boolean toggleBit = false;
    for (int i = 0; i < 16; i++)
    {
      final byte[] protocol =
      {
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

  public static void writeHeader(final String portName, 
      final SoundPackage soundPackage)
      throws DataInterfaceException
  {

    int bufferPos = 16;
    boolean toggleBit = false;
    for (final Sound sound : soundPackage.getSounds())
    {
      int endPage = sound.getStartPage() + sound.getPageCount() - 1;
      final byte[] protocol =
      {
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
      for (int i = 0; i < nameChars.length; i++)
      {
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

  public static void writePage(final String portName, final byte[] soundData,
      final int page) throws DataInterfaceException
  {
    //final byte highByte = bufferToggle ? (byte)0x00 : (byte)0x80;
    final byte highByte = (byte) 0x00;
    boolean protocolToggle = true;
    // write frames to page buffer
    for (int frame = 0; frame < PAGE_SIZE / FRAME_SIZE; frame++)
    {
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

  public static void writeBufferToPage(String portName, int page)
      throws DataInterfaceException
  {
    byte pageHighByte = ByteUtil.highByte(page);
    byte pageLowByte = ByteUtil.lowByte(page);

    byte[] cmd =
    {
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

  public static byte[] escapeDLE(byte[] data)
  {
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    @SuppressWarnings("UnusedAssignment")
    int b = 0;
    int pos = 0;
    while ((b = in.read()) != -1)
    {
      if (b == 0x10 && pos > 4 && pos < data.length - 4)
      {
        out.write(b);
      }
      out.write(b);
      pos++;
    }
    return out.toByteArray();
  }

  private static DataInterface getDataInterface()
  {
    return DeviceContext.getDeviceType().getDataInterface();
  }
}
