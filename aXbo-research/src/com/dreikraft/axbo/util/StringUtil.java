package com.dreikraft.axbo.util;

/**
 * StringUtil
 *
 * @author jan.illetschko@3kraft.com
 */
public class StringUtil {

  private StringUtil() {
  }

  public static void addNewLine(StringBuilder sb) {
    if (sb.length() > 0) {
      sb.append("\n");
    }
  }

  public static boolean isEmpty(String s) {
    return (s == null || s.equals("") || s.length() < 1) ? true : false;
  }

  public static String rpad(String s, char c, int len) {
    if (s.length() == len)
      return s;
    if (s.length() > len)
      return s.substring(0, len);
    char[] cs = new char[len - s.length()];
    for (int i = 0; i < cs.length; i++) {
      cs[i] = c;
    }
    return s + new String(cs);
  }
}
