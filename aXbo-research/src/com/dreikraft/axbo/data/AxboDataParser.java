/*
 * $Id$
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.data;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.events.InfoEvent;
import com.dreikraft.axbo.events.MovementEvent;
import com.dreikraft.axbo.util.ByteUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AxboDataParser
 *
 * @author jan_solo
 * @author $Author$
 * @version $Revision$
 */
public enum AxboDataParser implements ProtocolHandler
{

  INSTANCE;
  static final Log log = LogFactory.getLog(AxboDataParser.class);
  public static final int STATUS_DATA_BYTE = 0x20;
  public static final int MOVEMENT_DATA_BYTE = 0x26;
  public static final int DUMMY_DATA_BYTE = 0xA4;
  public static final int FLASH_BUFFERREAD = 0x00;
  public static final int DLE = 0x10;
  public static final int STX = 0x02;
  public static final int ETX = 0x03;
  public static final int TOGGLE_BYTE_INPUT_OFF = 0x00;
  public static final int TOGGLE_BYTE_INPUT_ON = 0x80;
  public static final int TOGGLE_BYTE_OUTPUT_OFF = 0x01;
  public static final int TOGGLE_BYTE_OUTPUT_ON = 0x81;
  public static final int ACK = 0x06;
  public static final int NAK = 0x15;
  public static final int BUF_SIZE = 30;

  ;
  private AxboDataContext ctx = new AxboDataContext();

  @Override
  public void parse(final byte[] data)
  {
    if (log.isDebugEnabled())
    {
      if (log.isDebugEnabled())
      {
        log.debug(ByteUtil.dumpByteArray(data));
      }
    }

    for (final byte dataItem : data)
    {
      ctx.setDataItem(ByteUtil.byteToInt(dataItem));
      ctx.getState().process(ctx);
    }
  }
}

interface AxboDataState
{

  void process(AxboDataContext ctx);
}

class AxboDataContext
{

  private AxboDataState state;
  private AxboDataState previousState;
  private int dataItem;

  public AxboDataContext()
  {
    state = AxboDataStates.BEGIN;
  }

  public AxboDataState getState()
  {
    return state;
  }

  public void setState(AxboDataState state)
  {
    this.previousState = this.state;
    this.state = state;
  }

  public int getDataItem()
  {
    return dataItem;
  }

  public void setDataItem(int dataItem)
  {
    this.dataItem = dataItem;
  }

  public AxboDataState getPreviousState()
  {
    return previousState;
  }
}

enum AxboDataStates implements AxboDataState
{

