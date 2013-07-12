package com.dreikraft.axbo.task;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.data.AxboResponseProtocol;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.data.DeviceType;
import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SensorID;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.data.WakeInterval;
import com.dreikraft.axbo.data.WakeType;
import com.dreikraft.axbo.events.MovementEvent;
import com.dreikraft.axbo.events.SleepDataAdded;
import com.dreikraft.axbo.events.SleepDataImported;
import com.dreikraft.axbo.util.BundleUtil;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SleepDataImportTask
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataImportTask extends AxboTask<Integer, Integer>
    implements ApplicationEventEnabled {

  private static final Log log = LogFactory.getLog(SleepDataImportTask.class);
  private List<MovementEvent> movementEventsP1;
  private List<MovementEvent> movementEventsP2;
  private List<SleepData> sleepDates;
  private int dataCount = 0;
  private int newSleepDataCount = 0;

  public SleepDataImportTask(final List<SleepData> sleepDates) {
    super();
    this.sleepDates = sleepDates;
    movementEventsP1 = new ArrayList<MovementEvent>();
    movementEventsP2 = new ArrayList<MovementEvent>();
  }

  @Override
  protected Integer doInBackground() throws Exception {
    log.info("performing task" + getClass().getSimpleName() + " ...");

    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        MovementEvent.class, this);

    // run the command
    AxboCommandUtil.runLogDataCmd(Axbo.getPortName());

    // read data until no more data is send
    int oldDataCount = -1;
    try {
      synchronized (this) {
        while (dataCount > oldDataCount) {
          oldDataCount = dataCount;
          wait(1000);
        }
      }
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
    }

    // store data ffrom aXbo to file
    processLogData(movementEventsP1);
    processLogData(movementEventsP2);

    return newSleepDataCount;
  }

  @Override
  protected void done() {
    try {
      final Integer newCount = get();
      log.info("task " + getClass().getSimpleName() + " performed successfully");

      setResult(Result.SUCCESS);

      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new SleepDataImported(
          this, newCount));
    } catch (InterruptedException ex) {
      log.error("task " + getClass().getSimpleName() + " interrupted", ex);
      setResult(Result.INTERRUPTED);
    } catch (ExecutionException ex) {
      log.error("task " + getClass().getSimpleName() + " failed", ex.getCause());
      setResult(Result.FAILED);
    } finally {
      DeviceContext.getDeviceType().getDataInterface().stop();
      ApplicationEventDispatcher.getInstance()
          .deregisterApplicationEventHandler(
          MovementEvent.class, this);
    }
  }

  /**
   * Process movement records.
   *
   * @param movementEvent
   */
  public void handle(final MovementEvent movementEvent) {

    // notify swingworker about data
    synchronized (this) {
      dataCount = AxboResponseProtocol.END.getLetterAsString().equals(
          movementEvent.getCmd()) ? 0 : dataCount + 1;
      notifyAll();
    }

    // add movement record to corresponding person
    if (movementEvent.getId().equals(SensorID.P1.toString())) {
      movementEventsP1.add(movementEvent);
    } else if (movementEvent.getId().equals(SensorID.P2.toString())) {
      movementEventsP2.add(movementEvent);
    }
  }

  @SuppressWarnings("fallthrough")
  private void processLogData(final List<MovementEvent> movementEvents) {
    // sort movements
    Collections.sort(movementEvents);

    if (movementEvents.size() > 0) {
      // get sensor
      final SensorID sensorId = SensorID.valueOf(movementEvents.get(0).getId());
      final String name = Axbo.getApplicationPreferences().get(
          sensorId.toString(),
          sensorId.getDefaultName());
      // create initial sleep data object
      SleepData sleepData = new SleepData(sensorId.toString(), name,
          DeviceType.AXBO, "");
      long currentSleepEnd = Long.MAX_VALUE;

      // iterate over all movements for current sensor id
      for (int i = 0; i < movementEvents.size(); i++) {
        // get current movement
        final MovementData movement = movementEvents.get(i).getMovementData();
        // retrieve protocol type of movement data
        final AxboResponseProtocol protocolType = AxboResponseProtocol.
            valueOfLetter(movementEvents.get(i).getCmd());

        // calculate the time difference between previous and current movement
        long delta = 0;
        final MovementData prevMovement = sleepData.findLastMovement();
        if (prevMovement != null && movement.isMovement())
          delta = movement.getTimestamp().getTime() - prevMovement
              .getTimestamp().getTime();

        if (currentSleepEnd < movement.getTimestamp().getTime() || delta
            > Axbo.CLEANER_INTERVAL_DEFAULT) {
          // store current sleepdata
          storeSleepData(sleepData);
          sleepData = new SleepData(sensorId.toString(), name,
              DeviceType.AXBO, "");
          currentSleepEnd = Long.MAX_VALUE;
        }

        // handle different protocols
        switch (protocolType) {
          case BEGIN:
          case NEXT:
          case END:
            if (movement.getMovementsX() > 0)
              sleepData.addMovement(movement);
            break;

          case KEY:
            if (sleepData.getWakeIntervalStart() == null) {
              movement.setMovementsZ(MovementData.KEY);
              sleepData.addMovement(movement);
            }
            break;

          case SNOOZE:
            sleepData.addMovement(movement);
            currentSleepEnd = movement.getTimestamp().getTime()
                + 3 * sleepData.getWakeInterval().getTime();
            break;

          case RANDOM_WAKE:
          case GOOD_WAKE:
            // set wake time and mark sleep data for saving
            sleepData.setWakeupTime(movement.getTimestamp());
            sleepData.setWakeType(WakeType.GOOD);
            currentSleepEnd = movement.getTimestamp().getTime()
                + SleepData.SNOOZE_WAIT_INTERVAL;
            break;

          case WAKE:
            // set wake time and mark sleepdata for saving
            sleepData.setWakeupTime(movement.getTimestamp());
            sleepData.setWakeType(WakeType.LAST);
            currentSleepEnd = movement.getTimestamp().getTime()
                + SleepData.SNOOZE_WAIT_INTERVAL;
            break;

          case POWER_NAPPING:
            // create a sleepdata when powernapping starts
            storeSleepData(sleepData);
            sleepData = new SleepData(sensorId.toString(), name,
                DeviceType.AXBO, "Power Nap");
            sleepData.setPowerNap(true);
            sleepData.setWakeIntervalStart(movement.getTimestamp());
            break;

          case WAKE_INTERVAL_START:
          case WAKE_INTERVAL_SHORT:
            currentSleepEnd = movement.getTimestamp().getTime()
                + 3 * sleepData.getWakeInterval().getTime();
            if (sleepData.getWakeIntervalStart() == null) {
              sleepData.setWakeIntervalStart(movement.getTimestamp());
              sleepData.setWakeInterval(WakeInterval
                  .getWakeIntervalFromProtocol(protocolType));
              currentSleepEnd = movement.getTimestamp().getTime()
                  + 3 * sleepData.getWakeInterval().getTime();
            } else {
              // reverse calculate snooze times from wake interval restarts
              movement.setMovementsZ(MovementData.SNOOZE);
              movement.setTimestamp(new Date(movement.getTimestamp().getTime()
                  - SleepData.SNOOZE_RESTART_INTERVAL));
              sleepData.addMovement(movement);
            }
            break;
          default:
            if (log.isDebugEnabled())
              log.debug("unprocessed protocol type: " + protocolType);
            break;
        }
      }
      // store last sleepData Object
      storeSleepData(sleepData);
    }
  }

  private void storeSleepData(final SleepData sleepData) {
    if (sleepData.getMovements().size() < 2) {
      return;
    }

    if (!sleepData.isPowerNap() && (sleepData.calculateDuration()
        < Axbo.MINIMUM_SLEEP_DURATION || sleepData.calculateMovementCount()
        < Axbo.MINIMUM_MOVEMENTS || sleepData.calculateMovementsPerHour()
        < Axbo.AVERAGE_MOVEMENTS_THRESHOLD
        || (sleepData.getWakeupTime() == null && sleepData
        .getWakeIntervalStart()
        == null))) {
      return;
    }

    if (sleepDates.contains(sleepData)) {
      return;
    }

    final File dir = new File(Axbo.PROJECT_DIR_DEFAULT);
    final File f = new File(dir, sleepData.getSleepDataFilename());
    try {
      if (f.exists()) {
        return;
      }

      // write to xml file
      final XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
          new FileOutputStream(f)));
      encoder.writeObject(sleepData);
      encoder.close();

      // add sleep data to project table
      sleepData.setDataFile(f);

      newSleepDataCount++;

      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new SleepDataAdded(
          this, sleepData));
      final String statusMsg = BundleUtil.getMessage(
          "statusLabel.fileSaved", f.getName(), dir.getAbsolutePath());
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new ApplicationMessageEvent(
          this, statusMsg, false));
    } catch (Exception ex) {
      final String msg = BundleUtil.getErrorMessage("sleepData.saveFailed",
          f.getName(), dir.getAbsolutePath());
      log.error(ex.getMessage(), ex);
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new ApplicationMessageEvent(
          this, msg, true));
    }
  }
}
