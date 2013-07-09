package com.dreikraft.axbo;

/**
 * Supported operating systems.
 *
 * @author jan.illetschko@3kraft.com
 */
public enum OS implements Arch {

  /**
   * Mac OS X.
   */
  Mac("/dev/tty.SLAB_USBtoUART") {
    /**
     * All Mac OSX architecture variants are supported (ppc, x86, x86_64)
     */
    @Override
    public boolean isSupported() {
      return true;
    }
  },
  /**
   * All Windows variants.
   */
  Windows("COM1") {
    /**
     * The native rxtx libraries (2.2pre2) currently only support 32-bit
     * libraries on windows.
     */
    @Override
    public boolean isSupported() {
      return BITS.BITS_32.toString().equals(osBits);
    }
  },
  /**
   * Linux.
   */
  Linux("/dev/ttyUSB0") {
    /**
     * The native rxtx libraries (2.2pre2) currently only support 32-bit
     * libraries on linux.
     */
    @Override
    public boolean isSupported() {
      return BITS.BITS_32.toString().equals(osBits);
    }
  },
  /**
   * Anything else.
   */
  Unsupported("None") {
    @Override
    public boolean isSupported() {
      return false;
    }
  };

  /**
   * 32- or 64-bit.
   */
  enum BITS {

    /**
     * 32-bit.
     */
    BITS_32("32"),
    /**
     * 64-bit.
     */
    BITS_64("64"),
    /**
     * Bits unknown.
     */
    BITS_UNKNOWN("unknown");
    private String bits;

    private BITS(final String bits) {
      this.bits = bits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return bits;
    }
  }
  private static final String OS_NAME_PROPERTY = "os.name";
  private static final String OS_BITS_PROPERTY = "sun.arch.data.model";
  private static final String osName = System.getProperty(OS_NAME_PROPERTY);
  private static final String osBits = System.getProperty(OS_BITS_PROPERTY);
  private final String defaultPort;

  private OS(final String defaultPort) {
    this.defaultPort = defaultPort;
  }

  /**
   * Returns the default serial port name for this operating system.
   *
   * @return
   */
  public String getDefaultPort() {
    return defaultPort;
  }

  /**
   * Determines the current operating system.
   *
   * @return the current operating system or Unsupported.
   */
  public static OS get() {
    if (OS.Mac.isCurrent()) {
      return Mac;
    } else if (OS.Windows.isCurrent()) {
      return Windows;
    } else if (OS.Linux.isCurrent()) {
      return Linux;
    }
    return Unsupported;
  }

  /**
   * Checks if the current operating system matches this os instance.
   *
   * @return true if the OS matches the current operating system.
   */
  public boolean isCurrent() {
    return osName.startsWith(name());
  }
}
