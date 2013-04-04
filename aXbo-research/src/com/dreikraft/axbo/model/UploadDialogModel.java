/*
 * © 2008 3kraft
 * $Id: UploadDialogModel.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.model;

import com.dreikraft.axbo.util.ReflectUtil;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * $Id: UploadDialogModel.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class UploadDialogModel implements Serializable
{
  public static final String OVERALL_MSG_PROPERTY = "overallMsg";
  public static final String OVERALL_SIZE_PROPERTY = "overallSize";
  public static final String OVERALL_VALUE_PROPERTY = "overallValue";
  public static final String DETAIL_MSG_PROPERTY = "detailMsg";
  public static final String DETAIL_SIZE_PROPERTY = "detailSize";
  public static final String DETAIL_VALUE_PROPERTY = "detailValue";
  
  private String overallMsg = "---";
  private int overallSize = 0;
  private int overallValue = 0;
  private String detailMsg = "---";
  private int detailSize = 0;
  private int detailValue = 0;
  
  private transient PropertyChangeSupport propertyChangeSupport;

  public UploadDialogModel()
  {
    super();
    this.propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public UploadDialogModel(String overallMsg, int overallSize, int overallValue,
      String detailMsg, int detailSize, int detailValue)
  {
    this();
    this.overallMsg = overallMsg;
    this.overallSize = overallSize;
    this.overallValue = overallValue;
    this.detailMsg = detailMsg;
    this.detailSize = detailSize;
    this.detailValue = detailValue;
  }

  public String getOverallMsg()
  {
    return overallMsg;
  }

  public void setOverallMsg(String overallMsg)
  {
    String oldOverallMsg = getOverallMsg();
    this.overallMsg = overallMsg;
    propertyChangeSupport.firePropertyChange(OVERALL_MSG_PROPERTY, oldOverallMsg,
        overallMsg);
  }

  public int getOverallSize()
  {
    return overallSize;
  }

  public void setOverallSize(int overallSize)
  {
    int oldOverallValue = getOverallValue();
    this.overallSize = overallSize;
    propertyChangeSupport.firePropertyChange(OVERALL_SIZE_PROPERTY,
        oldOverallValue,
        overallSize);
  }

  public int getOverallValue()
  {
    return overallValue;
  }

  public void setOverallValue(int overallValue)
  {
    int oldOverallValue = getOverallValue();
    this.overallValue = overallValue;
    propertyChangeSupport.firePropertyChange(OVERALL_VALUE_PROPERTY,
        oldOverallValue,
        overallValue);
  }

  public String getDetailMsg()
  {
    return detailMsg;
  }

  public void setDetailMsg(String detailMsg)
  {
    String oldDetailMsg = getDetailMsg();
    this.detailMsg = detailMsg;
    propertyChangeSupport.firePropertyChange(DETAIL_MSG_PROPERTY, oldDetailMsg,
        detailMsg);
  }

  public int getDetailSize()
  {
    return detailSize;
  }

  public void setDetailSize(int detailSize)
  {
    int oldDetailSize = getDetailSize();
    this.detailSize = detailSize;
    propertyChangeSupport.firePropertyChange(DETAIL_SIZE_PROPERTY, oldDetailSize,
        detailSize);
  }

  public int getDetailValue()
  {
    return detailValue;
  }

  public void setDetailValue(int detailValue)
  {
    int oldDetailValue = getDetailValue();
    this.detailValue = detailValue;
    propertyChangeSupport.firePropertyChange(DETAIL_VALUE_PROPERTY,
        oldDetailValue,
        detailValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public void reset()
  {
    setOverallMsg("---");
    setOverallSize(11);
    setOverallValue(0);
    setDetailMsg("---");
    setDetailValue(0);
  }  
  
  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }
}
