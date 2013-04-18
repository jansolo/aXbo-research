package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import java.io.File;

/**
 * SoundPackageUpload
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundPackageUpload extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final File soundPackageFile;

  public SoundPackageUpload(final Object source, final File soundPackageFile) {
    super(source);
    this.soundPackageFile = soundPackageFile;
  }

  public File getSoundPackageFile() {
    return soundPackageFile;
  }
}
