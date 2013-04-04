/*
 * © 2008 3kraft
 * $Id: AxboResponseProtocol.java,v 1.4 2010-12-14 14:41:29 illetsch Exp $
 */
package com.dreikraft.axbo.data;

/**
 * $Id: AxboResponseProtocol.java,v 1.4 2010-12-14 14:41:29 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.4 $
 */
public enum AxboResponseProtocol
{
  KEY('T'), BEGIN('B'), NEXT('N'), PERSON_CHANGE('P'), SENSOR_SLEEP('S'),
  WAKE('W'), DEFAULT('D'), GOOD_WAKE('G'), RANDOM_WAKE('R'),
  WAKE_INTERVALL_START('Z'), CHILLOUT('C'), SOUNDFILE('F'), POWER_NAPPING('K'),
  END('X'), SNOOZE('I');
  
  private char letter;
  
  private AxboResponseProtocol(char letter) 
  {
    this.letter = letter;
  }
  
  public char getLetter()
  {
    return letter;
  }

  public int getLetterAsInt()
  {
    return (int)letter;
  }

  public String getLetterAsString()
  {
    return String.valueOf(letter);
  }
  
  public static AxboResponseProtocol valueOfLetter(String letter)
  {
    for (AxboResponseProtocol arp: AxboResponseProtocol.values())
    {
      if (arp.getLetterAsString().equals(letter))
        return arp;
    }
    return AxboResponseProtocol.DEFAULT;
  }
}
