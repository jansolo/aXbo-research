package com.dreikraft.axbo.data;

import com.dreikraft.axbo.util.ReflectUtil;

/**
 * AxboInfo.
 *
 * @author jan.illetschko@3kraft.com
 */
public class AxboInfo {

  final private String serialNumber;
  final private String hardwareVersion;
  final private String softwareVersion;
  final private String rtcCalibration;

  public AxboInfo(final String serialNumber, final String hardwareVersion,
      final String softwareVersion, final String rtcCalibration) {
    this.serialNumber = serialNumber;
    this.hardwareVersion = hardwareVersion;
    this.softwareVersion = softwareVersion;
    this.rtcCalibration = rtcCalibration;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public String getHardwareVersion() {
    return hardwareVersion;
  }
  public String getSoftwareVersion() {
    return softwareVersion;
  }

  public String getRtcCalibration() {
    return rtcCalibration;
  }

  @Override
  public String toString() {
    return ReflectUtil.toString(this);
  }
}
