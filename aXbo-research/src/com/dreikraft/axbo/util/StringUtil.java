package com.dreikraft.axbo.util;

/**
 * Class with utility functions for button creation.
 * <pre>
 *            ______   ___
 *           (___   | /  /
 *            ___|  |/  /
 * (c) 2007  (___      (  3Kraft Software Applications Development
 *            ___|  |\  \
 *           (______| \__\
 *
 * Phone: +43 (1) 920 45 49                  http://www.3kraft.com
 *
 * $Id: StringUtil.java,v 1.3 2008-02-10 22:56:06 illetsch Exp $
 * </pre>
 *
 *
 * @author  3Kraft - Florian Heinisch
 **/
public class StringUtil
{
  
  /** Creates a new instance of StringUtil */
  public StringUtil()
  {
  }
  
  public static void addNewLine(StringBuilder sb)
  {
    if(sb.length() > 0)
    {
      sb.append("\n");
    }
  }
  
  public static boolean isEmpty(String s) {
    return (s == null || s.equals("") || s.length() < 1) ? true : false;
  }

  public static String rpad(String s, char c, int len)
  {
    if (s.length() == len)
      return s;
    if (s.length() > len) 
      return s.substring(0, len);
    char[] cs = new char[len - s.length()];
    for (char c1: cs)
      c1 = c;
    return s + new String(cs);
  }
}
