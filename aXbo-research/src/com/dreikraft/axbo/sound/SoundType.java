/*
 * SoundType.java
 *
 * Created on June 20, 2007, 6:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.dreikraft.axbo.sound;

/**
 *
 * @author jan_solo
 */
public enum SoundType
{
  uLaw("wav"), PCM("wav"), mp3("mp3");
  
  private String suffix;
  
  private SoundType(String suffix) 
  {
    this.suffix = suffix;
  }
  
  public String getSuffix()
  {
    return this.suffix;
  }
}
