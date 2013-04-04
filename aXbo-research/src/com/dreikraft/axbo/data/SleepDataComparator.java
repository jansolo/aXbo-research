/*
 * © 2008 3kraft
 * $Id: SleepDataComparator.java,v 1.3 2010-12-03 18:10:02 illetsch Exp $
 */
package com.dreikraft.axbo.data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * $Id: SleepDataComparator.java,v 1.3 2010-12-03 18:10:02 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.3 $
 */
public class SleepDataComparator implements Comparator<SleepData>, 
    Serializable {

  @Override
  public int compare(SleepData s1, SleepData s2)
  {
    if (s1 == null || s2 == null)
    {
      throw new IllegalArgumentException("compare value may not be null");
    }
    if (s1.getStartHour() == s2.getStartHour())
    {
      return 0;
    }
    if (s1.getStartHour() < s2.getStartHour())
    {
      return -1;
    }
    else
    {
      return 1;
    }
  }
}
