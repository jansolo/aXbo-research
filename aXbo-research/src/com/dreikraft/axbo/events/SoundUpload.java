package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SoundUpload
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundUpload extends ApplicationEvent {

  final private String soundName;

  public SoundUpload(final Object source, final String soundName) {
    super(source);
    this.soundName = soundName;
  }

  public String getSoundName() {
    return soundName;
  }
}
