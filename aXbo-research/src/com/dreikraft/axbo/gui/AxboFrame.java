/*
 * © 2008 3kraft
 * $Id: AxboFrame.java,v 1.54 2010-12-17 10:11:40 illetsch Exp $
 */
package com.dreikraft.axbo.gui;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationExit;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.events.AxboClear;
import com.dreikraft.axbo.events.AxboDisconnect;
import com.dreikraft.axbo.events.PrefsOpen;
import com.dreikraft.axbo.events.AxboReset;
import com.dreikraft.axbo.events.AxboStatusGet;
import com.dreikraft.axbo.events.AxboTimeSet;
import com.dreikraft.axbo.events.DataSearch;
import com.dreikraft.axbo.events.DiagramClose;
import com.dreikraft.axbo.events.DiagramPrint;
import com.dreikraft.axbo.events.SleepDataCompare;
import com.dreikraft.axbo.events.SleepDataDelete;
import com.dreikraft.axbo.events.SleepDataImport;
import com.dreikraft.axbo.events.SleepDataOpen;
import com.dreikraft.axbo.model.MetaDataTableModel;
import com.dreikraft.axbo.util.BundleUtil;
import com.dreikraft.swing.SplashScreen;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.logging.*;

/**
 * $Id: AxboFrame.java,v 1.54 2010-12-17 10:11:40 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.54 $
 */
public class AxboFrame extends JFrame
{

  private static final String DATA_CARD_NAME = "data";
  private static final String SOUND_CARD_NAME = "sound";
  private static Log log = LogFactory.getLog(AxboFrame.class);
  private SplashScreen splashScreen;

  public void init()
  {
    initComponents();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    modifyColumnWidths();
  }

  public void setSelectView(String view)
  {
    ((CardLayout) mainPanel.getLayout()).show(mainPanel, view);
    ((CardLayout) mainToolbar.getLayout()).show(mainToolbar, view);
  }

