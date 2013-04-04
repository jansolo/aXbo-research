/*
 * SoundPackageException.java
 *
 * Created on June 20, 2007, 11:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.dreikraft.axbo.sound;

/**
 *
 * @author jan_solo
 */
public class SoundPackageException extends java.lang.Exception
{
  
  /**
   * Creates a new instance of <code>SoundPackageException</code> without detail
   * message.
   */
  public SoundPackageException()
  {
  }
  
  
  /**
   * Constructs an instance of <code>SoundPackageException</code> with the 
   * specified detail message.
   * @param msg the detail message.
   */
  public SoundPackageException(String msg)
  {
    super(msg);
  }
  
  public SoundPackageException(String msg, Throwable source)
  {
    super(msg, source);
  }

  public SoundPackageException(Throwable source)
  {
    this(source.getMessage(), source);
  }
}
