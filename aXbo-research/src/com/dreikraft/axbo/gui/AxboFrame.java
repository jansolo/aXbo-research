package com.dreikraft.axbo.gui;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationExit;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.OS;
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
import com.dreikraft.axbo.events.SoundPackageUpload;
import com.dreikraft.axbo.model.ChartType;
import com.dreikraft.axbo.model.MetaDataTableModel;
import com.dreikraft.axbo.util.BundleUtil;
import com.dreikraft.swing.SplashScreen;
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
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.logging.*;

/**
 * AxboFrame
 *
 * @author jan.illetschko@3kraft.com
 */
public class AxboFrame extends JFrame {

  private static final Log log = LogFactory.getLog(AxboFrame.class);
  private SplashScreen splashScreen;

  public void init() {
    initComponents();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    modifyColumnWidths();
  }

  public void jumpToDataView(final DataFrame dataView) {
    dataViewsPanel.revalidate();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final double dataViewPosY = dataView.getLocation().getY();
        int scrollPosY = (int) dataViewPosY;
        final int dataViewsHeight = dataViewsPanel.getHeight();
        final int viewportHeight = dataScrollPane.getViewport().getHeight();
        if (dataViewsHeight < viewportHeight + dataViewPosY) {
          scrollPosY = dataViewsHeight - viewportHeight;
        }
        dataScrollPane.getViewport().setViewPosition(new Point(0, scrollPosY));
      }
    });
  }

  public List<DataFrame> getDataViews() {
    final List<DataFrame> dataViews = new ArrayList<>();
    for (final Component component : dataViewsPanel.getComponents()) {
      if (component instanceof DataFrame) {
        dataViews.add((DataFrame) component);
      }
    }
    return dataViews;
  }

  /**
   * Adds a sleep data frame to the data panel. Depending on the chart type
   * different layout constraints will be set.
   *
   * @param view a view instance
   * @param chartType the requested chart type
   * @param sleepData the sleepData
   */
  public void addDataView(final DataFrame view, final ChartType chartType,
      final SleepData sleepData) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weightx = 1.0;
    final int heightExt = sleepData.getComment() != null && sleepData
        .getComment().trim().length() > 0 ? 16 : 0;

    if (chartType.equals(ChartType.MOVING_AVG)) {
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weighty = 0;
      view.setMinimumSize(new Dimension(300, 85 + heightExt));
      view.setPreferredSize(new Dimension(300, 85 + heightExt));
      view.setMaximumSize(new Dimension(300, 85 + heightExt));
    } else {
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weighty = 1.0;
      view.setMinimumSize(new Dimension(300, 200 + heightExt));
      view.setPreferredSize(new Dimension(300, 200 + heightExt));
    }

    dataViewsPanel.add(view, gbc);
  }

  public void updateDataViewsPanel() {
    dataViewsPanel.revalidate();
    dataViewsPanel.repaint();
  }

  private void modifyColumnWidths() {
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

  public void showMessage(final String msg, final boolean isErrorMsg) {
    JOptionPane.showMessageDialog(this, msg, isErrorMsg ? BundleUtil.getMessage(
        "errorMessageBox.title") : BundleUtil.getMessage("infoMessageBox.title"),
        isErrorMsg ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
  }

  public int showOptionMessage(String msg, String title) {
    return JOptionPane.showConfirmDialog(this, msg, title,
        JOptionPane.OK_CANCEL_OPTION);
  }

  public MetaDataTableModel getMetaDataTableModel() {
    return (MetaDataTableModel) metaDataTable.getModel();
  }

  public void showDeviceEnabled() {
    this.statusTextLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/resources/images/link-16.png")));
    this.statusTextLabel.setToolTipText(BundleUtil.getMessage(
        "toolTip.deviceEnabled"));
  }

  public void showDeviceDisabled() {
    this.statusTextLabel.setIcon(new javax.swing.ImageIcon(
        getClass().getResource("/resources/images/link-broken-16.png")));
    this.statusTextLabel.setToolTipText(BundleUtil.getMessage(
        "toolTip.deviceDisabled"));
  }

  public void setMetaDataTableModel(final MetaDataTableModel model) {
    metaDataTable.setModel(model);
    modifyColumnWidths();
  }

  public void showStatusMessage(final String text) {
    statusTextLabel.setText(text);
  }

  public void setStatusProgressBarLength(int len) {
    statusProgressBar.setMaximum(len);
  }

  public void setStatusProgressBarStringPainted(boolean painted) {
    statusProgressBar.setStringPainted(painted);
  }

  public void setStatusProgressBarValue(int val) {
    statusProgressBar.setValue(val);
  }

  public void setStatusProgressBarIndeterminate(boolean b) {
    statusProgressBar.setIndeterminate(b);
  }

  public void showSplashScreen() {
    try {
      this.splashScreen = new SplashScreen();
      this.splashScreen.setImageURL(this.getClass().getResource(
          "/resources/images/SplashScreen-11_07.gif"));
      this.splashScreen.setVisible(true);
      if (!this.isVisible()) {
        Rectangle screenRect = this.getGraphicsConfiguration().getBounds();
        splashScreen.setLocation(
            screenRect.x + screenRect.width / 2 - splashScreen.getBounds().width
            / 2,
            screenRect.y + screenRect.height / 2
            - splashScreen.getBounds().height
            / 2);
      } else {
        Rectangle screenRect = this.getBounds();
        splashScreen.setLocation(
            screenRect.x + screenRect.width / 2 - splashScreen.getBounds().width
            / 2,
            screenRect.y + screenRect.height / 2
            - splashScreen.getBounds().height
            / 2);
      }

    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  public void hideSplashScreen() {
    this.setVisible(true);
    this.splashScreen.setVisible(false);
    this.splashScreen = null;

  }

  public void showSummary(final long sumDuration, final long avgDuration,
      final long minDuration, final long maxDuration, final long timeSaving,
      final int count, final int countOpen) {

    legendPanel.setVisible(getDataViews().size() > 0);
    if (sumDuration != 0) {
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
      lblCountOpenVal.setText(String.format("%-2d", countOpen));
    } else {
      summaryPanel.setVisible(false);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    sleepDataPopupMenu = new javax.swing.JPopupMenu();
    viewPopupMenuItem = new javax.swing.JMenuItem();
    deletePopupMenuItem = new javax.swing.JMenuItem();
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
    lblCountOpen = new javax.swing.JLabel();
    lblCountOpenVal = new javax.swing.JLabel();
    lblSpacer4 = new javax.swing.JLabel();
    lblCountSelected = new javax.swing.JLabel();
    lblCountSelecetedVal = new javax.swing.JLabel();
    lblSpacer = new javax.swing.JLabel();
    lblSleepDuration = new javax.swing.JLabel();
    lblSleepDurationMin = new javax.swing.JLabel();
    lblSleepDurationMinValue = new javax.swing.JLabel();
    lblSpacer1 = new javax.swing.JLabel();
    lblSleepDurationMax = new javax.swing.JLabel();
    lblSleepDurationMaxValue = new javax.swing.JLabel();
    lblSpacer3 = new javax.swing.JLabel();
    lblSleepDurationAvg = new javax.swing.JLabel();
    lblSleepDurationAvgValue = new javax.swing.JLabel();
    lblSpacer2 = new javax.swing.JLabel();
    lblTimeSavings = new javax.swing.JLabel();
    lblTimeSavingsValue = new javax.swing.JLabel();
    legendPanel = new javax.swing.JPanel();
    lblLegendSleepStartColor = new javax.swing.JLabel();
    lblLegendSleepStart = new javax.swing.JLabel();
    lblLegendWakeTimeColor = new javax.swing.JLabel();
    lblLegendWakeTime = new javax.swing.JLabel();
    lblLegendWakeIntervalColor = new javax.swing.JLabel();
    lblLegendWakeInterval = new javax.swing.JLabel();
    lblLegendKeyColor = new javax.swing.JLabel();
    lblLegendKey = new javax.swing.JLabel();
    lblLegendSnoozeColor = new javax.swing.JLabel();
    lblLegendSnooze = new javax.swing.JLabel();
    lblLegendMovementColor = new javax.swing.JLabel();
    lblLegendMovement = new javax.swing.JLabel();
    statusTextPanel = new javax.swing.JPanel();
    statusTextLabel = new javax.swing.JLabel();
    statusProgressBar = new javax.swing.JProgressBar();
    menuBar = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    viewMenuItem = new javax.swing.JMenuItem();
    deleteMenuItem = new javax.swing.JMenuItem();
    uploadSoundPackageMenuItem = new javax.swing.JMenuItem();
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

    viewPopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/chart-bar-16.png"))); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/default"); // NOI18N
    viewPopupMenuItem.setText(bundle.getString("menu.file.view")); // NOI18N
    viewPopupMenuItem.setDoubleBuffered(true);
    viewPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewPopupMenuItemActionPerformed(evt);
      }
    });
    sleepDataPopupMenu.add(viewPopupMenuItem);

    deletePopupMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin-16.png"))); // NOI18N
    deletePopupMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deletePopupMenuItem.setDoubleBuffered(true);
    deletePopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deletePopupMenuItemActionPerformed(evt);
      }
    });
    sleepDataPopupMenu.add(deletePopupMenuItem);

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
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 21, 0, 0);
    navToolbarPanel.add(jLabel1, gridBagConstraints);

    javax.swing.GroupLayout spacerPanelLayout = new javax.swing.GroupLayout(spacerPanel);
    spacerPanel.setLayout(spacerPanelLayout);
    spacerPanelLayout.setHorizontalGroup(
      spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 239, Short.MAX_VALUE)
    );
    spacerPanelLayout.setVerticalGroup(
      spacerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 47, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
    gridBagConstraints.weightx = 1.0;
    navToolbarPanel.add(spacerPanel, gridBagConstraints);

    toolbarPanel.add(navToolbarPanel, java.awt.BorderLayout.WEST);

    mainToolbar.setLayout(new java.awt.BorderLayout());

    java.awt.GridBagLayout dataToolbarPanelLayout = new java.awt.GridBagLayout();
    dataToolbarPanelLayout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0};
    dataToolbarPanelLayout.rowHeights = new int[] {0};
    dataToolbarPanel.setLayout(dataToolbarPanelLayout);

    loadDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/inbox-32.png"))); // NOI18N
    loadDataButton.setText(bundle.getString("button.loadData")); // NOI18N
    loadDataButton.setToolTipText(bundle.getString("button.loadData.tooltip")); // NOI18N
    loadDataButton.setBorderPainted(false);
    loadDataButton.setContentAreaFilled(false);
    loadDataButton.setFocusable(false);
    loadDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    loadDataButton.setIconTextGap(2);
    loadDataButton.setMaximumSize(new java.awt.Dimension(120, 80));
    loadDataButton.setMinimumSize(new java.awt.Dimension(120, 80));
    loadDataButton.setMultiClickThreshhold(1000L);
    loadDataButton.setPreferredSize(new java.awt.Dimension(120, 80));
    loadDataButton.setRequestFocusEnabled(false);
    loadDataButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    loadDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    loadDataButton.putClientProperty("JButton.buttonType", "segmentedTextured");
    loadDataButton.putClientProperty("JButton.segmentPosition", "middle");
    loadDataButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadDataButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
    dataToolbarPanel.add(loadDataButton, gridBagConstraints);

    btnCompare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/ruler-32.png"))); // NOI18N
    btnCompare.setText(bundle.getString("button.compare")); // NOI18N
    btnCompare.setToolTipText(bundle.getString("button.compare.tooltip")); // NOI18N
    btnCompare.setBorderPainted(false);
    btnCompare.setContentAreaFilled(false);
    btnCompare.setFocusable(false);
    btnCompare.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnCompare.setIconTextGap(2);
    btnCompare.setMaximumSize(new java.awt.Dimension(120, 80));
    btnCompare.setMinimumSize(new java.awt.Dimension(120, 80));
    btnCompare.setMultiClickThreshhold(1000L);
    btnCompare.setPreferredSize(new java.awt.Dimension(120, 80));
    btnCompare.setRequestFocusEnabled(false);
    btnCompare.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    btnCompare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnCompare.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCompareActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnCompare, gridBagConstraints);

    btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/printer-32.png"))); // NOI18N
    btnPrint.setText(bundle.getString("button.print")); // NOI18N
    btnPrint.setToolTipText(bundle.getString("button.print.tooltip")); // NOI18N
    btnPrint.setBorderPainted(false);
    btnPrint.setContentAreaFilled(false);
    btnPrint.setFocusable(false);
    btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnPrint.setIconTextGap(2);
    btnPrint.setMaximumSize(new java.awt.Dimension(120, 80));
    btnPrint.setMinimumSize(new java.awt.Dimension(120, 80));
    btnPrint.setMultiClickThreshhold(1000L);
    btnPrint.setPreferredSize(new java.awt.Dimension(120, 80));
    btnPrint.setRequestFocusEnabled(false);
    btnPrint.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnPrint.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPrintActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnPrint, gridBagConstraints);

    btnCloseAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/button-cross-32.png"))); // NOI18N
    btnCloseAll.setText(bundle.getString("button.closeAll")); // NOI18N
    btnCloseAll.setToolTipText(bundle.getString("button.closeAll.tooltip")); // NOI18N
    btnCloseAll.setBorderPainted(false);
    btnCloseAll.setContentAreaFilled(false);
    btnCloseAll.setFocusable(false);
    btnCloseAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    btnCloseAll.setIconTextGap(2);
    btnCloseAll.setMaximumSize(new java.awt.Dimension(120, 80));
    btnCloseAll.setMinimumSize(new java.awt.Dimension(120, 80));
    btnCloseAll.setMultiClickThreshhold(1000L);
    btnCloseAll.setPreferredSize(new java.awt.Dimension(120, 80));
    btnCloseAll.setRequestFocusEnabled(false);
    btnCloseAll.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    btnCloseAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btnCloseAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCloseAllActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    dataToolbarPanel.add(btnCloseAll, gridBagConstraints);

    mainToolbar.add(dataToolbarPanel, java.awt.BorderLayout.WEST);

    toolbarPanel.add(mainToolbar, java.awt.BorderLayout.CENTER);

    getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

    mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    mainPanel.setLayout(new java.awt.BorderLayout());

    dataPanel.setLayout(new java.awt.BorderLayout(4, 0));

    dataListPanel.setMinimumSize(new java.awt.Dimension(350, 84));
    dataListPanel.setPreferredSize(new java.awt.Dimension(350, 10));
    dataListPanel.setLayout(new java.awt.GridBagLayout());

    tableScrollPane.setAutoscrolls(true);
    tableScrollPane.setFocusable(false);

    metaDataTable.setAutoCreateRowSorter(true);
    metaDataTable.setModel(new MetaDataTableModel());
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
    searchNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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
    searchDateFromLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    searchTermsPanel.add(searchDateFromLabel, gridBagConstraints);

    searchDateFromTextField.setMinimumSize(new java.awt.Dimension(120, 28));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
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

    searchDateToTextField.setMaximumSize(new java.awt.Dimension(300, 28));
    searchDateToTextField.setMinimumSize(new java.awt.Dimension(120, 28));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    searchTermsPanel.add(searchDateToTextField, gridBagConstraints);

    searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search-16.png"))); // NOI18N
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

    dataViewsPanel.setLayout(new java.awt.GridBagLayout());
    dataScrollPane.setViewportView(dataViewsPanel);

    dataContainerPanel.add(dataScrollPane, java.awt.BorderLayout.CENTER);

    infoPanel.setLayout(new java.awt.BorderLayout());

    summaryPanel.setVisible(false);
    summaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    summaryPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

    lblCountOpen.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblCountOpen.setForeground(DataFrame.AXIS_COLOR);
    lblCountOpen.setText(bundle.getString("lblCountOpen")); // NOI18N
    summaryPanel.add(lblCountOpen);

    lblCountOpenVal.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblCountOpenVal.setForeground(DataFrame.AXIS_COLOR);
    lblCountOpenVal.setText("--");
    summaryPanel.add(lblCountOpenVal);

    lblSpacer4.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacer4.setForeground(DataFrame.AXIS_COLOR);
    lblSpacer4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacer4.setText("|");
    lblSpacer4.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacer4.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacer4.setPreferredSize(new java.awt.Dimension(14, 14));
    summaryPanel.add(lblSpacer4);

    lblCountSelected.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblCountSelected.setForeground(DataFrame.AXIS_COLOR);
    lblCountSelected.setText(bundle.getString("lblCountSelected")); // NOI18N
    summaryPanel.add(lblCountSelected);

    lblCountSelecetedVal.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblCountSelecetedVal.setForeground(DataFrame.AXIS_COLOR);
    lblCountSelecetedVal.setText("--");
    summaryPanel.add(lblCountSelecetedVal);

    lblSpacer.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacer.setForeground(DataFrame.AXIS_COLOR);
    lblSpacer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacer.setText("|");
    lblSpacer.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacer.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacer.setPreferredSize(new java.awt.Dimension(14, 14));
    summaryPanel.add(lblSpacer);

    lblSleepDuration.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDuration.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDuration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDuration.setText(bundle.getString("lblSleepDuration")); // NOI18N
    summaryPanel.add(lblSleepDuration);

    lblSleepDurationMin.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationMin.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationMin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDurationMin.setText(bundle.getString("lblSleepDurationMin")); // NOI18N
    summaryPanel.add(lblSleepDurationMin);

    lblSleepDurationMinValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationMinValue.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationMinValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationMinValue);

    lblSpacer1.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacer1.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSpacer1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacer1.setText("-");
    lblSpacer1.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacer1.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacer1.setPreferredSize(new java.awt.Dimension(14, 14));
    summaryPanel.add(lblSpacer1);

    lblSleepDurationMax.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationMax.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationMax.setText(bundle.getString("lblSleepDurationMax")); // NOI18N
    summaryPanel.add(lblSleepDurationMax);

    lblSleepDurationMaxValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationMaxValue.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationMaxValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationMaxValue);

    lblSpacer3.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacer3.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSpacer3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacer3.setText("/");
    lblSpacer3.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacer3.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacer3.setPreferredSize(new java.awt.Dimension(14, 14));
    summaryPanel.add(lblSpacer3);

    lblSleepDurationAvg.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationAvg.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationAvg.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblSleepDurationAvg.setText(bundle.getString("lblSleepDurationAvg")); // NOI18N
    summaryPanel.add(lblSleepDurationAvg);

    lblSleepDurationAvgValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSleepDurationAvgValue.setForeground(DataFrame.SLEEP_DURATION_PAINT);
    lblSleepDurationAvgValue.setText("--:--:--");
    summaryPanel.add(lblSleepDurationAvgValue);

    lblSpacer2.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblSpacer2.setForeground(DataFrame.AXIS_COLOR);
    lblSpacer2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblSpacer2.setText("|");
    lblSpacer2.setMaximumSize(new java.awt.Dimension(14, 14));
    lblSpacer2.setMinimumSize(new java.awt.Dimension(14, 14));
    lblSpacer2.setPreferredSize(new java.awt.Dimension(14, 14));
    summaryPanel.add(lblSpacer2);

    lblTimeSavings.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblTimeSavings.setForeground(DataFrame.WAKE_PAINT);
    lblTimeSavings.setText(bundle.getString("lblTimeSaving")); // NOI18N
    summaryPanel.add(lblTimeSavings);

    lblTimeSavingsValue.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblTimeSavingsValue.setForeground(DataFrame.WAKE_PAINT);
    lblTimeSavingsValue.setText("--:--");
    summaryPanel.add(lblTimeSavingsValue);

    infoPanel.add(summaryPanel, java.awt.BorderLayout.NORTH);

    legendPanel.setVisible(false);
    legendPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    legendPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

    lblLegendSleepStartColor.setBackground(DataFrame.SLEEP_MARKER_PAINT);
    lblLegendSleepStartColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendSleepStartColor.setText("  ");
    lblLegendSleepStartColor.setOpaque(true);
    legendPanel.add(lblLegendSleepStartColor);

    lblLegendSleepStart.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendSleepStart.setForeground(DataFrame.SLEEP_MARKER_PAINT);
    lblLegendSleepStart.setText(bundle.getString("lblLegendSleepStart")); // NOI18N
    legendPanel.add(lblLegendSleepStart);

    lblLegendWakeTimeColor.setBackground(DataFrame.WAKE_PAINT);
    lblLegendWakeTimeColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendWakeTimeColor.setText("  ");
    lblLegendWakeTimeColor.setOpaque(true);
    legendPanel.add(lblLegendWakeTimeColor);

    lblLegendWakeTime.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendWakeTime.setForeground(DataFrame.WAKE_PAINT);
    lblLegendWakeTime.setText(bundle.getString("lblLegendWakeupTime")); // NOI18N
    legendPanel.add(lblLegendWakeTime);

    lblLegendWakeIntervalColor.setBackground(DataFrame.WAKE_INTERVALL_PAINT);
    lblLegendWakeIntervalColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendWakeIntervalColor.setText("  ");
    lblLegendWakeIntervalColor.setOpaque(true);
    legendPanel.add(lblLegendWakeIntervalColor);

    lblLegendWakeInterval.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendWakeInterval.setForeground(DataFrame.WAKE_INTERVALL_END_PAINT);
    lblLegendWakeInterval.setText(bundle.getString("lblLegendWakeInterval")); // NOI18N
    legendPanel.add(lblLegendWakeInterval);

    lblLegendKeyColor.setBackground(DataFrame.KEY_PAINT);
    lblLegendKeyColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendKeyColor.setText("  ");
    lblLegendKeyColor.setOpaque(true);
    legendPanel.add(lblLegendKeyColor);

    lblLegendKey.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendKey.setForeground(DataFrame.KEY_PAINT);
    lblLegendKey.setText(bundle.getString("lblLegendKeys")); // NOI18N
    legendPanel.add(lblLegendKey);

    lblLegendSnoozeColor.setBackground(DataFrame.SNOOZE_PAINT);
    lblLegendSnoozeColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendSnoozeColor.setText("  ");
    lblLegendSnoozeColor.setOpaque(true);
    legendPanel.add(lblLegendSnoozeColor);

    lblLegendSnooze.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendSnooze.setForeground(DataFrame.SNOOZE_PAINT);
    lblLegendSnooze.setText(bundle.getString("lblLegendSnooze")); // NOI18N
    legendPanel.add(lblLegendSnooze);

    lblLegendMovementColor.setBackground(DataFrame.BAR_COLOR);
    lblLegendMovementColor.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendMovementColor.setText("  ");
    lblLegendMovementColor.setOpaque(true);
    legendPanel.add(lblLegendMovementColor);

    lblLegendMovement.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
    lblLegendMovement.setForeground(DataFrame.BAR_COLOR);
    lblLegendMovement.setText(bundle.getString("lblLegendMovements")); // NOI18N
    legendPanel.add(lblLegendMovement);

    infoPanel.add(legendPanel, java.awt.BorderLayout.SOUTH);

    dataContainerPanel.add(infoPanel, java.awt.BorderLayout.SOUTH);

    dataPanel.add(dataContainerPanel, java.awt.BorderLayout.CENTER);

    mainPanel.add(dataPanel, java.awt.BorderLayout.CENTER);

    getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

    statusTextPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 1, 1));
    statusTextPanel.setLayout(new java.awt.BorderLayout(5, 5));

    statusTextLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/link-broken-16.png"))); // NOI18N
    statusTextPanel.add(statusTextLabel, java.awt.BorderLayout.CENTER);

    statusProgressBar.setFocusable(false);
    statusProgressBar.setMaximumSize(new java.awt.Dimension(200, 20));
    statusProgressBar.setMinimumSize(new java.awt.Dimension(20, 20));
    statusProgressBar.setPreferredSize(new java.awt.Dimension(200, 20));
    statusProgressBar.setRequestFocusEnabled(false);
    statusProgressBar.putClientProperty("JProgressBar.style", "circular");
    statusTextPanel.add(statusProgressBar, java.awt.BorderLayout.EAST);

    getContentPane().add(statusTextPanel, java.awt.BorderLayout.SOUTH);

    fileMenu.setText(bundle.getString("menu.file")); // NOI18N

    viewMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/chart-bar-16.png"))); // NOI18N
    viewMenuItem.setText(bundle.getString("menu.file.view")); // NOI18N
    viewMenuItem.setDoubleBuffered(true);
    viewMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(viewMenuItem);

    deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin-16.png"))); // NOI18N
    deleteMenuItem.setText(bundle.getString("menu.file.delete")); // NOI18N
    deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(deleteMenuItem);

    uploadSoundPackageMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/music-16.png"))); // NOI18N
    uploadSoundPackageMenuItem.setText(bundle.getString("btnSoundPkgImport.text")); // NOI18N
    uploadSoundPackageMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        uploadSoundPackageMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(uploadSoundPackageMenuItem);

    if (OS.Mac.isCurrent())
    jSeparator1.setVisible(false);
    fileMenu.add(jSeparator1);

    if (OS.Mac.isCurrent())
    prefsMenuItem.setVisible(false);
    prefsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/wrench-16.png"))); // NOI18N
    prefsMenuItem.setText(bundle.getString("menu.file.prefs")); // NOI18N
    prefsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        prefsMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(prefsMenuItem);

    if (OS.Mac.isCurrent())
    jSeparator3.setVisible(false);
    fileMenu.add(jSeparator3);

    if (OS.Mac.isCurrent())
    exitMenuItem.setVisible(false);
    exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/button-power-16.png"))); // NOI18N
    exitMenuItem.setText(bundle.getString("menu.file.exit")); // NOI18N
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    deviceMenu.setText(bundle.getString("menu.device")); // NOI18N

    readStoredDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/inbox-16.png"))); // NOI18N
    readStoredDataMenuItem.setText(bundle.getString("menu.device.readStoredData")); // NOI18N
    readStoredDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        readStoredDataMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(readStoredDataMenuItem);

    clearDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/bin-16.png"))); // NOI18N
    clearDataMenuItem.setText(bundle.getString("menu.device.clearData")); // NOI18N
    clearDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearDataMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(clearDataMenuItem);

    setClockDateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/clock-16.png"))); // NOI18N
    setClockDateMenuItem.setText(bundle.getString("menu.device.setDate")); // NOI18N
    setClockDateMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setClockDateMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(setClockDateMenuItem);

    readStatusMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/information-16.png"))); // NOI18N
    readStatusMenuItem.setText(bundle.getString("menu.device.readStatus")); // NOI18N
    readStatusMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        readStatusMenuItemActionPerformed(evt);
      }
    });
    deviceMenu.add(readStatusMenuItem);

    resetClockMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/button-synchronize-16.png"))); // NOI18N
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
    int result
        = showOptionMessage(BundleUtil.getMessage("message.confirmReset"),
            BundleUtil.getMessage("infoMessageBox.title"));

    if (result == JOptionPane.OK_OPTION) {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboReset(
          this));
    }
  }//GEN-LAST:event_resetClockMenuItemActionPerformed

  private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteMenuItemActionPerformed
  {//GEN-HEADEREND:event_deleteMenuItemActionPerformed
    if (metaDataTable.getSelectedRowCount() > 0) {
      int response = showOptionMessage(BundleUtil.getMessage(
          "notification.message.delete"), BundleUtil.getMessage(
              "infoMessageBox.title"));
      if (response == JOptionPane.OK_OPTION) {
        int selectedRows[] = metaDataTable.getSelectedRows();
        ArrayList<SleepData> tmpSleepData = new ArrayList<>(Array
            .getLength(selectedRows));
        for (int selectedRowIdx : selectedRows) {
          tmpSleepData.add(getMetaDataTableModel().getSleepDataAt(
              metaDataTable.convertRowIndexToModel(selectedRowIdx)));
        }
        for (SleepData sleepData : tmpSleepData) {
          ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
              new SleepDataDelete(
                  this, sleepData));
        }
      }
    } else {
      showMessage(BundleUtil.getErrorMessage(
          "MetaDataTableModel.nothingSelected"), true);
    }
  }//GEN-LAST:event_deleteMenuItemActionPerformed

  private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
  {//GEN-HEADEREND:event_formWindowClosed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new AxboDisconnect(
            this));
  }//GEN-LAST:event_formWindowClosed

  private void loadDataButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadDataButtonActionPerformed
  {//GEN-HEADEREND:event_loadDataButtonActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new SleepDataImport(
            this));
  }//GEN-LAST:event_loadDataButtonActionPerformed

  private void clearDataMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearDataMenuItemActionPerformed
  {//GEN-HEADEREND:event_clearDataMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboClear(
        this));
  }//GEN-LAST:event_clearDataMenuItemActionPerformed

  private void closeAllMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeAllMenuItemActionPerformed
  {//GEN-HEADEREND:event_closeAllMenuItemActionPerformed
    for (final Component component : dataViewsPanel.getComponents()) {
      if (component instanceof DataFrame)
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new DiagramClose(
                log, (DataFrame) component));
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
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new SleepDataImport(
            this));
  }//GEN-LAST:event_readStoredDataMenuItemActionPerformed

  private void prefsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_prefsMenuItemActionPerformed
  {//GEN-HEADEREND:event_prefsMenuItemActionPerformed
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new PrefsOpen(
        this, this));
  }//GEN-LAST:event_prefsMenuItemActionPerformed

  private void metaDataTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_metaDataTableMouseClicked
  {//GEN-HEADEREND:event_metaDataTableMouseClicked
    if (evt.getButton() == MouseEvent.BUTTON3) {
      int row = metaDataTable.rowAtPoint(evt.getPoint());
      metaDataTable.getSelectionModel().addSelectionInterval(row, row);
    }

    // is it a double click, open a new internal frame with the selected sleep data
    if (evt.getClickCount() == 2) {
      int[] selectedRows = metaDataTable.getSelectedRows();
      final List<SleepData> sleepDataList = new ArrayList<>();
      for (int row : selectedRows) {
        sleepDataList.add(getMetaDataTableModel().getSleepDataAt(metaDataTable.
            convertRowIndexToModel(row)));
      }
      if (sleepDataList.size() > 0) {
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new SleepDataOpen(
                this, sleepDataList));
      }
    }
  }//GEN-LAST:event_metaDataTableMouseClicked

  private void searchTextFieldsKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_searchTextFieldsKeyPressed
  {//GEN-HEADEREND:event_searchTextFieldsKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
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
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new ApplicationExit(
              this));
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void deletePopupMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deletePopupMenuItemActionPerformed
    {//GEN-HEADEREND:event_deletePopupMenuItemActionPerformed
      deleteMenuItemActionPerformed(evt);
}//GEN-LAST:event_deletePopupMenuItemActionPerformed

    private void viewMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_viewMenuItemActionPerformed
    {//GEN-HEADEREND:event_viewMenuItemActionPerformed
      int[] selectedRows = metaDataTable.getSelectedRows();
      if (selectedRows.length < 1) {
        showMessage(BundleUtil.getErrorMessage(
            "MetaDataTableModel.nothingSelected"), true);
        return;
      }
      final List<SleepData> sleepDataList = new ArrayList<>(
          selectedRows.length);
      for (int selectedRowIdx : selectedRows) {
        sleepDataList.add(getMetaDataTableModel().getSleepDataAt(metaDataTable.
            convertRowIndexToModel(selectedRowIdx)));
      }
      if (sleepDataList.size() > 0) {
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new SleepDataOpen(
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

    private void uploadSoundPackageMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_uploadSoundPackageMenuItemActionPerformed
    {//GEN-HEADEREND:event_uploadSoundPackageMenuItemActionPerformed

      final FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(final File f) {
          return f.getName().toLowerCase(Locale.ENGLISH)
              .contains(Axbo.SOUND_DATA_FILE_EXT.
                  toLowerCase()) || f.isDirectory();
        }

        @Override
        public String getDescription() {
          return "Axbo Sound Package Files";
        }
      };

      // open file chooser for directory with sleep data files
      JFileChooser chooser = new JFileChooser(Axbo.SOUND_PACKAGES_DIR);
      chooser.setFileFilter(filter);
      chooser.setMultiSelectionEnabled(false);
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int returnVal = chooser.showOpenDialog(this);

      // process selected directory
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new SoundPackageUpload(this, chooser.getSelectedFile()));
      }

    }//GEN-LAST:event_uploadSoundPackageMenuItemActionPerformed

