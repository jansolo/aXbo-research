package com.dreikraft.axbo.data;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.events.InfoEvent;
import com.dreikraft.axbo.events.MovementEvent;
import com.dreikraft.axbo.util.ByteUtil;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The AxboDataParser class processes data chunks received from the serial
 * interface. The data parser is implemented as an enum state machine (see
 * http://http://vanillajava.blogspot.co.at/2011/06/java-secret-using-enum-as-state-machine.html).
 * The aXbo protocol definition can be found here:
 * https://docs.google.com/document/d/1rQkp8XcMFh0PPenZfhWqaGXDfi2gdxutdeXtu-T6OTo/pub
 *
 * The class is implemented as an enum singleton (thread safe singleton).
 *
 * @author jan.illetschko@3kraft.com
 */
public enum AxboDataParser implements ProtocolHandler {

  /**
   * The singleton instance.
   */
  INSTANCE;
  /**
   * The logger.
   */
  public static final Log log = LogFactory.getLog(AxboDataParser.class);
  /**
   * Status command byte.
   */
  public static final int STATUS_DATA_BYTE = 0x20;
  /**
   * Movement command byte
   */
  public static final int MOVEMENT_DATA_BYTE = 0x26;
  /**
   * Dummy command byte.
   */
  public static final int DUMMY_DATA_BYTE = 0xA4;
  /**
   * Flash buffer read command byte
   */
  public static final int FLASH_BUFFERREAD = 0x00;
  /**
   * Data link escape byte.
   */
  public static final int DLE = 0x10;
  /**
   * Start transaction byte.
   */
  public static final int STX = 0x02;
  /**
   * End transaction byte.
   */
  public static final int ETX = 0x03;
  /**
   * Toggle byte input off.
   */
  public static final int TOGGLE_BYTE_INPUT_OFF = 0x00;
  /**
   * Toggle byte input on.
   */
  public static final int TOGGLE_BYTE_INPUT_ON = 0x80;
  /**
   * Toggle byte output off.
   */
  public static final int TOGGLE_BYTE_OUTPUT_OFF = 0x01;
  /**
   * Toggle byte output on.
   */
  public static final int TOGGLE_BYTE_OUTPUT_ON = 0x81;
  /**
   * Acknowledged byte.
   */
  public static final int ACK = 0x06;
  /**
   * Not acknowledged byte.
   */
  public static final int NACK = 0x15;
  /**
   * Initial data buffer size.
   */
  public static final int BUFFER_SIZE = 0x20;
  /**
   * State machine context for data parsing.
   */
  private final AxboDataContext ctx;

  private AxboDataParser() {
    ctx = new AxboDataContext();  
  }
  
  /**
   * {@inheritDoc}
   * @param data
   */
  @Override
  public void parse(final byte[] data) {

    if (log.isDebugEnabled()) {
      log.debug("Protocol: " + ByteUtil.dumpByteArray(data));
    }

    // for all bytes in this data chunk
    for (final byte dataItem : data) {
      // sets the current data byte into the state machine context.
      ctx.setDataItem(ByteUtil.byteToInt(dataItem));
      // processes the current data byte in the current state of the state 
      // machine.
      ctx.getState().process(ctx);
    }
  }

  @Override
  public void reset() {
    ctx.setState(AxboDataStates.BEGIN);
  }
}

/**
 * The AxboDataState interface defines the processing interface of each state in
 * the state machine.
 *
 * @author jan.illetschko@3kraft.com
 */
interface AxboDataState {

  /**
   * Process data in the current state. Needs to overridden in every instance of
   * the state machine enum.
   *
   * @param ctx
   */
  void process(AxboDataContext ctx);
}

/**
 * Data context passed between states of the state machine.
 *
 * @author jan.illetschko@3kraft.com
 */
class AxboDataContext {

  private AxboDataState state;
  private int dataItem;
  private int[] buffer;
  private int bufferPos;

  /**
   * Initializes the context in state BEGIN.
   */
  public AxboDataContext() {
    state = AxboDataStates.BEGIN;
  }

  /**
   * Retrieves the current state.
   *
   * @return the current state.
   */
  public AxboDataState getState() {
    return state;
  }

  /**
   * Sets the current state.
   *
   * @param state the state.
   */
  public void setState(AxboDataState state) {
    this.state = state;
  }

  /**
   * Gets the current data byte.
   *
   * @return the current data byte
   */
  public int getDataItem() {
    return dataItem;
  }

  /**
   * Sets the current data byte.
   *
   * @param dataItem the byte.
   */
  public void setDataItem(int dataItem) {
    this.dataItem = dataItem;
  }

  /**
   * Gets thh current data buffer.
   *
   * @return the bytes in the buffer.
   */
  public int[] getBuffer() {
    return buffer;
  }

  /**
   * Sets the data buffer.
   *
   * @param buffer a buffer
   */
  public void setBuffer(int[] buffer) {
    this.buffer = buffer;
  }

  /**
   * Get the current position in the buffer.
   *
   * @return the current position
   */
  public int getBufferPos() {
    return bufferPos;
  }

  /**
   * Set the position into processed data buffer.
   *
   * @param bufferPos the position.
   */
  public void setBufferPos(int bufferPos) {
    this.bufferPos = bufferPos;
  }
}

/**
 * The state machine for parsing the data chunks read from the serial interface.
 *
 * @author jan.illetschko@3kraft.com
 */
enum AxboDataStates implements AxboDataState {

  /**
   * State begin.
   */
  BEGIN() {
    @Override
    public void process(AxboDataContext ctx) {
      switch (ctx.getDataItem()) {
        case AxboDataParser.STX: // STX
          ctx.setState(AxboDataStates.STX);
          break;
        case AxboDataParser.ACK: // ACK
          ctx.setState(AxboDataStates.ACK);
          break;
        case AxboDataParser.NACK: // NACK
          ctx.setState(AxboDataStates.NACK);
          break;
        default:
      }
    }
  },
  /**
   * State ACK. Acknowledge received.
   */
  ACK() {
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      // switch to state begin
      ctx.setState(AxboDataStates.BEGIN);
      // notify serial interface implementation about data processed successfully
      DeviceContext.getDeviceType().getDataInterface().dataReceived();
    }
  },
  /**
   * State NACK. Not Acknowledge received.
   */
  NACK() {
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      // switch to state begin
      ctx.setState(AxboDataStates.BEGIN);
      // notify serial interface implementation about data processed successfully
      DeviceContext.getDeviceType().getDataInterface().dataReceived();
    }
  },
  /**
   * State STX. Start transaction received.
   */
  STX() {
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      // reset data buffer
      ctx.setBufferPos(0);
      ctx.setBuffer(new int[AxboDataParser.BUFFER_SIZE]);
      // switch to data processing
      ctx.setState(AxboDataStates.DATA);
    }
  },
  /**
   * State DATA. Data processing enabled.
   */
  DATA() {
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      if (ctx.getDataItem() == AxboDataParser.DLE) { // escape character
        // process escape character.
        ctx.setState(AxboDataStates.DATA_ESCAPE);
      } else {
        // write data byte to data buffer
        writeToBuffer(ctx);
      }
    }
  },
  /**
   * State DATA_ESCAPE. Handle escape characters in data chunk.
   */
  DATA_ESCAPE() {
    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      if (ctx.getDataItem() == AxboDataParser.ETX) { // ETX 
        // end transaction reached
        ctx.setState(AxboDataStates.CHECKSUM);
      } else {
        // skip DATA_ESCAPE character and write current byte to buffer
        writeToBuffer(ctx);
        ctx.setState(AxboDataStates.DATA);
      }
    }
  },
  /**
   * State CHECKSUM. Process data checksum.
   */
  CHECKSUM() {
    private int pos = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(AxboDataContext ctx) {
      // process 2 checksum bytes
      if (pos > 0) {
        pos = 0;
        ctx.setState(AxboDataStates.BEGIN);
        // process data buffer
        handleData(ctx.getBuffer());
      } else {
        pos++;
      }
    }
  };

  /**
   * Writes a data byte to the buffer. Resizes the buffer if required.
   *
   * @param ctx the data context of the state machine.
   */
  void writeToBuffer(final AxboDataContext ctx) {

    if (ctx.getBufferPos() >= ctx.getBuffer().length) {
      // end of buffer reached, resize buffer
      ctx.setBuffer(Arrays.copyOf(ctx.getBuffer(),
          ctx.getBuffer().length + AxboDataParser.BUFFER_SIZE));
    }
    // write data byte to buffer
    ctx.getBuffer()[ctx.getBufferPos()] = ctx.getDataItem();
    // increment buffer position pointer
    ctx.setBufferPos(ctx.getBufferPos() + 1);
  }

  /**
   * Process data buffer. Triggers various event based on data type.
   *
   * @param data the data buffer
   */
  void handleData(final int[] data) {
    try {
      if (AxboDataParser.log.isDebugEnabled()) {
        AxboDataParser.log.debug("Data: " + ByteUtil.dumpByteArray(data));
      }

      switch (data[0]) {

        case AxboDataParser.STATUS_DATA_BYTE:

          // data type is info data
          // extract version info
          char[] version = {
            (char) data[1], (char) data[2], (char) data[3],
            (char) data[4], (char) data[5], (char) data[6], (char) data[7],
            (char) data[8]
          };
          // extract hardware info
          char[] hw = {
            (char) data[10], (char) data[11]
          };
          // extract rtc
          int[] rtc = {
            data[13], data[14], data[15]
          };
          // read serial number if available
          StringBuffer serialNumber = new StringBuffer("");
          for (int i = 17; i < 17 + 8; i++) {
            if ((data[i] >= 0 && data[i] <= 9) || (data[i] >= 48 && data[i]
                <= 57)) {
              serialNumber
                  .append((char) (data[i] < 48 ? data[i] + 48 : data[i]));
            }
          }
          if (serialNumber.toString().equals("00000000")) {
            serialNumber = new StringBuffer("");
          }
          // put an info event into the event queue
          final AxboInfo axboData = new AxboInfo(serialNumber.toString().trim(),
              new String(hw).trim(), new String(version).trim(),
              String.valueOf(ByteUtil.hexToDec(rtc)));
          final InfoEvent info = new InfoEvent(this, axboData);
          ApplicationEventDispatcher.getInstance().dispatchEvent(info);

          // notify data processed successfully
          DeviceContext.getDeviceType().getDataInterface().dataReceived();
          break;

        case AxboDataParser.MOVEMENT_DATA_BYTE:

          // data type is a movment record
          if (AxboDataParser.log.isDebugEnabled()) {
            AxboDataParser.log.debug("protocol type: " + (char) data[17]);
          }

          // reduce sender ids from 8 two 2
          final SensorID sensorId = (data[1]) % 2 != 0 ? SensorID.P1
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
          if (data.length == 18) {
            movementData.setMovementsY(ByteUtil.upperNibble(data[16]) + ByteUtil
                .lowerNibble(data[16]));
          } else {
            movementData.setMovementsY(0);
          }

          final MovementEvent movementEvent = new MovementEvent(this,
              movementData,
              sensorId.toString(), "" + (char) data[17], data);
          if (AxboDataParser.log.isDebugEnabled()) {
            AxboDataParser.log.debug("movement event: " + movementEvent);
          }

          // put the event into the event queue
          ApplicationEventDispatcher.getInstance().dispatchEvent(
              movementEvent);

          // notify data processed successfully
          DeviceContext.getDeviceType().getDataInterface().dataReceived();
          break;

        case AxboDataParser.DUMMY_DATA_BYTE:

          // notify data processed successfully
          DeviceContext.getDeviceType().getDataInterface().dataReceived();
          break;
        default:
          if (AxboDataParser.log.isDebugEnabled())
            AxboDataParser.log.debug("Unhandled command: " + ByteUtil.dumpByte(
                data[0]));
      }
    } catch (RuntimeException ex) {
      AxboDataParser.log.error("failed to process data:" + ByteUtil.
          dumpByteArray(data), ex);
    }
  }
}
