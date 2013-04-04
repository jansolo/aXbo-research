/*
 * $Id: SleepDataTimeSeries.java,v 1.10 2010-12-16 23:13:53 illetsch Exp $
 * Copyright 3kraft May 15, 2007
 */
package com.dreikraft.axbo.timeseries;

import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class SleepDataTimeSeries extends TimeSeries
    implements PropertyChangeListener
{

  private final TimeZone timeZone;
  private final SleepData sleepData;
  private final int maxMovements;
  private final Map<TimePeriod, Integer> movementsX;
  private final Map<TimePeriod, Integer> movementsY;

  public SleepDataTimeSeries(final String name, final SleepData sleepData,
      final Class<?> timePeriodClass, final int maxMovements)
  {
    super(name);
    this.sleepData = sleepData;
    this.maxMovements = maxMovements;
    this.sleepData.addPropertyChangeListener(this);

    movementsX = new HashMap<TimePeriod, Integer>();
    movementsY = new HashMap<TimePeriod, Integer>();

    // set start and end time
    timeZone = TimeZone.getDefault();
    final RegularTimePeriod startTime = RegularTimePeriod.createInstance(
        timePeriodClass, sleepData.calculateStartTime(), timeZone);
    add(startTime, 0);

    final Date endTime = sleepData.calculateEndTime();
    final RegularTimePeriod endTimePeriod = RegularTimePeriod.createInstance(
        timePeriodClass, endTime, timeZone);
    addOrUpdate(endTimePeriod, 0);

    for (MovementData movement : sleepData.getMovements())
    {
      addMovementData(movement);
    }
  }

  public void close()
  {
    sleepData.removePropertyChangeListener(this);
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }

  public int getMaxMovements()
  {
    return maxMovements;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    addMovementData((MovementData) evt.getNewValue());
  }

  private void addMovementData(MovementData data)
  {
    // get time of movement
    final RegularTimePeriod timePeriod = RegularTimePeriod.createInstance(
        getTimePeriodClass(), data.getTimestamp(), timeZone);

    int x = 0;
    Integer xOld = 0;
    if ((x = data.getMovementsX()) > 0)
    {
      if ((xOld = movementsX.get(timePeriod)) != null)
      {
        x += xOld;
      }
      movementsX.put(timePeriod, x);
    }

    int y = 0;
    Integer yOld = 0;
    if ((y = data.getMovementsY()) > 0)
    {
      if ((yOld = movementsY.get(timePeriod)) != null)
      {
        y += yOld;
      }
      movementsY.put(timePeriod, y);
    }

    if (x + y > 0)
    {
      final int value = (x > y ? x : y);
      final int trimmedValue = value < maxMovements ? value : maxMovements;
    
      delete(timePeriod);
      add(new TimeSeriesDataItem(timePeriod, trimmedValue));
    }
  }
}