  public void jumpToDataView(final DataFrame dataView)
  {
    dataViewsPanel.revalidate();
    SwingUtilities.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        final double dataViewPosY = dataView.getLocation().getY();
        int scrollPosY = (int) dataViewPosY;
        final int dataViewsHeight = dataViewsPanel.getHeight();
        final int viewportHeight = dataScrollPane.getViewport().getHeight();
        if (dataViewsHeight < viewportHeight + dataViewPosY)
        {
          scrollPosY = dataViewsHeight - viewportHeight;
        }
        dataScrollPane.getViewport().setViewPosition(new Point(0, scrollPosY));
      }
    });
  }

  public List<DataFrame> getDataViews()
  {
    final List<DataFrame> dataViews = new ArrayList<DataFrame>();
    for (final Component component : dataViewsPanel.getComponents())
    {
      dataViews.add((DataFrame) component);
    }
    return dataViews;
  }

  public void addDataView(final DataFrame view)
  {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;

    view.setMinimumSize(new Dimension(300, 200));
    view.setPreferredSize(new Dimension(300, 200));

    dataViewsPanel.add(view, gbc);
  }

  public void updateDataViewsPanel()
  {
    dataViewsPanel.revalidate();
    dataViewsPanel.repaint();
  }

  private void modifyColumnWidths()
  {
    metaDataTable.getColumnModel().getColumn(0).setPreferredWidth(85);
    metaDataTable.getColumnModel().getColumn(0).setMaxWidth(85);
    metaDataTable.getColumnModel().getColumn(0).setMinWidth(85);
    metaDataTable.getColumnModel().getColumn(0).setResizable(false);

    metaDataTable.getColumnModel().getColumn(1).setPreferredWidth(35);
    metaDataTable.getColumnModel().getColumn(1).setMaxWidth(35);
    metaDataTable.getColumnModel().getColumn(1).setMinWidth(35);
    metaDataTable.getColumnModel().getColumn(1).setResizable(false);

    metaDataTable.getColumnModel().getColumn(2).setPreferredWidth(70);
  }

  public void showMessage(final String msg, final boolean isErrorMsg)
  {
    JOptionPane.showMessageDialog(this, msg, isErrorMsg ? BundleUtil.getMessage(
        "errorMessageBox.title") : BundleUtil.getMessage("infoMessageBox.title"),
        isErrorMsg ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
  }

  public int showOptionMessage(String msg, String title)
  {
    return JOptionPane.showConfirmDialog(this, msg, title,
        JOptionPane.OK_CANCEL_OPTION);
  }

  public MetaDataTableModel getMetaDataTableModel()
  {
    return (MetaDataTableModel) metaDataTable.getModel();
  }

  public void showDeviceEnabled()
  {
    this.statusTextLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/resources/images/connect.png")));
    this.statusTextLabel.setToolTipText(BundleUtil.getMessage(
        "toolTip.deviceEnabled"));
  }

  public void showDeviceDisabled()
  {
    this.statusTextLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/resources/images/disconnect.png")));
    this.statusTextLabel.setToolTipText(BundleUtil.getMessage(
        "toolTip.deviceDisabled"));
  }

  public void setMetaDataTableModel(final MetaDataTableModel model)
  {
    metaDataTable.setModel(model);
    modifyColumnWidths();
  }

  public void showStatusMessage(final String text)
  {
    statusTextLabel.setText(text);
  }

  public File[] showSoundFileChooser(String dir)
  {
    File[] selectedFiles = null;

    final FileFilter filter = new FileFilter()
    {

      @Override
      public boolean accept(File f)
      {
        if (f.getName().toLowerCase().indexOf(Axbo.SOUND_DATA_FILE_EXT.
            toLowerCase()) > 0 || f.isDirectory())
        {
          return true;
        }
        else
        {
          return false;
        }
      }

      @Override
      public String getDescription()
      {
        return "Axbo Sound Package Files";
      }
    };

    // open file chooser for directory with sleep data files
    JFileChooser chooser = new JFileChooser(dir);
    chooser.setFileFilter(filter);
    chooser.setMultiSelectionEnabled(true);
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    // repaint the main window before event processing finishes
    //this.contentPanel.paintImmediately(
    //    new Rectangle(this.contentPanel.getSize()));

    int returnVal = chooser.showOpenDialog(this);

    // process selected directory
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      selectedFiles = chooser.getSelectedFiles();
    }

//    this.contentPanel.paintImmediately(
//        new Rectangle(this.contentPanel.getSize()));

    return selectedFiles;
  }

  public void setStatusProgressBarLength(int len)
  {
    statusProgressBar.setMaximum(len);
  }

  public void setStatusProgressBarValue(int val)
  {
    statusProgressBar.setValue(val);
  }

  public void setStatusProgressBarIndeterminate(boolean b)
  {
    statusProgressBar.setIndeterminate(b);
  }

  public void showSplashScreen()
  {
    try
    {
      this.splashScreen = new SplashScreen();
      this.splashScreen.setImageURL(this.getClass().getResource(
          "/resources/images/SplashScreen-11_07.gif"));
      this.splashScreen.setVisible(true);
      if (!this.isVisible())
      {
        Rectangle screenRect = this.getGraphicsConfiguration().getBounds();
        splashScreen.setLocation(
            screenRect.x + screenRect.width / 2 - splashScreen.getBounds().width
            / 2,
            screenRect.y + screenRect.height / 2 - splashScreen.getBounds().height
            / 2);
      }
      else
      {
        Rectangle screenRect = this.getBounds();
        splashScreen.setLocation(
            screenRect.x + screenRect.width / 2 - splashScreen.getBounds().width
            / 2,
            screenRect.y + screenRect.height / 2 - splashScreen.getBounds().height
            / 2);
      }

    }
    catch (Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }
  }

  public void hideSplashScreen()
  {
    this.setVisible(true);
    this.splashScreen.setVisible(false);
    this.splashScreen = null;

  }

  public void showSummary(final long sumDuration, final long avgDuration,
      final long minDuration, final long maxDuration, final long timeSaving,
      final int count)
  {
    legendPanel.setVisible(getDataViews().size() > 0);

    if (sumDuration != 0)
    {
      summaryPanel.setVisible(true);

      final Calendar cal = Calendar.getInstance(
          TimeZone.getTimeZone("GMT"));

      cal.setTime(new Date(minDuration));
      lblSleepDurationMinValue.setText(String.format("%tT", cal));
      cal.setTime(new Date(maxDuration));
      lblSleepDurationMaxValue.setText(String.format("%tT", cal));
      cal.setTime(new Date(avgDuration));
      lblSleepDurationAvgValue.setText(String.format("%tT", cal));
      cal.setTime(new Date(timeSaving));
      lblTimeSavingsValue.setText(String.format("%tR", cal));

      lblCountSelecetedVal.setText(String.format("%-2d", count));
    }
    else
    {
      summaryPanel.setVisible(false);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    sleepDataPopupMenu = new javax.swing.JPopupMenu();
    viewPopupMenuItem = new javax.swing.JMenuItem();
    deletePopupMenuItem = new javax.swing.JMenuItem();
    soundsPopupMenu = new javax.swing.JPopupMenu();
    openSoundPkgPopupMenuItem = new javax.swing.JMenuItem();
    deleteSoungPkgPopupMenuItem = new javax.swing.JMenuItem();
    toolbarPanel = new javax.swing.JPanel();
    navToolbarPanel = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    spacerPanel = new javax.swing.JPanel();
    mainToolbar = new javax.swing.JPanel();
    dataToolbarPanel = new javax.swing.JPanel();
    loadDataButton = new javax.swing.JButton();
    btnCompare = new javax.swing.JButton();
    btnPrint = new javax.swing.JButton();
    btnCloseAll = new javax.swing.JButton();
    soundToolbarPanel = new javax.swing.JPanel();
    btnSoundPkgImport = new javax.swing.JButton();
    mainPanel = new javax.swing.JPanel();
    dataPanel = new javax.swing.JPanel();
    dataListPanel = new javax.swing.JPanel();
    tableScrollPane = new javax.swing.JScrollPane();
    metaDataTable = new javax.swing.JTable();
    searchTermsPanel = new javax.swing.JPanel();
    searchNameLabel = new javax.swing.JLabel();
    searchNameTextField = new javax.swing.JTextField();
    searchDateFromLabel = new javax.swing.JLabel();
    searchDateFromTextField = new org.jdesktop.swingx.JXDatePicker();
    searchDateToLabel = new javax.swing.JLabel();
    searchDateToTextField = new org.jdesktop.swingx.JXDatePicker();
    searchButton = new javax.swing.JButton();
    dataContainerPanel = new javax.swing.JPanel();
    dataScrollPane = new javax.swing.JScrollPane();
    dataViewsPanel = new com.dreikraft.swing.BackgroundImagePanel();
    infoPanel = new javax.swing.JPanel();
    summaryPanel = new javax.swing.JPanel();
    lblSleepDuration = new javax.swing.JLabel();
    lblSleepDurationMin = new javax.swing.JLabel();
    lblSleepDurationMinValue = new javax.swing.JLabel();
    lblSleepDurationMax = new javax.swing.JLabel();
    lblSleepDurationMaxValue = new javax.swing.JLabel();
    lblSleepDurationAvg = new javax.swing.JLabel();
    lblSleepDurationAvgValue = new javax.swing.JLabel();
    lblTimeSavings = new javax.swing.JLabel();
    lblTimeSavingsValue = new javax.swing.JLabel();
    lblCountSelected = new javax.swing.JLabel();
    lblCountSelecetedVal = new javax.swing.JLabel();
    legendPanel = new javax.swing.JPanel();
    lblLegendMovementColor = new javax.swing.JLabel();
    lblLegendMovement = new javax.swing.JLabel();
    lblLegendKeyColor = new javax.swing.JLabel();
    lblLegendKey = new javax.swing.JLabel();
    lblLegendSleepStartColor = new javax.swing.JLabel();
    lblLegendSleepStart = new javax.swing.JLabel();
    lblLegendWakeTimeColor = new javax.swing.JLabel();
    lblLegendWakeTime = new javax.swing.JLabel();
    lblLegendSnoozeColor = new javax.swing.JLabel();
    lblLegendSnooze = new javax.swing.JLabel();
    lblLegendWakeIntervalColor = new javax.swing.JLabel();
    lblLegendWakeInterval = new javax.swing.JLabel();
    statusTextPanel = new javax.swing.JPanel();
    statusTextLabel = new javax.swing.JLabel();
    statusProgressBar = new javax.swing.JProgressBar();
    menuBar = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    viewMenuItem = new javax.swing.JMenuItem();
    deleteMenuItem = new javax.swing.JMenuItem();
    deleteSoungPkgMenuItem = new javax.swing.JMenuItem();
    miImportSound = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    prefsMenuItem = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    exitMenuItem = new javax.swing.JMenuItem();
    deviceMenu = new javax.swing.JMenu();
    readStoredDataMenuItem = new javax.swing.JMenuItem();
    clearDataMenuItem = new javax.swing.JMenuItem();
    setClockDateMenuItem = new javax.swing.JMenuItem();
    readStatusMenuItem = new javax.swing.JMenuItem();
    resetClockMenuItem = new javax.swing.JMenuItem();

    viewPopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/chart_bar.png"))); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/default"); // NOI18N
    viewPopupMenuItem.setText(bundle.getString("menu.file.view")); // NOI18N
    viewPopupMenuItem.setDoubleBuffered(true);
    viewPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewPopupMenuItemActionPerformed(evt);
      }
    });
    sleepDataPopupMenu.add(viewPopupMenuItem);

    deletePopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin.png"))); // NOI18N
    deletePopupMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deletePopupMenuItem.setDoubleBuffered(true);
    deletePopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deletePopupMenuItemActionPerformed(evt);
      }
    });
    sleepDataPopupMenu.add(deletePopupMenuItem);

    openSoundPkgPopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/folder_go.png"))); // NOI18N
    openSoundPkgPopupMenuItem.setText(bundle.getString("menu.file.open")); // NOI18N
    openSoundPkgPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openSoundPkgPopupMenuItemActionPerformed(evt);
      }
    });
    soundsPopupMenu.add(openSoundPkgPopupMenuItem);

    deleteSoungPkgPopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin.png"))); // NOI18N
    deleteSoungPkgPopupMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deleteSoungPkgPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteSoungPkgPopupMenuItemActionPerformed(evt);
      }
    });
    soundsPopupMenu.add(deleteSoungPkgPopupMenuItem);

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle(bundle.getString("mdiframe.title")); // NOI18N
    setIconImage(new ImageIcon(getClass().getResource(Axbo.ICON_IMAGE_DEFAULT)).getImage());
    setName("axboFrame"); // NOI18N
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });
    getContentPane().setLayout(new java.awt.BorderLayout(5, 0));

    toolbarPanel.setLayout(new java.awt.BorderLayout());

    navToolbarPanel.setPreferredSize(new java.awt.Dimension(350, 20));
    navToolbarPanel.setLayout(new java.awt.GridBagLayout());

    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/aXbo-logo-software-small.png"))); // NOI18N
    navToolbarPanel.add(jLabel1, new java.awt.GridBagConstraints());

    org.jdesktop.layout.GroupLayout spacerPanelLayout = new org.jdesktop.layout.GroupLayout(spacerPanel);
    spacerPanel.setLayout(spacerPanelLayout);
    spacerPanelLayout.setHorizontalGroup(
      spacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 260, Short.MAX_VALUE)
    );
    spacerPanelLayout.setVerticalGroup(
      spacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(0, 46, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.weightx = 1.0;
    navToolbarPanel.add(spacerPanel, gridBagConstraints);

    toolbarPanel.add(navToolbarPanel, java.awt.BorderLayout.WEST);

    mainToolbar.setLayout(new java.awt.CardLayout());

    dataToolbarPanel.setLayout(new java.awt.GridBagLayout());

    loadDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/redo.png"))); // NOI18N
    loadDataButton.setText(bundle.getString("menu.device.readStoredData")); // NOI18N
    loadDataButton.setToolTipText(bundle.getString("button.loadData.tooltip")); // NOI18N
    loadDataButton.setBorderPainted(false);
    loadDataButton.setFocusable(false);
    loadDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    loadDataButton.setIconTextGap(2);
    loadDataButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    loadDataButton.setMultiClickThreshhold(1000L);
    loadDataButton.setRequestFocusEnabled(false);
    loadDataButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    loadDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    loadDataButton.putClientProperty("JButton.buttonType", "segmentedTextured");
    loadDataButton.putClientProperty("JButton.segmentPosition", "middle");
    loadDataButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadDataButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(loadDataButton, gridBagConstraints);

    btnCompare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/shape_align_left.png"))); // NOI18N
    btnCompare.setText(bundle.getString("compareMenuItem.text")); // NOI18N
    btnCompare.setBorderPainted(false);
    btnCompare.setFocusable(false);
    btnCompare.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnCompare.setIconTextGap(2);
    btnCompare.setMargin(new java.awt.Insets(0, 0, 0, 0));
    btnCompare.setMultiClickThreshhold(1000L);
    btnCompare.setRequestFocusEnabled(false);
    btnCompare.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    btnCompare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnCompare.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCompareActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnCompare, gridBagConstraints);

    btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer.png"))); // NOI18N
    btnPrint.setText(bundle.getString("menu.file.print")); // NOI18N
    btnPrint.setBorderPainted(false);
    btnPrint.setFocusable(false);
    btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPrint.setIconTextGap(2);
    btnPrint.setMargin(new java.awt.Insets(0, 0, 0, 0));
    btnPrint.setMultiClickThreshhold(1000L);
    btnPrint.setRequestFocusEnabled(false);
    btnPrint.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPrintActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnPrint, gridBagConstraints);

    btnCloseAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/application_form_delete.png"))); // NOI18N
    btnCloseAll.setText(bundle.getString("menu.file.closeAll")); // NOI18N
    btnCloseAll.setBorderPainted(false);
    btnCloseAll.setFocusable(false);
    btnCloseAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnCloseAll.setIconTextGap(2);
    btnCloseAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
    btnCloseAll.setMultiClickThreshhold(1000L);
    btnCloseAll.setRequestFocusEnabled(false);
    btnCloseAll.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    btnCloseAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnCloseAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCloseAllActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnCloseAll, gridBagConstraints);

    mainToolbar.add(dataToolbarPanel, "data");

    soundToolbarPanel.setLayout(new java.awt.GridBagLayout());

    btnSoundPkgImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/sound_add.png"))); // NOI18N
    btnSoundPkgImport.setText(bundle.getString("btnSoundPkgImport.text")); // NOI18N
    btnSoundPkgImport.setToolTipText(bundle.getString("btnSoundPkgImport.tooltip")); // NOI18N
    btnSoundPkgImport.setBorderPainted(false);
    btnSoundPkgImport.setFocusable(false);
    btnSoundPkgImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnSoundPkgImport.setIconTextGap(2);
    btnSoundPkgImport.setMultiClickThreshhold(1000L);
    btnSoundPkgImport.setRequestFocusEnabled(false);
    btnSoundPkgImport.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    btnSoundPkgImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnSoundPkgImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSoundPkgImportActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    soundToolbarPanel.add(btnSoundPkgImport, gridBagConstraints);

    mainToolbar.add(soundToolbarPanel, "sound");

    toolbarPanel.add(mainToolbar, java.awt.BorderLayout.CENTER);

    getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

    mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    mainPanel.setLayout(new java.awt.CardLayout());

    dataPanel.setLayout(new java.awt.BorderLayout(4, 0));

    dataListPanel.setPreferredSize(new java.awt.Dimension(350, 10));
    dataListPanel.setLayout(new java.awt.GridBagLayout());

    tableScrollPane.setAutoscrolls(true);
    tableScrollPane.setFocusable(false);

    metaDataTable.setModel(new MetaDataTableModel());
    metaDataTable.setAutoCreateRowSorter(true);
    metaDataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
    metaDataTable.setComponentPopupMenu(sleepDataPopupMenu);
    metaDataTable.setFocusable(false);
    metaDataTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    metaDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        metaDataTableMouseClicked(evt);
      }
    });
    tableScrollPane.setViewportView(metaDataTable);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    dataListPanel.add(tableScrollPane, gridBagConstraints);

    searchTermsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    searchTermsPanel.setLayout(new java.awt.GridBagLayout());

    searchNameLabel.setLabelFor(searchNameTextField);
    searchNameLabel.setText(bundle.getString("search.label.name")); // NOI18N
    searchNameLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    searchTermsPanel.add(searchNameLabel, gridBagConstraints);

    searchNameTextField.putClientProperty("JTextField.variant", "search");
    searchNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        searchTextFieldsKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    searchTermsPanel.add(searchNameTextField, gridBagConstraints);

    searchDateFromLabel.setLabelFor(searchDateFromTextField);
    searchDateFromLabel.setText(bundle.getString("search.label.dateFrom")); // NOI18N
    searchDateFromLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    searchTermsPanel.add(searchDateFromLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    searchTermsPanel.add(searchDateFromTextField, gridBagConstraints);

    searchDateToLabel.setLabelFor(searchDateToTextField);
    searchDateToLabel.setText(bundle.getString("search.label.dateTo")); // NOI18N
    searchDateToLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    searchTermsPanel.add(searchDateToLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    searchTermsPanel.add(searchDateToTextField, gridBagConstraints);

    searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/glass.png"))); // NOI18N
    searchButton.setToolTipText(bundle.getString("button.search.tooltip")); // NOI18N
    searchButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    searchButton.putClientProperty("JButton.buttonType", "textured");
    searchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        searchButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    searchTermsPanel.add(searchButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
    dataListPanel.add(searchTermsPanel, gridBagConstraints);

    dataPanel.add(dataListPanel, java.awt.BorderLayout.WEST);

    dataContainerPanel.setLayout(new java.awt.BorderLayout());

    dataScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    dataViewsPanel.setBackgroundImageFilename("/resources/images/background_dark.png"); // NOI18N
    dataViewsPanel.setLayout(new java.awt.GridBagLayout());
    dataScrollPane.setViewportView(dataViewsPanel);

    dataContainerPanel.add(dataScrollPane, java.awt.BorderLayout.CENTER);

    infoPanel.setLayout(new java.awt.BorderLayout());

    summaryPanel.setVisible(false);
    summaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    summaryPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

    lblSleepDuration.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDuration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDuration.setText(bundle.getString("lblSleepDuration")); // NOI18N
    summaryPanel.add(lblSleepDuration);

    lblSleepDurationMin.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationMin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDurationMin.setText(bundle.getString("lblSleepDurationMin")); // NOI18N
    summaryPanel.add(lblSleepDurationMin);

    lblSleepDurationMinValue.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationMinValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationMinValue);

    lblSleepDurationMax.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationMax.setText(bundle.getString("lblSleepDurationMax")); // NOI18N
    summaryPanel.add(lblSleepDurationMax);

    lblSleepDurationMaxValue.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationMaxValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationMaxValue);

    lblSleepDurationAvg.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationAvg.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDurationAvg.setText(bundle.getString("lblSleepDurationAvg")); // NOI18N
    summaryPanel.add(lblSleepDurationAvg);

    lblSleepDurationAvgValue.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblSleepDurationAvgValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationAvgValue);

    lblTimeSavings.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblTimeSavings.setText(bundle.getString("lblTimeSaving")); // NOI18N
    summaryPanel.add(lblTimeSavings);

    lblTimeSavingsValue.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblTimeSavingsValue.setText("--:--");
    summaryPanel.add(lblTimeSavingsValue);

    lblCountSelected.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblCountSelected.setText(bundle.getString("lblCountSelected")); // NOI18N
    summaryPanel.add(lblCountSelected);

    lblCountSelecetedVal.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblCountSelecetedVal.setText("--");
    summaryPanel.add(lblCountSelecetedVal);

    infoPanel.add(summaryPanel, java.awt.BorderLayout.NORTH);

    legendPanel.setVisible(false);
    legendPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    legendPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

    lblLegendMovementColor.setBackground(DataFrame.BAR_COLOR);
    lblLegendMovementColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendMovementColor.setText("  ");
    lblLegendMovementColor.setOpaque(true);
    legendPanel.add(lblLegendMovementColor);

    lblLegendMovement.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendMovement.setText(bundle.getString("lblLegendMovements")); // NOI18N
    legendPanel.add(lblLegendMovement);

    lblLegendKeyColor.setBackground(DataFrame.KEY_PAINT);
    lblLegendKeyColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendKeyColor.setText("  ");
    lblLegendKeyColor.setOpaque(true);
    legendPanel.add(lblLegendKeyColor);

    lblLegendKey.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendKey.setText(bundle.getString("lblLegendKeys")); // NOI18N
    legendPanel.add(lblLegendKey);

    lblLegendSleepStartColor.setBackground(DataFrame.SLEEP_MARKER_PAINT);
    lblLegendSleepStartColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendSleepStartColor.setText("  ");
    lblLegendSleepStartColor.setOpaque(true);
    legendPanel.add(lblLegendSleepStartColor);

    lblLegendSleepStart.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendSleepStart.setText(bundle.getString("lblLegendSleepStart")); // NOI18N
    legendPanel.add(lblLegendSleepStart);

    lblLegendWakeTimeColor.setBackground(DataFrame.WAKE_PAINT);
    lblLegendWakeTimeColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendWakeTimeColor.setText("  ");
    lblLegendWakeTimeColor.setOpaque(true);
    legendPanel.add(lblLegendWakeTimeColor);

    lblLegendWakeTime.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendWakeTime.setText(bundle.getString("lblLegendWakeupTime")); // NOI18N
    legendPanel.add(lblLegendWakeTime);

    lblLegendSnoozeColor.setBackground(DataFrame.SNOOZE_PAINT);
    lblLegendSnoozeColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendSnoozeColor.setText("  ");
    lblLegendSnoozeColor.setOpaque(true);
    legendPanel.add(lblLegendSnoozeColor);

    lblLegendSnooze.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendSnooze.setText(bundle.getString("lblLegendSnooze")); // NOI18N
    legendPanel.add(lblLegendSnooze);

    lblLegendWakeIntervalColor.setBackground(DataFrame.WAKE_INTERVALL_PAINT);
    lblLegendWakeIntervalColor.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendWakeIntervalColor.setText("  ");
    lblLegendWakeIntervalColor.setOpaque(true);
    legendPanel.add(lblLegendWakeIntervalColor);

    lblLegendWakeInterval.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
    lblLegendWakeInterval.setText(bundle.getString("lblLegendWakeInterval")); // NOI18N
    legendPanel.add(lblLegendWakeInterval);

    infoPanel.add(legendPanel, java.awt.BorderLayout.SOUTH);

    dataContainerPanel.add(infoPanel, java.awt.BorderLayout.SOUTH);

    dataPanel.add(dataContainerPanel, java.awt.BorderLayout.CENTER);

    mainPanel.add(dataPanel, "data");

    getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

    statusTextPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 1, 1));
    statusTextPanel.setLayout(new java.awt.BorderLayout(5, 5));

    statusTextLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/disconnect.png"))); // NOI18N
    statusTextPanel.add(statusTextLabel, java.awt.BorderLayout.CENTER);

    statusProgressBar.setFocusable(false);
    statusProgressBar.setRequestFocusEnabled(false);
    statusProgressBar.setString("\n");
    statusProgressBar.putClientProperty("JProgressBar.style", "circular");
    statusTextPanel.add(statusProgressBar, java.awt.BorderLayout.EAST);

    getContentPane().add(statusTextPanel, java.awt.BorderLayout.SOUTH);

    fileMenu.setText(bundle.getString("menu.file")); // NOI18N

    viewMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/chart_bar.png"))); // NOI18N
    viewMenuItem.setText(bundle.getString("menu.file.view")); // NOI18N
    viewMenuItem.setDoubleBuffered(true);
    viewMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(viewMenuItem);

    deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin.png"))); // NOI18N
    deleteMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(deleteMenuItem);

    deleteSoungPkgMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin.png"))); // NOI18N
    deleteSoungPkgMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deleteSoungPkgMenuItem.setVisible(false);
    deleteSoungPkgMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteSoungPkgMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(deleteSoungPkgMenuItem);

    miImportSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/sound_add.png"))); // NOI18N
    miImportSound.setText(bundle.getString("btnSoundPkgImport.text")); // NOI18N
    miImportSound.setVisible(false);
    miImportSound.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        miImportSoundActionPerformed(evt);
      }
    });
    fileMenu.add(miImportSound);

    if (Axbo.MAC_OS_X)
    jSeparator1.setVisible(false);
    fileMenu.add(jSeparator1);

    if (Axbo.MAC_OS_X)
    prefsMenuItem.setVisible(false);
    prefsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/detailsViewIcon.png"))); // NOI18N
    prefsMenuItem.setText(bundle.getString("menu.file.prefs")); // NOI18N
    prefsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        prefsMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(prefsMenuItem);

    if (Axbo.MAC_OS_X)
    jSeparator3.setVisible(false);
    fileMenu.add(jSeparator3);

    if (Axbo.MAC_OS_X)
    exitMenuItem.setVisible(false);
    exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/cross.png"))); // NOI18N
    exitMenuItem.setText(bundle.getString("menu.file.exit")); // NOI18N
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    deviceMenu.setText(bundle.getString("menu.device")); // NOI18N

    readStoredDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/redo.png"))); // NOI18N
    readStoredDataMenuItem.setText(bundle.getString("menu.device.readStoredData")); // NOI18N
    readStoredDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        readStoredDataMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(readStoredDataMenuItem);

    clearDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin.png"))); // NOI18N
    clearDataMenuItem.setText(bundle.getString("menu.device.clearData")); // NOI18N
    clearDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearDataMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(clearDataMenuItem);

    setClockDateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/time.png"))); // NOI18N
    setClockDateMenuItem.setText(bundle.getString("menu.device.setDate")); // NOI18N
    setClockDateMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setClockDateMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(setClockDateMenuItem);

    readStatusMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/information.png"))); // NOI18N
    readStatusMenuItem.setText(bundle.getString("menu.device.readStatus")); // NOI18N
    readStatusMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        readStatusMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(readStatusMenuItem);

    resetClockMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/undo.png"))); // NOI18N
    resetClockMenuItem.setText(bundle.getString("menu.device.resetClock")); // NOI18N
    resetClockMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        resetClockMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(resetClockMenuItem);

    menuBar.add(deviceMenu);

    setJMenuBar(menuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void resetClockMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetClockMenuItemActionPerformed
  {//GEN-HEADEREND:event_resetClockMenuItemActionPerformed
    int result = showOptionMessage(BundleUtil.getMessage("message.confirmReset"),
        BundleUtil.getMessage("infoMessageBox.title"));

    if (result == JOptionPane.OK_OPTION)
    {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboReset(
          this));
    }
  }//GEN-LAST:event_resetClockMenuItemActionPerformed

  private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteMenuItemActionPerformed
  {//GEN-HEADEREND:event_deleteMenuItemActionPerformed
    if (metaDataTable.getSelectedRowCount() > 0)
    {
      int response = showOptionMessage(BundleUtil.getMessage(
          "notification.message.delete"), BundleUtil.getMessage(
          "infoMessageBox.title"));
      if (response == JOptionPane.OK_OPTION)
      {
        int selectedRows[] = metaDataTable.getSelectedRows();
        ArrayList<SleepData> tmpSleepData =
            new ArrayList<SleepData>(Array.getLength(selectedRows));
        for (int selectedRowIdx : selectedRows)
        {
          tmpSleepData.add(getMetaDataTableModel().getSleepDataAt(
              metaDataTable.convertRowIndexToModel(selectedRowIdx)));
        }
        for (SleepData sleepData : tmpSleepData)
        {
          ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataDelete(
              this, sleepData));
        }
      }
    }
    else
    {
      showMessage(BundleUtil.getErrorMessage(
          "MetaDataTableModel.nothingSelected"), true);
    }
  }//GEN-LAST:event_deleteMenuItemActionPerformed

  private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
  {//GEN-HEADEREND:event_formWindowClosed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboDisconnect(
        this));
  }//GEN-LAST:event_formWindowClosed

  private void loadDataButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadDataButtonActionPerformed
  {//GEN-HEADEREND:event_loadDataButtonActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataImport(
        this));
  }//GEN-LAST:event_loadDataButtonActionPerformed

  private void clearDataMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearDataMenuItemActionPerformed
  {//GEN-HEADEREND:event_clearDataMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboClear(
        this));
  }//GEN-LAST:event_clearDataMenuItemActionPerformed

  private void closeAllMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeAllMenuItemActionPerformed
  {//GEN-HEADEREND:event_closeAllMenuItemActionPerformed
    for (final Component dataView : dataViewsPanel.getComponents())
    {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramClose(
          log, (DataFrame) dataView));
    }
  }//GEN-LAST:event_closeAllMenuItemActionPerformed

  private void readStatusMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_readStatusMenuItemActionPerformed
  {//GEN-HEADEREND:event_readStatusMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboStatusGet(
        this));
  }//GEN-LAST:event_readStatusMenuItemActionPerformed

  private void setClockDateMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_setClockDateMenuItemActionPerformed
  {//GEN-HEADEREND:event_setClockDateMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboTimeSet(
        this));
  }//GEN-LAST:event_setClockDateMenuItemActionPerformed

  private void readStoredDataMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_readStoredDataMenuItemActionPerformed
  {//GEN-HEADEREND:event_readStoredDataMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataImport(
        this));
  }//GEN-LAST:event_readStoredDataMenuItemActionPerformed

  private void prefsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_prefsMenuItemActionPerformed
  {//GEN-HEADEREND:event_prefsMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new PrefsOpen(
        this, this));
  }//GEN-LAST:event_prefsMenuItemActionPerformed

  private void metaDataTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_metaDataTableMouseClicked
  {//GEN-HEADEREND:event_metaDataTableMouseClicked
    if (evt.getButton() == MouseEvent.BUTTON3)
    {
      int row = metaDataTable.rowAtPoint(evt.getPoint());
      metaDataTable.getSelectionModel().addSelectionInterval(row, row);
    }

    // is it a double click, open a new internal frame with the selected sleep data
    if (evt.getClickCount() == 2)
    {
      int[] selectedRows = metaDataTable.getSelectedRows();
      final List<SleepData> sleepDataList = new ArrayList<SleepData>();
      for (int row : selectedRows)
      {
        sleepDataList.add(getMetaDataTableModel().getSleepDataAt(metaDataTable.
            convertRowIndexToModel(row)));
      }
      if (sleepDataList.size() > 0)
      {
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataOpen(
            this, sleepDataList));
      }
    }
  }//GEN-LAST:event_metaDataTableMouseClicked

  private void searchTextFieldsKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_searchTextFieldsKeyPressed
  {//GEN-HEADEREND:event_searchTextFieldsKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER)
    {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DataSearch(
          this, searchNameTextField.getText(), searchDateFromTextField.getDate(),
          searchDateToTextField.getDate()));
    }
  }//GEN-LAST:event_searchTextFieldsKeyPressed

  private void searchButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchButtonActionPerformed
  {//GEN-HEADEREND:event_searchButtonActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DataSearch(
        this, searchNameTextField.getText(), searchDateFromTextField.getDate(),
        searchDateToTextField.getDate()));
  }//GEN-LAST:event_searchButtonActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationExit(
          this));
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void deletePopupMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deletePopupMenuItemActionPerformed
    {//GEN-HEADEREND:event_deletePopupMenuItemActionPerformed
      deleteMenuItemActionPerformed(evt);
}//GEN-LAST:event_deletePopupMenuItemActionPerformed

    private void viewMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_viewMenuItemActionPerformed
    {//GEN-HEADEREND:event_viewMenuItemActionPerformed
      int[] selectedRows = metaDataTable.getSelectedRows();
      if (selectedRows.length < 1)
      {
        showMessage(BundleUtil.getErrorMessage(
            "MetaDataTableModel.nothingSelected"), true);
        return;
      }
      final List<SleepData> sleepDataList = new ArrayList<SleepData>(
          selectedRows.length);
      for (int selectedRowIdx : selectedRows)
      {
        sleepDataList.add(getMetaDataTableModel().getSleepDataAt(metaDataTable.
            convertRowIndexToModel(selectedRowIdx)));
      }
      if (sleepDataList.size() > 0)
      {
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataOpen(
            this, sleepDataList));
      }
    }//GEN-LAST:event_viewMenuItemActionPerformed

    private void viewPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_viewPopupMenuItemActionPerformed
    {//GEN-HEADEREND:event_viewPopupMenuItemActionPerformed
      viewMenuItemActionPerformed(evt);
    }//GEN-LAST:event_viewPopupMenuItemActionPerformed

    private void soundPackagesTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_soundPackagesTableMouseClicked
    {//GEN-HEADEREND:event_soundPackagesTableMouseClicked
//      if (evt.getButton() == MouseEvent.BUTTON3)
//      {
//        int row = soundPackagesTable.rowAtPoint(evt.getPoint());
//        soundPackagesTable.getSelectionModel().addSelectionInterval(row, row);
//      }
//
//      if (evt.getClickCount() == 2)
//      {
//        ctrl.openSoundPkg(soundPackagesTable.getSelectedRow());
//      }
    }//GEN-LAST:event_soundPackagesTableMouseClicked

    private void btnSoundPkgImportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSoundPkgImportActionPerformed
    {//GEN-HEADEREND:event_btnSoundPkgImportActionPerformed
//      ctrl.importSoundPackage();
    }//GEN-LAST:event_btnSoundPkgImportActionPerformed

    private void miImportSoundActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miImportSoundActionPerformed
    {//GEN-HEADEREND:event_miImportSoundActionPerformed
//      ctrl.importSoundPackage();
    }//GEN-LAST:event_miImportSoundActionPerformed

