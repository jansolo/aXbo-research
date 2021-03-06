/*
 * $Id: SleepDataTimeSeries.java,v 1.10 2010-12-16 23:13:53 illetsch Exp $
 * Copyright 3kraft May 15, 2007
 */
package com.dreikraft.axbo.timeseries;

import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
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
{

  private final TimeZone timeZone;
  private final int maxMovements;
  private final Map<TimePeriod, Integer> movementsX;
  private final Map<TimePeriod, Integer> movementsY;

  @SuppressWarnings("LeakingThisInConstructor")
  public SleepDataTimeSeries(final String name, final SleepData sleepData,
      final Class<?> timePeriodClass, final int maxMovements)
  {
    super(name);
    this.maxMovements = maxMovements;

    movementsX = new HashMap<>();
    movementsY = new HashMap<>();

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

  public int getMaxMovements()
  {
    return maxMovements;
  }

  private void addMovementData(MovementData data)
  {
    // get time of movement
    final RegularTimePeriod timePeriod = RegularTimePeriod.createInstance(
        getTimePeriodClass(), data.getTimestamp(), timeZone);

    int x;
    Integer xOld;
    if ((x = data.getMovementsX()) > 0)
    {
      if ((xOld = movementsX.get(timePeriod)) != null)
      {
        x += xOld;
      }
      movementsX.put(timePeriod, x);
    }

    int y;
    Integer yOld;
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
