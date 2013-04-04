/*
 * © 2008 3kraft
 * $Id: SleepData.java,v 1.33 2010-12-16 23:13:53 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
 * $Id: SleepData.java,v 1.33 2010-12-16 23:13:53 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.33 $
 */
public class SleepData implements Serializable
{

  public static final Log log = LogFactory.getLog(SleepData.class);
  public static final long UNSET = -1;
  public static final long HOUR = 60 * 60 * 1000;
  public static final long WAKE_INTERVAL = 30 * 60 * 1000;
  public static final long SLEEP_TRIGGER_INTERVAL = 8 * 60 * 1000;
  public static final long SLEEP_START_DELAY = 4 * 60 * 1000;
  public static final long DEFAULT_SLEEP_DURATION = 8 * 60 * 60 * 1000;
  public static final String SLEEP_DATA_FILE_EXT1 = ".axm";
  public static final String SLEEP_DATA_FILE_EXT2 = ".spw";
  private String id;
  private String name;
  private Date wakeupTime;
  private Date wakeIntervalStart;
  private List<MovementData> movements;
  private DeviceType deviceType;
  private String comment;
  private boolean powerNap = false;
  private WakeType wakeType = WakeType.NONE;
  private String firmwareVersion;
  private transient Date sleepStart;
  private transient PropertyChangeSupport propertyChangeSupport;
  private transient File dataFile;
  private transient int startHour = (int) UNSET;

