package com.dreikraft.axbo;

/**
 * Interface for supported cpu architectures for each operating system.
 *
 * @author jan.illetschko@3kraft.com
 */
public interface Arch {

  /**
   * Is the cpu architecture supported by aXbo research.
   *
   * @return true if supported.
   */
  boolean isSupported();
}
