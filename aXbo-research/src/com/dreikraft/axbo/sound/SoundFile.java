package com.dreikraft.axbo.sound;

import java.io.File;

/**
 * SoundFile
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundFile {

  private String path;
  private SoundType type;

  /**
   * Creates a new instance of SoundFile
   */
  public SoundFile() {
    this(null, SoundType.uLaw);
  }

  public SoundFile(String filename, SoundType soundType) {
    this.path = filename;
    this.type = soundType;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String filename) {
    this.path = filename;
  }

  public SoundType getType() {
    return type;
  }

  public void setType(SoundType soundType) {
    this.type = soundType;
  }

  @Override
  public String toString() {
    // return file name without path information
    return extractName();
  }

  // Extracts file name of absolute path
  public String extractName() {
    if (getPath() != null)
      return getPath().substring(getPath().lastIndexOf(File.separator) + 1,
          getPath().length());
    else
      return getPath();
  }
}
