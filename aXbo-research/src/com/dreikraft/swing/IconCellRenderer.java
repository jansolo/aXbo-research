package com.dreikraft.swing;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * IconCellRenderer
 *
 * @author jan.illetschko@3kraft.com
 */
public class IconCellRenderer extends DefaultTableCellRenderer {

  private String iconUrl;
  private String val;

  public IconCellRenderer(String iconUrl) {
    this(iconUrl, null);
  }

  public IconCellRenderer(String iconUrl, String val) {
    this.iconUrl = iconUrl;
    this.val = val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus,
      int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
        column);
    if (iconUrl != null && (val == null || value.equals(val))) {
      ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(
          iconUrl));
      setIcon(icon);
    } else {
      setIcon(null);
    }
    return this;
  }
}
