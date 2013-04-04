/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreikraft.swing;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author jan_solo
 */
public class IconCellRenderer extends DefaultTableCellRenderer
{
  private String iconUrl;
  private String val;
  
  public IconCellRenderer(String iconUrl) 
  {
    this(iconUrl, null);
  }
  
  public IconCellRenderer(String iconUrl, String val) 
  {
    this.iconUrl = iconUrl;
    this.val = val;
  }
  
  /*
   * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
   */
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus,
      int row, int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (iconUrl != null && (val == null || value.equals(val))) 
    {
      ImageIcon icon = new ImageIcon(getClass().getResource(iconUrl));
      setIcon(icon);
    }
    else
    {
      setIcon(null);
    }
    return this;
  }

  
}
