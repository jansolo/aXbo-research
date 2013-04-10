package com.dreikraft.axbo.events;

import com.dreikraft.axbo.sound.SoundPackage;
import com.dreikraft.events.ApplicationEvent;

/**
 * SoundPackageUploadComplete
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundPackageUploadComplete extends ApplicationEvent
{
 private SoundPackage soundPackage;

  public SoundPackageUploadComplete(final Object source, 
      final SoundPackage soundPackage)
  {
    super(source);
    this.soundPackage = soundPackage;
  }

  public SoundPackage getSoundPackage() {
    return soundPackage;
  }
}
