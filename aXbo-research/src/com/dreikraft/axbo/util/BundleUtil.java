/*
 * $Id: BundleUtil.java,v 1.1.1.1 2007-06-11 13:52:33 illetsch Exp $
 * Copyright 3kraft May 2, 2007
 */
package com.dreikraft.axbo.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class BundleUtil
{
  public static final Log log = LogFactory.getLog(BundleUtil.class);
  
  public static final String DEFAULT_BUNDLE = "resources.default";
  public static final String ERRORS_BUNDLE = "resources.errors";

  
  public static ResourceBundle getResourceBundle(String bundle)
  {
    return ResourceBundle.getBundle(bundle);
  }
  
  public static ResourceBundle getDefaultBundle()
  {
    return ResourceBundle.getBundle(DEFAULT_BUNDLE);
  }
  
  public static ResourceBundle getErrorsBundle()
  {
    return ResourceBundle.getBundle(ERRORS_BUNDLE);
  }
  
  public static String getMessage(String key, Object... arguments) 
  {
    return MessageFormat.format(getDefaultBundle().getString(key), arguments);
  }    

  public static String getErrorMessage(String key, Object... arguments) 
  {
    return MessageFormat.format(getErrorsBundle().getString(key), arguments);
  }   
}
