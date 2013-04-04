/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BackgroundImagePanel.java
 *
 * Created on Nov 29, 2010, 7:22:04 PM
 */
package com.dreikraft.swing;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author jan_solo
 */
public class BackgroundImagePanel extends javax.swing.JPanel
{

  private boolean scale;
  private Image backgroundImage = null;

  /** Creates new form BackgroundImagePanel */
  public BackgroundImagePanel()
  {
    initComponents();
  }

  @Override
  public void paint(Graphics g)
  {
    super.paint(g);
    if (getBackgroundImage() != null)
    {
      if (isScale())
      {
        g.drawImage(getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
      }
      else
      {
        int width = getBackgroundImage().getWidth(this);
        int height = getBackgroundImage().getHeight(this);
        int xStart = (int) (((double) (getWidth() - width)) / 2.0);
        int yStart = (int) (((double) (getHeight() - height)) / 2.0);

        g.drawImage(getBackgroundImage(), xStart, yStart, width, height, this);
      }
      paintChildren(g);
    }
  }

  public boolean isScale()
  {
    return scale;
  }

  public void setScale(boolean scale)
  {
    this.scale = scale;
  }

  public Image getBackgroundImage()
  {
    return backgroundImage;
  }

  public void setBackgroundImage(Image backgroundImage)
  {
    this.backgroundImage = backgroundImage;
    this.repaint();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
