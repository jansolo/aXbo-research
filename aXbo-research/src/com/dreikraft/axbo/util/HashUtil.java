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
 * $Id: HashUtil.java,v 1.1 2007-08-31 20:49:19 heinisch Exp $
 * </pre>
 *
 *
 * @author  3Kraft - Florian Heinisch
 **/
public class HashUtil
{
  
  /** Creates a new instance of HashUtil */
  public HashUtil()
  {
  }
  
  public static String generateHash(String serialNumber)
  {
    String hash = "";
    if(serialNumber != null)
    {
      hash = "This hash is empty";
    }
    return hash;
  }
  
}