private void deleteSoungPkgMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSoungPkgMenuItemActionPerformed
//  if (soundPackagesTable.getSelectedRowCount() > 0)
//  {
//    int response = showOptionMessage(BundleUtil.getMessage(
//        "notification.message.delete"), BundleUtil.getMessage(
//        "infoMessageBox.title"));
//    if (response == JOptionPane.OK_OPTION)
//    {
//      int selectedRows[] = soundPackagesTable.getSelectedRows();
//      ArrayList<SoundPackage> tmpSndPkgs =
//          new ArrayList<SoundPackage>(Array.getLength(selectedRows));
//      for (int i : selectedRows)
//      {
//        tmpSndPkgs.add(((SoundPackagesTableModel) soundPackagesTable.getModel()).
//            getSoundPackageAt(i));
//      }
//      for (SoundPackage sndPkg : tmpSndPkgs)
//      {
//        ctrl.deleteSoundPackage(sndPkg);
//      }
//    }
//  }
//  else
//  {
//    showMessage(BundleUtil.getErrorMessage("MetaDataTableModel.nothingSelected"),
//        BundleUtil.getMessage("errorMessageBox.title"), true);
//  }
}//GEN-LAST:event_deleteSoungPkgMenuItemActionPerformed

private void deleteSoungPkgPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSoungPkgPopupMenuItemActionPerformed
  deleteSoungPkgMenuItemActionPerformed(evt);
}//GEN-LAST:event_deleteSoungPkgPopupMenuItemActionPerformed

