package com.dreikraft.axbo.events;

import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.util.ReflectUtil;

/**
 * MovementEvent
 *
 * @author jan.illetschko@3kraft.com
 */
public final class MovementEvent extends ApplicationEvent implements
    Comparable<MovementEvent> {

  /**
   * SerialVersionUID.
   */
  public static final long serialVersionUID = 1L;
  private final MovementData movementData;
  private final String id;
  private final String cmd;

  public MovementEvent(final Object source, final MovementData movementData,
      final String id, String cmd) {
    super(source);
    this.movementData = movementData;
    this.id = id;
    this.cmd = cmd;
  }

  public MovementData getMovementData() {
    return this.movementData;
  }

  public String getId() {
    return this.id;
  }

  public String getCmd() {
    return cmd;
  }

  @Override
  public String toString() {
    return ReflectUtil.toString(this);
  }

  @Override
  public int compareTo(MovementEvent evt) {
    if (this.getMovementData().getTimestamp().equals(
        evt.getMovementData().getTimestamp()))
      return 0;
    if (this.getMovementData().getTimestamp().before(
        evt.getMovementData().getTimestamp()))
      return -1;
    else
      return 1;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof MovementEvent && ((MovementEvent) obj)
        .getMovementData() != null && getMovementData() != null
        && getMovementData().getTimestamp().equals(((MovementEvent) obj)
        .getMovementData().getTimestamp());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash =
        13 * hash + (this.movementData != null && this.movementData
        .getTimestamp() != null ? this.movementData.getTimestamp().hashCode()
        : 0);
    return hash;
  }
}