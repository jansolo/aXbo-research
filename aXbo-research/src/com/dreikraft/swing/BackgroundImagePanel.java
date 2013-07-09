package com.dreikraft.swing;

import java.awt.Graphics;
import java.awt.Image;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackgroundImagePanel
 *
 * @author jan.illetschko@3kraft.com
 */
public class BackgroundImagePanel extends javax.swing.JPanel {

  private static final Log log = LogFactory.getLog(BackgroundImagePanel.class);
  private boolean scale;
  private String backgroundImageFilename;
  private transient Image backgroundImage;

  /**
   * Creates new form BackgroundImagePanel
   */
  public BackgroundImagePanel() {
    initComponents();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (backgroundImage != null) {
      if (isScale()) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
      } else {
        int width = backgroundImage.getWidth(this);
        int height = backgroundImage.getHeight(this);
        int xStart = (int) (((double) (getWidth() - width)) / 2.0);
        int yStart = (int) (((double) (getHeight() - height)) / 2.0);

        g.drawImage(backgroundImage, xStart, yStart, width, height, this);
      }
      paintChildren(g);
    }
  }

  public boolean isScale() {
    return scale;
  }

  public void setScale(boolean scale) {
    this.scale = scale;
  }

  public String getBackgroundImageFilename() {
    return backgroundImageFilename;
  }

  public void setBackgroundImageFilename(String backgroundImageFilename) {
    this.backgroundImageFilename = backgroundImageFilename;
    try {
      backgroundImage = javax.imageio.ImageIO.read(getClass().getResource(
          backgroundImageFilename));
    } catch (java.io.IOException ex) {
      log.error("can not load background image", ex);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
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
