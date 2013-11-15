/*
 * © 2008 3kraft
 * $Id: KeyTimeSeries.java,v 1.8 2010-12-17 10:11:40 illetsch Exp $
 */
package com.dreikraft.axbo.timeseries;

import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
import java.util.TimeZone;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class KeyTimeSeries extends TimeSeries
{
  private final TimeZone timeZone = TimeZone.getDefault();
  private final SleepData sleepData;
  private final int keyType;

  @SuppressWarnings("LeakingThisInConstructor")
  public KeyTimeSeries(final String name, final SleepData sleepData,
      final Class<?> timePeriodClass, final int keyType)
  {
    super(name);
    this.sleepData = sleepData;
    this.timePeriodClass = timePeriodClass;
    this.keyType = keyType;

    for (MovementData movement : sleepData.getMovements())
    {
      addMovementData(movement);
    }
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }

  public int getKeyType()
  {
    return keyType;
  }

  private void addMovementData(final MovementData data)
  {
    if (data.getMovementsZ() == keyType)
    {
      // get time of movement
      final RegularTimePeriod timePeriod = RegularTimePeriod.createInstance(
          getTimePeriodClass(), data.getTimestamp(), timeZone);

        delete(timePeriod);
      add(new TimeSeriesDataItem(timePeriod, 1));
    }
  }
}
