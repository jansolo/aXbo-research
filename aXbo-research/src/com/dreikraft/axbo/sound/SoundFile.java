/*
 * $Id: SoundFile.java,v 1.6 2008-02-05 14:34:53 illetsch Exp $
 * Copyright 3kraft June 11, 2007
 */
package com.dreikraft.axbo.sound;

import java.io.File;
import java.io.Serializable;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class SoundFile implements Serializable
{
  private String path;
  private SoundType type;
  
  /** Creates a new instance of SoundFile */
  public SoundFile()
  {
    this(null, SoundType.uLaw);
  }
  
  public SoundFile(String filename, SoundType soundType) 
  {
    this.path = filename;
    this.type = soundType;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String filename)
  {
    this.path = filename;
  }

  public SoundType getType()
  {
    return type;
  }

  public void setType(SoundType soundType)
  {
    this.type = soundType;
  }

  @Override
  public String toString() 
  {
    // return file name without path information
    return extractName();
  }
  
  // Extracts file name of absolute path
  public String extractName()
  {
    if (getPath() != null)
      return getPath().substring(getPath().lastIndexOf(File.separator) + 1, 
          getPath().length());
    else
      return getPath();
  }
}