private void openSoundPkgPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSoundPkgPopupMenuItemActionPerformed
//  if (soundPackagesTable.getSelectedRowCount() > 0)
//  {
//    ctrl.openSoundPkg(soundPackagesTable.getSelectedRows()[0]);
//  }
//  else
//  {
//    showMessage(BundleUtil.getErrorMessage("MetaDataTableModel.nothingSelected"),
//        BundleUtil.getMessage("errorMessageBox.title"), true);
//  }
//
//
}//GEN-LAST:event_openSoundPkgPopupMenuItemActionPerformed

private void btnCompareActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCompareActionPerformed
{//GEN-HEADEREND:event_btnCompareActionPerformed
  ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataCompare(
      this));
}//GEN-LAST:event_btnCompareActionPerformed

private void btnCloseAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCloseAllActionPerformed
{//GEN-HEADEREND:event_btnCloseAllActionPerformed
  closeAllMenuItemActionPerformed(evt);
}//GEN-LAST:event_btnCloseAllActionPerformed

private void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
{//GEN-HEADEREND:event_btnPrintActionPerformed
  if (getDataViews().size() > 0)
  {
    final PrinterJob job = PrinterJob.getPrinterJob();
    if (job.printDialog())
    {
      final Book book = new Book();
      job.setPageable(book);
      job.setJobName("aXbo");
      ApplicationEventDispatcher.getInstance().dispatchEvent(new DiagramPrint(
          this, job, book));
      try
      {
        job.print();
      }
      catch (PrinterException ex)
      {
        final String msg = BundleUtil.getErrorMessage(
            "globalError.printingFailed");
        log.error(msg, ex);
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationMessageEvent(
            this, msg, true));
      }
    }
  }
}//GEN-LAST:event_btnPrintActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnCloseAll;
  private javax.swing.JButton btnCompare;
  private javax.swing.JButton btnPrint;
  private javax.swing.JButton btnSoundPkgImport;
  private javax.swing.JMenuItem clearDataMenuItem;
  private javax.swing.JPanel dataContainerPanel;
  private javax.swing.JPanel dataListPanel;
  private javax.swing.JPanel dataPanel;
  private javax.swing.JScrollPane dataScrollPane;
  private javax.swing.JPanel dataToolbarPanel;
  private com.dreikraft.swing.BackgroundImagePanel dataViewsPanel;
  private javax.swing.JMenuItem deleteMenuItem;
  private javax.swing.JMenuItem deletePopupMenuItem;
  private javax.swing.JMenuItem deleteSoungPkgMenuItem;
  private javax.swing.JMenuItem deleteSoungPkgPopupMenuItem;
  private javax.swing.JMenu deviceMenu;
  private javax.swing.JMenuItem exitMenuItem;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JPanel infoPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JLabel lblCountSelecetedVal;
  private javax.swing.JLabel lblCountSelected;
  private javax.swing.JLabel lblLegendKey;
  private javax.swing.JLabel lblLegendKeyColor;
  private javax.swing.JLabel lblLegendMovement;
  private javax.swing.JLabel lblLegendMovementColor;
  private javax.swing.JLabel lblLegendSleepStart;
  private javax.swing.JLabel lblLegendSleepStartColor;
  private javax.swing.JLabel lblLegendSnooze;
  private javax.swing.JLabel lblLegendSnoozeColor;
  private javax.swing.JLabel lblLegendWakeInterval;
  private javax.swing.JLabel lblLegendWakeIntervalColor;
  private javax.swing.JLabel lblLegendWakeTime;
  private javax.swing.JLabel lblLegendWakeTimeColor;
  private javax.swing.JLabel lblSleepDuration;
  private javax.swing.JLabel lblSleepDurationAvg;
  private javax.swing.JLabel lblSleepDurationAvgValue;
  private javax.swing.JLabel lblSleepDurationMax;
  private javax.swing.JLabel lblSleepDurationMaxValue;
  private javax.swing.JLabel lblSleepDurationMin;
  private javax.swing.JLabel lblSleepDurationMinValue;
  private javax.swing.JLabel lblTimeSavings;
  private javax.swing.JLabel lblTimeSavingsValue;
  private javax.swing.JPanel legendPanel;
  private javax.swing.JButton loadDataButton;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JPanel mainToolbar;
  private javax.swing.JMenuBar menuBar;
  javax.swing.JTable metaDataTable;
  private javax.swing.JMenuItem miImportSound;
  private javax.swing.JPanel navToolbarPanel;
  private javax.swing.JMenuItem openSoundPkgPopupMenuItem;
  private javax.swing.JMenuItem prefsMenuItem;
  private javax.swing.JMenuItem readStatusMenuItem;
  private javax.swing.JMenuItem readStoredDataMenuItem;
  private javax.swing.JMenuItem resetClockMenuItem;
  private javax.swing.JButton searchButton;
  private javax.swing.JLabel searchDateFromLabel;
  private org.jdesktop.swingx.JXDatePicker searchDateFromTextField;
  private javax.swing.JLabel searchDateToLabel;
  private org.jdesktop.swingx.JXDatePicker searchDateToTextField;
  private javax.swing.JLabel searchNameLabel;
  private javax.swing.JTextField searchNameTextField;
  private javax.swing.JPanel searchTermsPanel;
  private javax.swing.JMenuItem setClockDateMenuItem;
  private javax.swing.JPopupMenu sleepDataPopupMenu;
  private javax.swing.JPanel soundToolbarPanel;
  private javax.swing.JPopupMenu soundsPopupMenu;
  private javax.swing.JPanel spacerPanel;
  private javax.swing.JProgressBar statusProgressBar;
  private javax.swing.JLabel statusTextLabel;
  private javax.swing.JPanel statusTextPanel;
  private javax.swing.JPanel summaryPanel;
  private javax.swing.JScrollPane tableScrollPane;
  private javax.swing.JPanel toolbarPanel;
  private javax.swing.JMenuItem viewMenuItem;
  private javax.swing.JMenuItem viewPopupMenuItem;
  // End of variables declaration//GEN-END:variables
}
