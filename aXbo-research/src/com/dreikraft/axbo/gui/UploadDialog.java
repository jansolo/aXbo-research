/*
 * © 2008 3kraft
 * $Id: UploadDialog.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 */
package com.dreikraft.axbo.gui;

import com.dreikraft.axbo.model.UploadDialogModel;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;

/**
 * $Id: UploadDialog.java,v 1.2 2008-05-13 15:08:43 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class UploadDialog extends javax.swing.JDialog
{
  /** Creates new form UploadDialg */
  public UploadDialog(java.awt.Frame parent, boolean modal)
  {
    super(parent, modal);
    setResizable(false);

    // mac os laf
    //getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);   
    getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
    getRootPane().putClientProperty("Window.style", "small");
    getRootPane().putClientProperty("Window.alpha", new Float(0.9));

    initComponents();
  }

  public void updateProgress(final PropertyChangeEvent evt)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if (evt.getPropertyName().equals(UploadDialogModel.OVERALL_MSG_PROPERTY))
        {
          lblSoundPackageNameValue.setText((String) evt.getNewValue());
        }
        else if (evt.getPropertyName().equals(UploadDialogModel.OVERALL_SIZE_PROPERTY))
        {
          pgbrOverallProgress.setMinimum(0);
          pgbrOverallProgress.setMaximum((Integer) evt.getNewValue());
        }
        else if (evt.getPropertyName().equals(UploadDialogModel.OVERALL_VALUE_PROPERTY))
        {
          Integer val = (Integer) evt.getNewValue();
          pgbrOverallProgress.setValue(val);
          if (pgbrOverallProgress.getMaximum() == val)
          {
            btnReady.setEnabled(true);
          }
          else
          {
            btnReady.setEnabled(false);
          }
        }
        else if (evt.getPropertyName().equals(UploadDialogModel.DETAIL_MSG_PROPERTY))
        {
          lblDetailStatusValue.setText((String) evt.getNewValue());
        }
        else if (evt.getPropertyName().equals(UploadDialogModel.DETAIL_SIZE_PROPERTY))
        {
          pgbrDetailProgress.setMinimum(0);
          pgbrDetailProgress.setMaximum((Integer) evt.getNewValue());
        }
        else if (evt.getPropertyName().equals(UploadDialogModel.DETAIL_VALUE_PROPERTY))
        {
          pgbrDetailProgress.setValue((Integer) evt.getNewValue());
        }
      }
    });
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    lblSoundPackageName = new javax.swing.JLabel();
    lblSoundPackageNameValue = new javax.swing.JLabel();
    pgbrOverallProgress = new javax.swing.JProgressBar();
    lblDetailStatus = new javax.swing.JLabel();
    lblDetailStatusValue = new javax.swing.JLabel();
    pgbrDetailProgress = new javax.swing.JProgressBar();
    btnReady = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/default"); // NOI18N
    setTitle(bundle.getString("uploadDialog.title")); // NOI18N
    setName("UploadDialog"); // NOI18N
    setResizable(false);

    lblSoundPackageName.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    lblSoundPackageName.setText(bundle.getString("label.soundPackageName")); // NOI18N

    lblSoundPackageNameValue.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    lblSoundPackageNameValue.setText("---");

    lblDetailStatus.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    lblDetailStatus.setText(bundle.getString("label.detailProgress")); // NOI18N

    lblDetailStatusValue.setFont(new java.awt.Font("Lucida Grande", 0, 10));
    lblDetailStatusValue.setText("---");

    btnReady.setText(bundle.getString("button.uploadClose")); // NOI18N
    btnReady.setEnabled(false);
    btnReady.putClientProperty("JButton.buttonType", "textured");
    btnReady.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        btnReadyActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(layout.createSequentialGroup()
            .add(lblDetailStatus)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lblDetailStatusValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
            .addContainerGap())
          .add(layout.createSequentialGroup()
            .add(lblSoundPackageName)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lblSoundPackageNameValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
            .addContainerGap())
          .add(layout.createSequentialGroup()
            .add(pgbrOverallProgress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
            .addContainerGap())
          .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(org.jdesktop.layout.GroupLayout.LEADING, btnReady, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
              .add(pgbrDetailProgress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
            .addContainerGap())))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblSoundPackageName)
          .add(lblSoundPackageNameValue))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pgbrOverallProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblDetailStatus)
          .add(lblDetailStatusValue))
        .add(6, 6, 6)
        .add(pgbrDetailProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnReady)
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void btnReadyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnReadyActionPerformed
  {//GEN-HEADEREND:event_btnReadyActionPerformed
    setVisible(false);
}//GEN-LAST:event_btnReadyActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        UploadDialog dialog = new UploadDialog(new javax.swing.JFrame(), true);
        dialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
          public void windowClosing(java.awt.event.WindowEvent e)
          {
            System.exit(0);
          }
        });
        dialog.setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnReady;
  private javax.swing.JLabel lblDetailStatus;
  private javax.swing.JLabel lblDetailStatusValue;
  private javax.swing.JLabel lblSoundPackageName;
  private javax.swing.JLabel lblSoundPackageNameValue;
  private javax.swing.JProgressBar pgbrDetailProgress;
  private javax.swing.JProgressBar pgbrOverallProgress;
  // End of variables declaration//GEN-END:variables
}