private void btnCompareActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCompareActionPerformed
{//GEN-HEADEREND:event_btnCompareActionPerformed
  ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
      new SleepDataCompare(
          this));
}//GEN-LAST:event_btnCompareActionPerformed

private void btnCloseAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCloseAllActionPerformed
{//GEN-HEADEREND:event_btnCloseAllActionPerformed
  closeAllMenuItemActionPerformed(evt);
}//GEN-LAST:event_btnCloseAllActionPerformed

private void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
{//GEN-HEADEREND:event_btnPrintActionPerformed
  if (getDataViews().size() > 0) {
    final PrinterJob job = PrinterJob.getPrinterJob();
    if (job.printDialog()) {
      final Book book = new Book();
      job.setPageable(book);
      job.setJobName("aXbo");
      ApplicationEventDispatcher.getInstance().dispatchEvent(new DiagramPrint(
          this, job, book));
      try {
        job.print();
      } catch (PrinterException ex) {
        final String msg = BundleUtil.getErrorMessage(
            "globalError.printingFailed");
        log.error(msg, ex);
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
            new ApplicationMessageEvent(
                this, msg, true));
      }
    }
  }
}//GEN-LAST:event_btnPrintActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnCloseAll;
  private javax.swing.JButton btnCompare;
  private javax.swing.JButton btnPrint;
  private javax.swing.JMenuItem clearDataMenuItem;
  private javax.swing.JPanel dataContainerPanel;
  private javax.swing.JPanel dataListPanel;
  private javax.swing.JPanel dataPanel;
  private javax.swing.JScrollPane dataScrollPane;
  private javax.swing.JPanel dataToolbarPanel;
  private com.dreikraft.swing.BackgroundImagePanel dataViewsPanel;
  private javax.swing.JMenuItem deleteMenuItem;
  private javax.swing.JMenuItem deletePopupMenuItem;
  private javax.swing.JMenu deviceMenu;
  private javax.swing.JMenuItem exitMenuItem;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JPanel infoPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JLabel lblCountOpen;
  private javax.swing.JLabel lblCountOpenVal;
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
  private javax.swing.JLabel lblSpacer;
  private javax.swing.JLabel lblSpacer1;
  private javax.swing.JLabel lblSpacer2;
  private javax.swing.JLabel lblSpacer3;
  private javax.swing.JLabel lblSpacer4;
  private javax.swing.JLabel lblTimeSavings;
  private javax.swing.JLabel lblTimeSavingsValue;
  private javax.swing.JPanel legendPanel;
  private javax.swing.JButton loadDataButton;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JPanel mainToolbar;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JTable metaDataTable;
  private javax.swing.JPanel navToolbarPanel;
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
  private javax.swing.JPanel spacerPanel;
  private javax.swing.JProgressBar statusProgressBar;
  private javax.swing.JLabel statusTextLabel;
  private javax.swing.JPanel statusTextPanel;
  private javax.swing.JPanel summaryPanel;
  private javax.swing.JScrollPane tableScrollPane;
  private javax.swing.JPanel toolbarPanel;
  private javax.swing.JMenuItem uploadSoundPackageMenuItem;
  private javax.swing.JMenuItem viewMenuItem;
  private javax.swing.JMenuItem viewPopupMenuItem;
  // End of variables declaration//GEN-END:variables
}
