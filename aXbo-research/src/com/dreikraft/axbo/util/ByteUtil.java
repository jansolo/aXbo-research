// $Id: ByteUtil.java,v 1.4 2009-07-20 11:27:22 illetsch Exp $
package com.dreikraft.axbo.util;

public class ByteUtil
{
  public static int calcChecksum(byte[] data, int begin, int end)
  {
    // sum content bytes
    int sum = 0;
    for (int i = begin; i < end; i++)
    {
      sum += byteToInt(data[i]);
    }
    return sum;
  }

  public static int byteToInt(byte b)
  {
    return (int) b & 0x00000000FF;
  }

  public static int lowerNibble(int b)
  {
    return b & 0x0000000F;
  }

  public static int upperNibble(int b)
  {
    return b >> 4;
  }

  public static byte lowByte(int b)
  {
    return (byte) (b & 0x000000FF);
  }

  public static byte highByte(int b)
  {
    return (byte) (b >> 8 & 0x000000FF);
  }

  public static String dumpByteArray(byte[] data)
  {
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < data.length; i++)
    {
      s.append(String.format("%02X", byteToInt(data[i]))).append(" ");
    }
    return s.toString();
  }

  public static String dumpByteArray(int[] data)
  {
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < data.length; i++)
    {
      s.append(String.format("%02X", data[i])).append(" ");
    }
    return s.toString();
  }

  public static int hexToDec(int[] data) {
    int dec = 0;
    for (int i = 0; i < data.length; i++) {
      dec += (int)Math.pow(256, data.length - 1 - i) * data[i];
    }
    return dec;
  }
}