package com.dreikraft.axbo.util;

import java.awt.Window;

/**
 * GuiUtils
 */
public class GuiUtils {

  /**
   * Prevents instantiation.
   */
  private GuiUtils() {
  }

  /**
   * Positions a dialog above the center of a window.
   *
   * @param parent the parent window
   * @param dialog a dialog to center
   */
  public static void center(Window parent, Window dialog) {
    java.awt.Dimension d = dialog.getSize();
    java.awt.Rectangle r = parent.getBounds();

    int x = r.x + (r.width / 2 - d.width / 2);
    int y = r.y + (r.height / 2 - d.height / 2);
    int width = d.width;
    int height = d.height;

    dialog.setBounds(x, y, width, height);
  }
}
