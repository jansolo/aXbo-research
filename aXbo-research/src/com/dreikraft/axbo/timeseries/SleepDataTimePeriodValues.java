/*
 * © 2008 3kraft
 * $Id: SleepDataTimePeriodValues.java,v 1.5 2010-12-03 18:10:02 illetsch Exp $
 */
package com.dreikraft.axbo.timeseries;

import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;

/**
 * $Id: SleepDataTimePeriodValues.java,v 1.5 2010-12-03 18:10:02 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.5 $
 */
public class SleepDataTimePeriodValues extends TimePeriodValues implements
    PropertyChangeListener
{
  private SleepData sleepData;
  private int movementDirection;
  private int stepIntervall;
  
  public SleepDataTimePeriodValues(String name, SleepData sleepData,
      int movementDirection, int stepIntervall)
  {
    super(name);
    this.sleepData = sleepData;
    this.movementDirection = movementDirection;
    this.stepIntervall = stepIntervall;
    this.sleepData.addPropertyChangeListener(this);
    
    // set start and end time
    TimePeriod startTime = new SimpleTimePeriod(
        sleepData.calculateStartTime().getTime(),
        sleepData.calculateStartTime().getTime() + stepIntervall);
    add(startTime, 0);
    TimePeriod endTime = new SimpleTimePeriod(
        sleepData.calculateEndTime().getTime(),
        sleepData.calculateEndTime().getTime() + stepIntervall);
    add(endTime, 0);

    for (MovementData movement: sleepData.getMovements())
    {
      addMovement(movement);
    }
  }
  
  public SleepData getSleepData()
  {
    return sleepData;
  }
  
  public void setSleepData(SleepData sleepData)
  {
    this.sleepData = sleepData;
  }
  
  public int getMovementDirection()
  {
    return movementDirection;
  }
  
  public void setMovementDirection(int movementDirection)
  {
    this.movementDirection = movementDirection;
  }
  
  public int getStepIntervall()
  {
    return stepIntervall;
  }
  
  public void setStepIntervall(int stepIntervall)
  {
    this.stepIntervall = stepIntervall;
  }
  
  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    MovementData movement = (MovementData)evt.getNewValue();
    addMovement(movement);
  }
  
  private void addMovement(MovementData movement)
  {
    TimePeriod time = new SimpleTimePeriod(
        movement.getTimestamp().getTime(),
        movement.getTimestamp().getTime() + stepIntervall);
    switch (movementDirection)
    {
      case MovementData.X:
        add(time, movement.getMovementsX());
        break;
      case MovementData.Y:
        add(time, movement.getMovementsY());
        break;
      case MovementData.Z:
        add(time, movement.getMovementsZ());
        break;
    }
  }
}
