/*
 * © 2008 3kraft
 * $Id: UploadDialogController.java,v 1.3 2010-11-29 15:42:24 illetsch Exp $
 */
package com.dreikraft.axbo.controller;

import com.dreikraft.axbo.gui.GuiUtils;
import com.dreikraft.axbo.gui.UploadDialog;
import com.dreikraft.axbo.model.UploadDialogModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id: UploadDialogController.java,v 1.3 2010-11-29 15:42:24 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.3 $
 */
public class UploadDialogController implements PropertyChangeListener {
  
  public static final Log log = LogFactory.getLog(UploadDialogController.class);
  
  private UploadDialog uploadDialog;
  private AxboFrameController parentCtrl;  
  private UploadDialogModel model;
  
  public UploadDialogController(AxboFrameController parentCtrl, 
      UploadDialogModel model)
  {
    this.parentCtrl = parentCtrl;
    this.model = model;
    model.addPropertyChangeListener(this);
//    this.uploadDialog = new UploadDialog(parentCtrl.getFrame(), true);
  }
  
  public void show()
  {    
//    GuiUtils.center(parentCtrl.getFrame(), uploadDialog);
    uploadDialog.setVisible(true);
  }

  public void propertyChange(PropertyChangeEvent evt)
  {
    uploadDialog.updateProgress(evt);
  }

  void hide()
  {
    uploadDialog.setVisible(false);
  }
}
