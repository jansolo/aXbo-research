package com.dreikraft.axbo.util;

/**
 * Utility methods for handling bytes and byte arrays.
 *
 * @author jan.illetschko@3kraft.com
 */
public class ByteUtil {

  /**
   * Calculates the checksum of an byte array.
   *
   * @param data the byte array
   * @param begin the start index
   * @param end the end index
   * @return the calculated checksum
   */
  public static int calcChecksum(byte[] data, int begin, int end) {
    // sum content bytes
    int sum = 0;
    for (int i = begin; i < end; i++) {
      sum += byteToInt(data[i]);
    }
    return sum;
  }

  /**
   * Converts a unsigned byte to an signed int value.
   *
   * @param b a byte
   * @return the byte as int value
   */
  public static int byteToInt(byte b) {
    return (int) b & 0x00000000FF;
  }

  /**
   * Gets the lower 4 bits of a unsigned byte value.
   *
   * @param b a unsigned byte (as int)
   * @return the value of the lower 4 bits
   */
  public static int lowerNibble(int b) {
    return b & 0x0000000F;
  }

  /**
   * Gets the higher 4 bits of a unsigned byte value.
   *
   * @param b a unsigned byte (as int)
   * @return the value of the higher 4 bits
   */
  public static int upperNibble(int b) {
    // shift right 4 bits
    return b >> 4;
  }

  /**
   * Gets the low byte of an integer.
   *
   * @param b the int value
   * @return the lowest 8 bit
   */
  public static byte lowByte(int b) {
    return (byte) (b & 0x000000FF);
  }

  /**
   * Gets the high byte of an integer.
   *
   * @param b the int value
   * @return the right shifted (8-bits) byte value
   */
  public static byte highByte(int b) {
    return (byte) (b >> 8 & 0x000000FF);
  }

  /**
   * Dumps a single byte into a hex string.
   *
   * @param val a signed byte
   * @return a byte as hex string
   */
  public static String dumpByte(byte val) {

    return String.format("%02X", byteToInt(val));
  }

  /**
   * Dumps a single byte into a hex string.
   *
   * @param val a unsigned byte
   * @return a byte as hex string
   */
  public static String dumpByte(int val) {

    return String.format("%02X", val);
  }

  /**
   * Dumps a byte array into a hex string (e.g CB 2A FF ...).
   *
   * @param data the byte array
   * @return a hex string
   */
  public static String dumpByteArray(byte[] data) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      s.append(String.format("%02X", byteToInt(data[i]))).append(" ");
    }
    return s.toString();
  }

  /**
   * Dumps a int array into a hex string (e.g CB 2A FF ...).
   *
   * @param data the byte array
   * @return a hex string
   */
  public static String dumpByteArray(int[] data) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      s.append(String.format("%02X", data[i])).append(" ");
    }
    return s.toString();
  }

  /**
   * Converts a hex number into a decimal number.
   *
   * @param data the digits of a hex number
   * @return the decimal number
   */
  public static int hexToDec(int[] data) {
    int dec = 0;
    for (int i = 0; i < data.length; i++) {
      dec += (int) Math.pow(256, data.length - 1 - i) * data[i];
    }
    return dec;
  }
}