package com.dreikraft.axbo.sound;

import com.dreikraft.axbo.util.ReflectUtil;
import java.util.Arrays;

/**
 * Sound
 *
 * @author jan.illetschko@3kraft.com
 */
public class Sound {

  private Integer id;
  private String name;
  private SoundFile axboFile;
  private transient boolean playing = false;
  private transient int startPage;
  private transient int pageCount;
  private transient byte[] data;

  /**
   * Creates a new instance of Sound
   */
  public Sound() {
  }

  public Sound(final Integer id, final String name, final SoundFile axboFile) {
    this.id = id;
    this.name = name;
    this.axboFile = axboFile;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SoundFile getAxboFile() {
    return axboFile;
  }

  public void setAxboFile(SoundFile axboFile) {
    this.axboFile = axboFile;
  }

  @Override
  public String toString() {
    return ReflectUtil.toString(this);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isPlaying() {
    return playing;
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
  }

  public int getStartPage() {
    return startPage;
  }

  public void setStartPage(int startPage) {
    this.startPage = startPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public byte[] getData() {
    return data != null ? Arrays.copyOf(data, data.length) : new byte[0];
  }

  public void setData(byte[] data) {
    this.data = data != null ? Arrays.copyOf(data, data.length) : new byte[0];
  }
}
