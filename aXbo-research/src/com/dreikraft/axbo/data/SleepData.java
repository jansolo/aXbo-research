package com.dreikraft.axbo.data;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Combines movements records into a sleep record.
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepData implements Serializable {
  
  public static final long serialVersionUID = 1L;
  public static final Log log = LogFactory.getLog(SleepData.class);
  public static final long MINUTE = 60 * 1000;
  public static final long HOUR = 60 * 60 * 1000;
  public static final long SNOOZE_WAIT_INTERVAL = 2 * 60 * 1000;
  public static final long SNOOZE_RESTART_INTERVAL = 5 * 60 * 1000;
  public static final long SLEEP_TRIGGER_INTERVAL = 8 * 60 * 1000;
  public static final long SLEEP_START_DELAY = 4 * 60 * 1000;
  public static final long DEFAULT_SLEEP_DURATION = 8 * 60 * 60 * 1000;
  public static final String SLEEP_DATA_FILE_EXT = ".axm";
  public static final String SLEEP_DATA_FILE_EXT_PATTERN = "^.*(\\.axm|\\.spw)$";
  private String id;
  private String name;
  private Date wakeupTime;
  private Date wakeIntervalStart;
  private WakeInterval wakeInterval;
  private List<MovementData> movements;
  private DeviceType deviceType;
  private String comment;
  private boolean powerNap = false;
  private WakeType wakeType = WakeType.NONE;
  private String firmwareVersion;
  private transient Date sleepStart;
  private transient Date endTime;
  private transient File dataFile;
  private transient int compareStartHour;

  /**
   * Creates a new instance of SleepData
   */
  public SleepData() {
    this.powerNap = false;
    movements = new ArrayList<>();
  }

  /**
   * Creates a new instance of SleepData
   *
   * @param id
   * @param name
   * @param deviceType
   * @param comment
   */
  public SleepData(final String id, final String name,
      final DeviceType deviceType, final String comment) {
    this();
    this.id = id;
    this.name = name;
    this.deviceType = deviceType;
    this.comment = comment;
  }

  /**
   * Calculates the start time of this sleep record.
   *
   * @return if this is sleep record is a recorded power nap, returns the wake
   * interval start time, otherwise the first entry of the record.
   */
  public Date calculateStartTime() {
    if (isPowerNap()) {
      return getWakeIntervalStart();
    } else {
      return movements.get(0).getTimestamp();
    }
  }

  /**
   * Calculates the hour of the day when the record starts. Used to compare
   * diagrams.
   *
   * @return the hour of the day
   */
  public int calculateStartHour() {
    Calendar curStartCal = Calendar.getInstance();
    curStartCal.setTime(calculateStartTime());
    return curStartCal.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Calculates the end time of this sleep record.
   *
   * @return the calculated end of this sleep record
   */
  public Date calculateEndTime() {

    // use previously calculated value
    if (endTime == null) {

      // calculate a default value from start time and default duration
      endTime = new Date(new Date().getTime()
          + DEFAULT_SLEEP_DURATION);
      
      if (powerNap) {
        // use start time plus wake interval for powernap
        endTime = new Date(calculateStartTime().getTime() + getWakeInterval()
            .getTime());
      } else if (movements.size() > 0) {
        // otherwise take last movement entry
        endTime = movements.get(movements.size() - 1).getTimestamp();
      }

      // if the record has a wake interval
      final Date calculatedWakeIntervalEnd = calculateWakeIntervalEnd();
      endTime = calculatedWakeIntervalEnd != null ? calculatedWakeIntervalEnd
          : endTime;

      // if the record has a wake up time and its after the current calculated time
      if (getWakeupTime() != null && getWakeupTime().getTime() > endTime
          .getTime()) {
        endTime = getWakeupTime();
      }
    }
    return endTime;
  }

  /**
   * Calculates the time when the person of this record felt asleep. Only
   * calculated once.
   *
   * @return the time when the person felt asleep
   */
  public Date calculateSleepStart() {
    if (sleepStart != null) {
      return new Date(sleepStart.getTime());
    }
    
    sleepStart = calculateStartTime();
    if (powerNap) {
      return new Date(sleepStart.getTime());
    }
    
    if (movements.size() > 0) {
      MovementData prevMove = movements.get(0);
      for (int i = 1; i < movements.size(); i++) {
        long delta = movements.get(i).getTimestamp().getTime() - prevMove.
            getTimestamp().getTime();
        if (delta > SLEEP_TRIGGER_INTERVAL) {
          sleepStart = new Date(prevMove.getTimestamp().getTime()
              + SLEEP_START_DELAY);
          return new Date(sleepStart.getTime());
        }
        prevMove = movements.get(i);
      }
    }
    return new Date(sleepStart.getTime());
  }

  /**
   * Calculate the end of the wake interval. The wake interval may be extended
   * by using i-Snooze.
   *
   * @return the end of the wake interval or null, if there is no wake interval
   * in this record
   */
  public Date calculateWakeIntervalEnd() {
    Date wakeIntervalEnd = null;
    if (getWakeIntervalStart() != null) {
      wakeIntervalEnd = new Date(getWakeIntervalStart().getTime()
          + getWakeInterval().getTime());
      for (final MovementData movement : getMovements()) {
        if (movement.getMovementsZ() == MovementData.SNOOZE) {
          wakeIntervalEnd = new Date(movement.getTimestamp().getTime()
              + getWakeInterval().getTime());
        }
      }
    }
    return wakeIntervalEnd;
  }

  /**
   * Calculates the sleep duration for this data set. If possible the wake up
   * time is used for the calculation.
   *
   * @return the duration of the sleep in msec
   */
  public long calculateDuration() {
    if (calculateSleepStart() != null && wakeupTime != null) {
      return wakeupTime.getTime() - calculateSleepStart().getTime();
    } else {
      return calculateEndTime().getTime() - calculateStartTime().getTime();
    }
  }

  /**
   * Calculates the time between applying the sensor and falling asleep.
   *
   * @return the time in msec
   */
  public long calculateLatency() {
    return calculateSleepStart().getTime() - calculateStartTime().getTime();
  }

  /**
   * Calculates the sum of all movements between sleep start and wake up of this
   * sleep record.
   *
   * @return the movements sum
   */
  public int calculateMovementCount() {
    final Date start = calculateStartTime();
    final Date end = wakeupTime != null ? wakeupTime : calculateEndTime();
    
    int count = 0;
    for (MovementData move : movements) {
      if (move.getTimestamp().after(start) && move.getTimestamp().before(end))
        count += move.getMovementsX() + move.getMovementsY()
            + move.getMovementsZ();
    }
    return count;
  }

  /**
   * Calculates the average movement count per hour.
   *
   * @return the movement count
   */
  public double calculateMovementsPerHour() {
    long duration = calculateDuration();
    if (duration > 0) {
      double durationInHours = (double) duration / (60 * 60 * 1000);
      return (double) calculateMovementCount() / durationInHours;
    }
    return 0;
  }

  /**
   * Calculates the time saving. The saving is the difference between the actual
   * wake up time and the latest wake up time.
   *
   * @return the timesaving in msec or zero, if it can not be calculated
   */
  public long calculateTimeSaving() {
    if (wakeIntervalStart == null || wakeupTime == null) {
      return 0;
    }
    return Math.max(0, wakeIntervalStart.getTime() + getWakeInterval().getTime()
        - wakeupTime.getTime());
  }

  /**
   * Finds the last movement or key press in this sleep data record.
   *
   * @return the last movement or null
   */
  public MovementData findLastMovement() {
    for (int i = movements.size() - 1; i > -1; i--) {
      final MovementData movement = movements.get(i);
      if (movement.isMovement())
        return movement;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    String s = "";
    try {
      s = "{" + this.getClass() + ", " + this.getId() + ", " + this.getName()
          + ", " + this.calculateStartTime() + ", " + this.getWakeupTime()
          + ", " + this.getWakeIntervalStart() + ", " + this.calculateEndTime()
          + ", " + this.getDeviceType() + ", " + this.getComment() + ", " + this
          .isPowerNap() + ", " + this.getWakeType() + ", " + this.
          getFirmwareVersion() + "}";
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return s;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Date getWakeupTime() {
    return wakeupTime != null ? new Date(wakeupTime.getTime()) : null;
  }
  
  public void setWakeupTime(Date wakeupTime) {
    this.wakeupTime = new Date(wakeupTime.getTime());
  }
  
  public List<MovementData> getMovements() {
    return movements;
  }
  
  public void setMovements(List<MovementData> movements) {
    this.movements = movements;
  }
  
  public MovementData getMovement(int index) {
    return movements.get(index);
  }
  
  public void setMovements(int index, MovementData movement) {
    movements.set(index, movement);
  }
  
  public int addMovement(MovementData movement) {
    int index = movements.size();
    movements.add(movement);
    return index;
  }
  
  public DeviceType getDeviceType() {
    return deviceType;
  }
  
  public void setDeviceType(DeviceType deviceType) {
    this.deviceType = deviceType;
  }
  
  public void setDataFile(File dataFile) {
    this.dataFile = dataFile;
  }
  
  public File getDataFile() {
    return dataFile;
  }
  
  public Date getWakeIntervalStart() {
    return wakeIntervalStart != null ? new Date(wakeIntervalStart.getTime())
        : null;
  }
  
  public void setWakeIntervalStart(Date wakeIntervalStart) {
    this.wakeIntervalStart = new Date(wakeIntervalStart.getTime());
  }
  
  public WakeInterval getWakeInterval() {
    if (wakeInterval == null) {
      wakeInterval = WakeInterval.LONG;
    }
    return wakeInterval;
  }
  
  public void setWakeInterval(WakeInterval wakeInterval) {
    this.wakeInterval = wakeInterval;
  }
  
  public String getComment() {
    return comment == null ? "" : comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public boolean isPowerNap() {
    return powerNap;
  }
  
  public void setPowerNap(boolean powerNap) {
    this.powerNap = powerNap;
  }
  
  public WakeType getWakeType() {
    return wakeType;
  }
  
  public void setWakeType(WakeType wakeType) {
    this.wakeType = wakeType;
  }
  
  public int getCompareStartHour() {
    return compareStartHour;
  }
  
  public void setCompareStartHour(int compareStartHour) {
    this.compareStartHour = compareStartHour;
  }
  
  public String getFirmwareVersion() {
    return firmwareVersion;
  }
  
  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }
  
  public String getSleepDataFilename() {
    return getName().replaceAll(" ", "_") + "_" + new SimpleDateFormat(
        "yyyy_MM_dd_HH_mm").format(new Date(
                calculateStartTime().getTime())) + SLEEP_DATA_FILE_EXT;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SleepData other = (SleepData) obj;
    final Date thisSleepStart = calculateStartTime();
    final Date otherSleepStart = other.calculateStartTime();
    if (!thisSleepStart.equals(otherSleepStart)) {
      return false;
    }
    final long thisDuration = calculateDuration();
    final long otherDuration = other.calculateDuration();
    if (thisDuration != otherDuration) {
      return false;
    }
    final int thisMovementCount = calculateMovementCount();
    final int otherMovementCount = other.calculateMovementCount();
    if (thisMovementCount != otherMovementCount) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + calculateSleepStart().hashCode();
    hash = 83 * hash + (int) calculateDuration();
    hash = 83 * hash + calculateMovementCount();
    return hash;
  }
}
