/*
 * ObjectUtil.java
 *
 * Created on 09. Mai 2007, 14:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.dreikraft.axbo.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jan_solo
 */
public class ReflectUtil
{
  public static final Log log = LogFactory.getLog(ReflectUtil.class);
  
  public static String toString(Object bean)
  {
    String s = "";
    try
    {
      s = BeanUtils.describe(bean).toString();
    }
    catch (Exception ex)
    {
      log.error(ex);
    }
    return s;
  }
}
