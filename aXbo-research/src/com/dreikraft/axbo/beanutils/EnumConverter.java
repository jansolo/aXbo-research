/*
 * $Id: EnumConverter.java,v 1.1 2007-06-25 11:51:46 illetsch Exp $
 * Copyright 3kraft June 20, 2007
 */
package com.dreikraft.axbo.beanutils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class EnumConverter implements Converter
{
  
  @Override
  @SuppressWarnings("rawtypes")
  public Object convert(Class type, Object value)
  {
    if (value == null)
    {
      throw new ConversionException("No value specified");
    }
    
    if (type.isAssignableFrom(value.getClass()))
    {
      return value;
    }
    
    return Enum.valueOf(type, value.toString());
  }
  
}
