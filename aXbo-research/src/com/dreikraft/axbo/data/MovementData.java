/*
 * © 2008 3kraft
 * $Id: MovementData.java,v 1.18 2010-12-16 23:13:53 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import com.dreikraft.axbo.util.ReflectUtil;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * $Id: MovementData.java,v 1.18 2010-12-16 23:13:53 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.18 $
 */
public class MovementData implements Serializable, Comparable<MovementData>
{
  public static final Log log = LogFactory.getLog(MovementData.class);
  
  public static final int KEY = -1;
  public static final int SNOOZE = -2;
  public static final int EMPTY = 0;
  
  // enum can not be used because of XMLWriter Bug
  public static final int X = 0;
  public static final int Y = 1;
  public static final int Z = 2;
  
  private Date timestamp;
  private int movementsX = 0;
  private int movementsY = 0;
  private int movementsZ = 0;
  
  /** Creates a new instance of MovementData */
  public MovementData()
  {
    super();
  }
  
  public MovementData(Date timestamp, int x, int y, int z)
  {
    this.timestamp = timestamp;
    this.movementsX = x;
    this.movementsY = y;
    this.movementsZ = z;
  }
  
  public java.util.Date getTimestamp()
  {
    return timestamp;
  }
  
  public void setTimestamp(java.util.Date timestamp)
  {
    this.timestamp = timestamp;
  }
  
  public int getMovementsX()
  {
    return movementsX;
  }
  
  public int getMovementsY()
  {
    return movementsY;
  }
  
  public void setMovementsX(int movementsX)
  {
    this.movementsX = movementsX;
  }
  
  public void setMovementsY(int movementsY)
  {
    this.movementsY = movementsY;
  }

  public int getMovementsZ()
  {
    return movementsZ;
  }

  public void setMovementsZ(int movementsZ)
  {
    this.movementsZ = movementsZ;
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof MovementData))
    {
      return this.timestamp.equals(((MovementData)obj).getTimestamp());
    }
    return false;
  }
  
  public int compareTo(MovementData o1)
  {
    if (this.getTimestamp() == null || o1.getTimestamp() == null)
      return 0;
    if (this.equals(o1))
      return 0;
    if (this.getTimestamp().before(o1.getTimestamp()))
      return -1;
    else
      return 1;
  }
  
  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }
}
