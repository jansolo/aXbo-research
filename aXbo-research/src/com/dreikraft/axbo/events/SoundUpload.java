package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;

/**
 * SoundUpload
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundUpload extends ApplicationEvent {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final String soundName;

  public SoundUpload(final Object source, final String soundName) {
    super(source);
    this.soundName = soundName;
  }

  public String getSoundName() {
    return soundName;
  }
}
