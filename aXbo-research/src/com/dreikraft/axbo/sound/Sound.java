/*
 * $Id: Sound.java,v 1.7 2008-07-03 17:17:09 illetsch Exp $
 * Copyright 3kraft June 11, 2007
 */
package com.dreikraft.axbo.sound;

import com.dreikraft.axbo.util.ReflectUtil;
import java.io.Serializable;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class Sound implements Serializable
{
  private Integer id;
  private String name;
  private SoundFile axboFile;
  private transient boolean playing = false;
  private transient int startPage;
  private transient int pageCount;
  private transient byte[] data;
  
  /** Creates a new instance of Sound */
  public Sound()
  {
  }

  public Sound(Integer id, String name, SoundFile axboFile)
  {
    setId(id);
    setName(name);
    setAxboFile(axboFile);
  }
  
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public SoundFile getAxboFile()
  {
    return axboFile;
  }

  public void setAxboFile(SoundFile axboFile)
  {
    this.axboFile = axboFile;
  }

  public String toString()
  {
    return ReflectUtil.toString(this);
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public boolean isPlaying()
  {
    return playing;
  }

  public void setPlaying(boolean playing)
  {
    this.playing = playing;
  }

  public int getStartPage()
  {
    return startPage;
  }

  public void setStartPage(int startPage)
  {
    this.startPage = startPage;
  }

  public int getPageCount()
  {
    return pageCount;
  }

  public void setPageCount(int pageCount)
  {
    this.pageCount = pageCount;
  }

  public byte[] getData()
  {
    return data;
  }

  public void setData(byte[] data)
  {
    this.data = data;
  }
}