  /** Creates a new instance of SleepData */
  public SleepData()
  {
    this.powerNap = false;
    movements = new ArrayList<MovementData>();
    this.propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public SleepData(String id, String name, DeviceType deviceType,
      String comment)
  {
    this();
    this.id = id;
    this.name = name;
    this.deviceType = deviceType;
    this.comment = comment;
  }

  public SleepData(String id, String name, Date wakeupTime,
      Date wakeIntervalStart,
      List<MovementData> movements, DeviceType deviceType, String comment,
      boolean powerNap)
  {
    this.id = id;
    this.name = name;
    this.wakeupTime = wakeupTime;
    this.wakeIntervalStart = wakeIntervalStart;
    this.movements = movements;
    this.deviceType = deviceType;
    this.comment = comment;
    this.powerNap = powerNap;
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public Date calculateStartTime()
  {
    Date startTime = null;
    if (isPowerNap())
    {
      startTime = getWakeIntervalStart();
    }
    if (startTime == null && movements.size() > 0)
    {
      startTime = movements.get(0).getTimestamp();
    }
    return startTime;
  }

  private int calculateStartHour()
  {
    Calendar curStartCal = Calendar.getInstance();
    curStartCal.setTime(calculateStartTime());
    return curStartCal.get(Calendar.HOUR_OF_DAY);
  }

  public Date calculateEndTime()
  {
    Date endTime = new Date(new Date().getTime()
        + DEFAULT_SLEEP_DURATION);
    if (powerNap)
    {
      endTime = new Date(calculateStartTime().getTime() + WAKE_INTERVAL);
    }
    else if (movements.size() > 0)
    {
      endTime = movements.get(movements.size() - 1).getTimestamp();
    }
    if (getWakeIntervalStart() != null && getWakeIntervalStart().getTime()
        + WAKE_INTERVAL > endTime.getTime())
    {
      endTime = new Date(getWakeIntervalStart().getTime() + WAKE_INTERVAL);
    }
    if (getWakeupTime() != null && getWakeupTime().getTime() > endTime.getTime())
    {
      endTime = getWakeupTime();
    }
    return endTime;
  }

  public int calculateEndHour()
  {
    int durationHours = 2 + (int) (calculateDuration() / (1000 * 60 * 60));
    return getStartHour() + durationHours;
  }

  public Date calculateSleepStart()
  {
    if (sleepStart != null)
    {
      return sleepStart;
    }

    sleepStart = calculateStartTime();
    if (powerNap)
    {
      return sleepStart;
    }

    if (movements.size() > 0)
    {
      MovementData prevMove = movements.get(0);
      for (int i = 1; i < movements.size(); i++)
      {
        long delta = movements.get(i).getTimestamp().getTime() - prevMove.
            getTimestamp().getTime();
        if (delta > SLEEP_TRIGGER_INTERVAL)
        {
          sleepStart = new Date(prevMove.getTimestamp().getTime()
              + SLEEP_START_DELAY);
          return sleepStart;
        }
        prevMove = movements.get(i);
      }
    }
    return sleepStart;
  }

  public long calculateDuration()
  {
    if (calculateSleepStart() != null && wakeupTime != null)
    {
      return wakeupTime.getTime() - sleepStart.getTime();
    }
    else
    {
      return calculateEndTime().getTime() - calculateStartTime().getTime();
    }
  }

  public long calculateLatency()
  {
    return calculateSleepStart().getTime() - calculateStartTime().getTime();
  }

  public int calculateMovementCount()
  {
    int count = 0;
    for (MovementData move : movements)
    {
      count += move.getMovementsX() + move.getMovementsY()
          + move.getMovementsZ();
    }
    return count;
  }

  public double calculateMovementsPerHour()
  {
    long duration = calculateDuration();
    if (duration != UNSET && duration != 0)
    {
      double durationInHours = (double) calculateDuration() / (60 * 60 * 1000);
      return (double) calculateMovementCount() / durationInHours;
    }
    return 0;
  }

  public long calculateTimeSaving()
  {
    if (wakeIntervalStart == null || wakeupTime == null)
    {
      return UNSET;
    }

    long timesaving = wakeIntervalStart.getTime() + WAKE_INTERVAL - wakeupTime.
        getTime();

    if (timesaving < 0)
    {
      return UNSET;
    }
    return timesaving;
  }

  @Override
  public String toString()
  {
    String s = "";
    try
    {
      s = "{" + this.getClass() + ", " + this.getId() + ", " + this.getName()
          + ", " + this.calculateStartTime() + ", " + this.getWakeupTime()
          + ", " + this.getWakeIntervalStart() + ", " + this.calculateEndTime()
          + ", " + this.getDeviceType() + ", " + this.getComment() + ", " + this.
          isPowerNap() + ", " + this.getWakeType() + ", " + this.
          getFirmwareVersion() + "}";
    }
    catch (Exception ex)
    {
      log.error(ex);
    }
    return s;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Date getWakeupTime()
  {
    return wakeupTime;
  }

  public void setWakeupTime(Date wakeupTime)
  {
    this.wakeupTime = wakeupTime;
  }

  public List<MovementData> getMovements()
  {
    return movements;
  }

  public void setMovements(List<MovementData> movements)
  {
    List<MovementData> oldMovements = movements;
    this.movements = movements;
    propertyChangeSupport.firePropertyChange("movements", oldMovements,
        movements);
  }

  public MovementData getMovement(int index)
  {
    return movements.get(index);
  }

  public void setMovements(int index, MovementData movement)
  {
    MovementData oldMovement = movements.get(index);
    movements.set(index, movement);
    propertyChangeSupport.fireIndexedPropertyChange("movements", index,
        oldMovement, movement);
  }

  public int addMovement(MovementData movement)
  {
    int index = movements.size();
    movements.add(movement);
    propertyChangeSupport.fireIndexedPropertyChange("movements", index,
        null, movement);
    return index;
  }

  public DeviceType getDeviceType()
  {
    return deviceType;
  }

  public void setDeviceType(DeviceType deviceType)
  {
    this.deviceType = deviceType;
  }

  public void setDataFile(File dataFile)
  {
    this.dataFile = dataFile;
  }

  public File getDataFile()
  {
    return dataFile;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public Date getWakeIntervalStart()
  {
    return wakeIntervalStart;
  }

  public void setWakeIntervalStart(Date wakeIntervalStart)
  {
    this.wakeIntervalStart = wakeIntervalStart;
  }

  public String getComment()
  {
    return comment == null ? "" : comment;
  }

  public void setComment(String comment)
  {
    this.comment = comment;
  }

  public boolean isPowerNap()
  {
    return powerNap;
  }

  public void setPowerNap(boolean powerNap)
  {
    this.powerNap = powerNap;
  }

  public WakeType getWakeType()
  {
    return wakeType;
  }

  public void setWakeType(WakeType wakeType)
  {
    this.wakeType = wakeType;
  }

  public int getStartHour()
  {
    if (startHour == (int) UNSET)
    {
      startHour = calculateStartHour();
    }
    return startHour;
  }

  public void setStartHour(int startHour)
  {
    this.startHour = startHour;
  }

  public String getFirmwareVersion()
  {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion)
  {
    this.firmwareVersion = firmwareVersion;
  }

  public String getSleepDataFilename()
  {
    return getName().replaceAll(" ", "_") + "_" + new SimpleDateFormat(
        "yyyy_MM_dd_HH_mm").format(new Date(
        calculateStartTime().getTime())) + SLEEP_DATA_FILE_EXT1;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final SleepData other = (SleepData) obj;
    final Date thisSleepStart = calculateStartTime();
    final Date otherSleepStart = other.calculateStartTime();
    if (!thisSleepStart.equals(otherSleepStart))
    {
      return false;
    }
    final long thisDuration = calculateDuration();
    final long otherDuration = other.calculateDuration();
    if (thisDuration != otherDuration)
    {
      return false;
    }
    final int thisMovementCount = calculateMovementCount();
    final int otherMovementCount = other.calculateMovementCount();
    if (thisMovementCount != otherMovementCount)
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 83 * hash + calculateSleepStart().hashCode();
    hash = 83 * hash + (int) calculateDuration();
    hash = 83 * hash + calculateMovementCount();
    return hash;
  }
}