  BEGIN()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      switch (ctx.getDataItem())
      {
        case 0x02: // STX
          ctx.setState(AxboDataStates.STX);
          break;
        case 0x06: // ACK
          ctx.setState(AxboDataStates.ACK);
          break;
        case 0x15: // NACK
          ctx.setState(AxboDataStates.NACK);
          break;
      }
    }
  },
  ACK()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      ctx.setState(AxboDataStates.BEGIN);
      DeviceContext.getDeviceType().getDataInterface().dataReceived();
    }
  },
  NACK()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      ctx.setState(AxboDataStates.BEGIN);
      DeviceContext.getDeviceType().getDataInterface().dataReceived();
    }
  },
  STX()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      bufferPos = 0;
      buffer = new int[AxboDataParser.BUF_SIZE];
      ctx.setState(AxboDataStates.DATA);
    }
  },
  DATA()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      if (ctx.getDataItem() == 0x10) // escape character
      {
        ctx.setState(AxboDataStates.DATA_ESCAPE);
      }
      else
      {
        buffer[bufferPos] = ctx.getDataItem();
        bufferPos++;
      }
    }
  },
  DATA_ESCAPE()
  {

    @Override
    public void process(AxboDataContext ctx)
    {
      if (ctx.getDataItem() == 0x03) // ETX 
      {
        ctx.setState(AxboDataStates.CHECKSUM);
      }
      else
      {
        ctx.setState(ctx.getPreviousState());
        ctx.getState().process(ctx);
      }
    }
  },
  CHECKSUM()
  {

    private int pos = 0;

    @Override
    public void process(AxboDataContext ctx)
    {
      if (pos == 1)
      {
        pos = 0;
        ctx.setState(AxboDataStates.BEGIN);
        handleData(buffer);
        DeviceContext.getDeviceType().getDataInterface().dataReceived();
      }
      pos++;
    }
  };
  static int[] buffer;
  static int bufferPos;

  void handleData(final int[] data)
  {
    try
    {
      if (AxboDataParser.log.isDebugEnabled())
      {
        if (AxboDataParser.log.isDebugEnabled())
        {
          AxboDataParser.log.debug(ByteUtil.dumpByteArray(data));
        }
      }

      // is status data
      switch (data[0])
      {
        case AxboDataParser.STATUS_DATA_BYTE:
          char[] version =
          {
            (char) data[1], (char) data[2], (char) data[3],
            (char) data[4], (char) data[5], (char) data[6], (char) data[7],
            (char) data[8]
          };
          char[] hw =
          {
            (char) data[10], (char) data[11]
          };
          int[] rtc =
          {
            data[13], data[14], data[15]
          };
          StringBuffer serialNumber = new StringBuffer("");
          for (int i = 17; i < 17 + 8; i++)
          {
            if ((data[i] >= 0 && data[i] <= 9) || (data[i] >= 48 && data[i]
                <= 57))
            {
              serialNumber.append((char) (data[i] < 48 ? data[i] + 48 : data[i]));
            }
          }
          if (serialNumber.toString().equals("00000000"))
          {
            serialNumber = new StringBuffer("");
          }
          // put the event into the event queue
          final AxboInfo axboData = new AxboInfo(serialNumber.toString().trim(),
              new String(hw).trim(), new String(version).trim(),
              String.valueOf(ByteUtil.hexToDec(rtc)));
          final InfoEvent info = new InfoEvent(this, axboData);
          ApplicationEventDispatcher.getInstance().dispatchEvent(info);
          break;

        case AxboDataParser.MOVEMENT_DATA_BYTE:
          if (AxboDataParser.log.isDebugEnabled())
          {
            AxboDataParser.log.debug("protocol type: " + (char) data[data.length
                - 1]);
          }

          // reduce sender ids from 8 two 2
          final SensorID sensorId = (data[1]) % 2 == 1 ? SensorID.P1
              : SensorID.P2;

          // create movement event from record
          final MovementData movementData = new MovementData();
          final Calendar cal = GregorianCalendar.getInstance();
          cal.clear();
          cal.set(2000 + Integer.valueOf("" + (char) data[2] + (char) data[3]),
              Integer.valueOf(("" + (char) data[4] + (char) data[5])) - 1,
              Integer.valueOf("" + (char) data[6] + (char) data[7]),
              Integer.valueOf("" + (char) data[8] + (char) data[9]),
              Integer.valueOf("" + (char) data[10] + (char) data[11]),
              Integer.valueOf("" + (char) data[12] + (char) data[13]));
          movementData.setTimestamp(cal.getTime());
          movementData.setMovementsX(ByteUtil.upperNibble(data[15]) + ByteUtil.
              lowerNibble(data[15]));
          if (data.length == 18)
          {
            movementData.setMovementsY(ByteUtil.upperNibble(data[16]) + ByteUtil.
                lowerNibble(data[16]));
          }
          else
          {
            movementData.setMovementsY(0);
          }

          final MovementEvent movementEvent = new MovementEvent(this,
              movementData,
              sensorId.toString(), "" + (char) data[data.length - 1]);
          if (AxboDataParser.log.isDebugEnabled())
          {
            AxboDataParser.log.debug("movement event: " + movementEvent);
          }

          // put the event into the event queue
          ApplicationEventDispatcher.getInstance().dispatchEvent(movementEvent);
          break;
      }
    }
    catch (Exception ex)
    {
      AxboDataParser.log.error("failed to process data:" + ByteUtil.
          dumpByteArray(data), ex);
    }
  }
}
