package com.dreikraft.axbo.sound;

import com.dreikraft.axbo.util.ReflectUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SoundPackage
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundPackage {

  public static final String PACKAGE_INFO_FILENAME = "package-info.xml";
  public static final String FILE_EXT = ".axs";
  public static final String FILE_PATTERN = "^.*\\.axs$";

  public static enum SERIAL_NUMBER {

    GLOBAL, NONE
  };
  private transient File packageFile;
  private String name;
  private String serialNumber;
  private boolean securityEnforced;
  private List<Sound> sounds;
  private String creator;
  private Date creationDate;

  /**
   * Creates a new instance of SoundPackage
   */
  public SoundPackage() {
    securityEnforced = true;
    sounds = new ArrayList<Sound>(11);
  }

  public SoundPackage(String name, String serialNumber,
      boolean securityEnforced,
      List<Sound> sounds) {
    this.name = name;
    this.serialNumber = serialNumber;
    this.securityEnforced = securityEnforced;
    this.sounds = sounds;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public boolean isSecurityEnforced() {
    return securityEnforced;
  }

  public void setSecurityEnforced(boolean securityEnforced) {
    this.securityEnforced = securityEnforced;
  }

  public List<Sound> getSounds() {
    return sounds;
  }

  public void setSounds(List<Sound> sounds) {
    this.sounds = sounds;
  }

  @Override
  public String toString() {
    return ReflectUtil.toString(this);
  }

  public File getPackageFile() {
    return packageFile;
  }

  public void setPackageFile(File packageFile) {
    this.packageFile = packageFile;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Date getCreationDate() {
    return creationDate != null ? new Date(creationDate.getTime()) : null;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate != null ? new Date(creationDate.getTime())
        : null;
  }
}
