/*
 * © 2008 3kraft
 * $Id: GuiUtils.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 */
package com.dreikraft.axbo.gui;

import java.awt.Window;

/**
 * $Id: GuiUtils.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class GuiUtils
{
  /** Creates a new instance of CenterWindow */
  public static void center(Window parent, Window dialog)
  {
    // Größe des Dialogs
    java.awt.Dimension d = dialog.getSize();
    
    // Größe und Position des Hauptfensters
    java.awt.Rectangle r = parent.getBounds();
    
    int x = r.x + (r.width / 2 - d.width / 2);
    int y = r.y + (r.height / 2 - d.height / 2);
    int width = d.width;
    int height = d.height;
    
    dialog.setBounds(x, y, width, height);
  }
  
}
