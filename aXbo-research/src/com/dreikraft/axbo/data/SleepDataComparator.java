package com.dreikraft.axbo.data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the starting hours of two sleep records.
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataComparator implements Comparator<SleepData>,
    Serializable {
  
  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(SleepData s1, SleepData s2) {
    if (s1 == null || s2 == null) {
      throw new IllegalArgumentException("compare value may not be null");
    }
    if (s1.getCompareStartHour() == s2.getCompareStartHour()) {
      return 0;
    }
    if (s1.getCompareStartHour() < s2.getCompareStartHour()) {
      return -1;
    } else {
      return 1;
    }
  }
}
