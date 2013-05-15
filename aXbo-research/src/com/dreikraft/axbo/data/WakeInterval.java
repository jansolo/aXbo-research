package com.dreikraft.axbo.data;

/**
 * Enumeration for possible wake intervals.
 * 
 * @author jan.illetschko@3kraft.com
 */
public enum WakeInterval {

  SHORT(15l * 60 * 1000, AxboResponseProtocol.WAKE_INTERVAL_START),
  LONG(30l * 60 * 1000, AxboResponseProtocol.WAKE_INTERVAL_SHORT);
  private long time;
  private AxboResponseProtocol protocol;

  private WakeInterval(final long time, final AxboResponseProtocol protocol) {
    this.time = time;
    this.protocol = protocol;
  }

  public long getTime() {
    return time;
  }

  public AxboResponseProtocol getProtocol() {
    return protocol;
  }

  public static WakeInterval getWakeIntervalFromProtocol(
      final AxboResponseProtocol protocol) {
    for (final WakeInterval wakeInterval : values()) {
      if (protocol.equals(wakeInterval.getProtocol()))
        return wakeInterval;
    }
    return WakeInterval.LONG;
  }
}