package com.dreikraft.axbo.controller;

import apple.dts.samplecode.osxadapter.OSXAdapter;
import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.events.ApplicationExit;
import com.dreikraft.events.ApplicationInitialize;
import com.dreikraft.events.ApplicationInitialized;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.OS;
import com.dreikraft.axbo.data.AxboInfo;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.data.SleepDataComparator;
import com.dreikraft.axbo.events.AxboClear;
import com.dreikraft.axbo.events.AxboDisconnect;
import com.dreikraft.axbo.events.AxboFind;
import com.dreikraft.axbo.events.AxboFound;
import com.dreikraft.axbo.events.AxboReset;
import com.dreikraft.axbo.events.AxboStatusGet;
import com.dreikraft.axbo.events.AxboStatusGot;
import com.dreikraft.axbo.events.AxboTest;
import com.dreikraft.axbo.events.AxboTimeSet;
import com.dreikraft.axbo.events.DataSearch;
import com.dreikraft.axbo.events.DiagramClose;
import com.dreikraft.axbo.events.DiagramZoom;
import com.dreikraft.axbo.events.SoundPackageUpload;
import com.dreikraft.axbo.events.PrefsOpen;
import com.dreikraft.axbo.events.SleepDataAdded;
import com.dreikraft.axbo.events.DiagramClosed;
import com.dreikraft.axbo.events.SleepDataCompare;
import com.dreikraft.axbo.events.SleepDataDelete;
import com.dreikraft.axbo.events.SleepDataImport;
import com.dreikraft.axbo.events.SleepDataImported;
import com.dreikraft.axbo.events.SleepDataLoad;
import com.dreikraft.axbo.events.SleepDataOpen;
import com.dreikraft.axbo.events.SleepDataSave;
import com.dreikraft.axbo.events.SoundUpload;
import com.dreikraft.axbo.gui.AxboFrame;
import com.dreikraft.axbo.gui.DataFrame;
import com.dreikraft.axbo.task.AxboClearTask;
import com.dreikraft.axbo.task.AxboFindTask;
import com.dreikraft.axbo.task.AxboResetTask;
import com.dreikraft.axbo.task.AxboStatusGetTask;
import com.dreikraft.axbo.task.AxboTask;
import com.dreikraft.axbo.task.AxboTestTask;
import com.dreikraft.axbo.task.AxboTimeSetTask;
import com.dreikraft.axbo.task.SleepDataImportTask;
import com.dreikraft.axbo.task.SleepDataLoadTask;
import com.dreikraft.axbo.task.SoundPackageUploadTask;
import com.dreikraft.axbo.util.BundleUtil;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.commons.logging.*;
import org.apache.commons.logging.Log;

/**
 * AxboFrameController
 *
 * @author jan.illetschko@3kraft.com
 */
public final class AxboFrameController implements ApplicationEventEnabled {

  public static final int MAX_OPEN_DIAGRAMS = 30;
  public static final Log log = LogFactory.getLog(AxboFrameController.class);
  public static final String MINUTE_CLASS = "org.jfree.data.time.Minute";
  public static final String SECOND_CLASS = "org.jfree.data.time.Second";
  private final AxboFrame frame;
  private boolean taskInProgress = false;

  @SuppressWarnings("LeakingThisInConstructor")
  public AxboFrameController() {
    frame = new AxboFrame();

    // application events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationInitialize.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationInitialized.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationExit.class, this);
  }

  public void handle(final ApplicationInitialize evt) {
    // register events

    // message events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationMessageEvent.class, this);

    // view events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DataSearch.class, this);

    // data events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboDisconnect.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboFind.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboFound.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboStatusGet.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboStatusGot.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboReset.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboTest.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboTimeSet.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        AxboClear.class, this);

    // diagram events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramClosed.class, this);

    // sleep data events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataLoad.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataAdded.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataDelete.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataImport.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataImported.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataOpen.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataCompare.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataSave.class, this);

    // sound events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SoundPackageUpload.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SoundUpload.class, this);

    // create model and view objects
    frame.init();

    // enable view
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new ApplicationInitialized(
        this));

    // load stored data
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataLoad(
        this));
  }

  public void handle(final ApplicationInitialized evt) {
    registerForMacOSXEvents();
    frame.setSize(1024, 768);
    frame.setVisible(true);
  }

  public void handle(final ApplicationExit evt) {
    exit();
  }

  public void handle(final ApplicationMessageEvent evt) {
    if (evt.isError()) {
      frame.showMessage(evt.getMessage(), true);
    } else {
      frame.showStatusMessage(evt.getMessage());
    }
  }

  public void handle(final AxboDisconnect evt) {
    DeviceContext.getDeviceType().getDataInterface().stop();
  }

  public void handle(final AxboFind evt) {
    if (taskInProgress) {
      return;
    }
    final AxboFindTask task = new AxboFindTask(evt.getFollowUpTask());
    task.addPropertyChangeListener(new TaskProgressListener(frame, BundleUtil.
        getMessage("statusLabel.findAxbo"), BundleUtil.getErrorMessage(
        "info.axboFound"),
        BundleUtil.getErrorMessage("globalError.axboNotFound"), false));
    task.execute();
  }

  public void handle(final AxboFound evt) {
    if (evt.getPortName() != null && evt.getFollowUpTask() != null) {
      evt.getFollowUpTask().execute();
    }
  }

  public void handle(final AxboReset evt) {
    final AxboResetTask task = new AxboResetTask();
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.resetingAxbo"),
        BundleUtil.getMessage("statusLabel.resetedAxbo"),
        BundleUtil.getErrorMessage("globalError.failedToResetClock"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final AxboTest evt) {
    final AxboTestTask task = new AxboTestTask((byte) 0x08);
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.testingAxbo"),
        BundleUtil.getMessage("statusLabel.testedAxbo"),
        BundleUtil.getErrorMessage("globalError.failedToTestAxbo"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final AxboTimeSet evt) {
    final AxboTimeSetTask task = new AxboTimeSetTask();
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.setClockDate"),
        BundleUtil.getMessage("statusLabel.setClockDateSucceeded"),
        BundleUtil.getErrorMessage("globalError.failedToSetDate"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final AxboClear evt) {
    final AxboClearTask task = new AxboClearTask();
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.clearClockData"),
        BundleUtil.getMessage("statusLabel.clearedClockData"),
        BundleUtil.getErrorMessage("globalError.failedToClearClockData"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final AxboStatusGet evt) {
    final AxboStatusGetTask task = new AxboStatusGetTask();
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.getClockStatus"),
        BundleUtil.getMessage("statusLabel.gotClockStatus"),
        BundleUtil.getErrorMessage("globalError.failedToReadStatus"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final AxboStatusGot evt) {
    DeviceContext.getDeviceType().getDataInterface().stop();
    frame.setStatusProgressBarIndeterminate(false);
    frame.showDeviceDisabled();
    final AxboInfo infoData = evt.getInfoData();
    if (infoData != null) {
      final StringBuilder msg = new StringBuilder();
      if (infoData.getSerialNumber() != null && infoData.getSerialNumber().
          length() > 0) {
        msg.append(BundleUtil.getMessage("axboData.serialNumber.label"));
        msg.append(": ");
        msg.append(infoData.getSerialNumber()).append("\n");
      }
      msg.append(BundleUtil.getMessage("axboData.hardwareVersion.label"));
      msg.append(": ");
      msg.append(infoData.getHardwareVersion()).append("\n");
      msg.append(BundleUtil.getMessage("axboData.softwareVersion.label"));
      msg.append(": ");
      msg.append(infoData.getSoftwareVersion()).append("\n");
      msg.append(BundleUtil.getMessage("axboData.rtc.label"));
      msg.append(": ");
      msg.append(infoData.getRtcCalibration());
      frame.showMessage(msg.toString(), false);
    } else {
      frame.showStatusMessage("");
      frame.showMessage(BundleUtil.getErrorMessage(
          "globalError.failedToReadStatus"),
          true);
    }
  }

  public void handle(final SleepDataLoad evt) {
    frame.showStatusMessage(BundleUtil.getMessage("statusLabel.loadProject"));

    final File dir = new File(Axbo.PROJECT_DIR_DEFAULT);
    if (log.isDebugEnabled()) {
      log.debug("open project directory: " + dir.getAbsolutePath());
    }
    final File[] files = dir.listFiles(new Axbo.SPWFilenameFilter());
    if (log.isDebugEnabled()) {
      log.debug(files.length + " axbo sleep data files found");
    }

    // load files
    if (files.length > 0) {
      final SleepDataLoadTask task = new SleepDataLoadTask(files);
      task.addPropertyChangeListener(new TaskProgressListener(frame,
          BundleUtil.getMessage("statusLabel.loadProject"),
          MessageFormat.format(BundleUtil
          .getMessage("statusLabel.projectLoaded"), files.length),
          MessageFormat.format(BundleUtil
          .getMessage("statusLabel.projectLoaded"), 0), false));
      task.execute();
    }
  }

  public void handle(final SleepDataDelete evt) {
    final SleepData sleepData = evt.getSleepData();
    if (!sleepData.getDataFile().delete())
      log.warn("failed to delete sleep data file: " + sleepData.getDataFile()
          .getAbsolutePath());
    frame.getMetaDataTableModel().removeSleepData(sleepData);

    final DataFrame dataView = getDataViewForSleepData(sleepData);
    if (dataView != null) {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new DiagramClose(
          this, dataView));
    }
  }

  public void handle(final SleepDataAdded evt) {
    frame.getMetaDataTableModel().addSleepData(evt.getSleepData());
  }

  public void handle(final SleepDataImport evt) {
    final SleepDataImportTask task = new SleepDataImportTask(
        frame.getMetaDataTableModel().getData());
    task.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.importSleepData"),
        BundleUtil.getMessage("statusLabel.importedSleepData"),
        BundleUtil.getErrorMessage("globalError.failedToStoreClockData"),
        true));
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        task));
  }

  public void handle(final SleepDataImported evt) {
    final Integer newCount = evt.getNewSleepDataCount();
    if (newCount != -1) {
      frame.showStatusMessage(BundleUtil.getMessage(
          "statusLabel.importedSleepData", newCount));
    } else {
      final String msg = BundleUtil.getErrorMessage(
          "globalError.failedToStoreClockData");
      frame.showStatusMessage(BundleUtil.getMessage(
          "statusLabel.importedSleepData", 0));
      frame.showMessage(msg, true);
    }
  }

  public void handle(final DiagramClosed evt) {
    ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
        DiagramClose.class, evt.getDataViewController());
    frame.updateDataViewsPanel();
    calculateSummary();
  }

  public void handle(final SleepDataOpen evt) {
    final List<SleepData> selectedSleepDataList = evt.getSleepDataList();
    final List<SleepData> openSleepDataList = getOpenSleepDataList();
    int countNew = 0;
    SleepData curSleepData = null;
    for (final SleepData sleepData : selectedSleepDataList) {
      curSleepData = sleepData;
      if (!openSleepDataList.contains(sleepData)) {
        // open select Sleepdates
        final DataFrameController dataViewCtrl =
            new DataFrameController(sleepData);
        dataViewCtrl.init();
        frame.addDataView(dataViewCtrl.getView());
        countNew++;
      }
      if (openSleepDataList.size() + countNew > MAX_OPEN_DIAGRAMS) {
        ApplicationEventDispatcher.getInstance().dispatchEvent(
            new ApplicationMessageEvent(
            this, BundleUtil.getErrorMessage("globalError.toManyOpenDiagrams"),
            true));
        break;
      }
    }

    // scroll to last opened data view
    frame.jumpToDataView(getDataViewForSleepData(curSleepData));

    // show summary for open sleep dates
    calculateSummary();
  }

  private void calculateSummary() {
    final List<SleepData> openSleepDataList = getOpenSleepDataList();

    long sumDuration = 0;
    long minDuration = 1000 * 60 * 60 * 24;
    long maxDuration = 0;
    long saving = 0;
    int count = 0;
    for (final SleepData sleepData : openSleepDataList) {
      if (sleepData.getWakeupTime() != null && !sleepData.isPowerNap()) {
        count++;
        final long duration = sleepData.calculateDuration();
        sumDuration += duration;
        saving += sleepData.calculateTimeSaving();
        if (duration < minDuration) {
          minDuration = duration;
        }
        if (duration > maxDuration) {
          maxDuration = duration;
        }
      }
    }

    final long avgDuration = count > 0 ? sumDuration / count : 0;
    frame
        .showSummary(sumDuration, avgDuration, minDuration, maxDuration, saving,
        count);
  }

  private List<SleepData> getOpenSleepDataList() {
    final List<SleepData> openSleepDataList = new ArrayList<SleepData>();
    for (final DataFrame dataView : frame.getDataViews()) {
      openSleepDataList.add(dataView.getSleepData());
    }
    return openSleepDataList;
  }

  private DataFrame getDataViewForSleepData(final SleepData sleepData) {
    for (final DataFrame dataView : frame.getDataViews()) {
      if (dataView.getSleepData().equals(sleepData)) {
        return dataView;
      }
    }
    return null;
  }

  public void handle(final SleepDataCompare evt) {
    final List<SleepData> openSleepDataList = getOpenSleepDataList();
    if (openSleepDataList.size() > 1) {
      Collections.sort(openSleepDataList, new SleepDataComparator());
      int maxInterval = openSleepDataList.get(openSleepDataList.size() - 1).
          getStartHour() - openSleepDataList.get(0).getStartHour();

      if (maxInterval > 12) {
        for (SleepData sleepData : openSleepDataList) {
          int offset = sleepData.getStartHour() < 12 ? 24 : 0;
          sleepData.setStartHour(sleepData.getStartHour() + offset);
        }
        Collections.sort(openSleepDataList, new SleepDataComparator());
      }

      // get the start
      int minStartHour = openSleepDataList.get(0).getStartHour();

      // get latest end time
      int maxEndHour = 0;
      for (SleepData sleepData : openSleepDataList) {
        int endHour = sleepData.calculateEndHour();
        if (endHour > maxEndHour) {
          maxEndHour = endHour;
        }
      }

      for (final SleepData sleepData : openSleepDataList) {
        ApplicationEventDispatcher.getInstance().dispatchEvent(new DiagramZoom(
            this, minStartHour + ":01", (maxEndHour - minStartHour) * 60));

        final String msgParam = new StringBuffer(sleepData.getName()).append(
            " ").append(DateFormat.getDateInstance(DateFormat.SHORT).format(
            sleepData.
            calculateStartTime())).toString();
        final String msg = BundleUtil.getMessage("statusLabel.compareData",
            msgParam);
        ApplicationEventDispatcher.getInstance().dispatchEvent(
            new ApplicationMessageEvent(
            this, msg, false));
      }
    }
  }

  public void handle(final SleepDataSave evt) {
    final SleepData sleepData = evt.getSleepData();
    try {
      final XMLEncoder encoder =
          new XMLEncoder(new BufferedOutputStream(new FileOutputStream(sleepData
          .getDataFile())));
      encoder.writeObject(sleepData);
      encoder.close();
    } catch (FileNotFoundException ex) {
      log.error("failed to save file" + sleepData.getDataFile(), ex);
    }
  }

  public void handle(final DataSearch evt) {
    // recalculate the table model
    frame.getMetaDataTableModel().filterData(evt.getName(), evt.getFrom(), evt.
        getTo());
  }

  /**
   * Upload a sound package from file to aXbo.
   *
   * @param evt a sound package upload event was initiated by the user
   */
  public void handle(final SoundPackageUpload evt) {

    // prepare upload task
    final SoundPackageUploadTask uploadTask =
        new SoundPackageUploadTask(evt.getSoundPackageFile());
    // register progress bar updates
    uploadTask.addPropertyChangeListener(new TaskProgressListener(frame,
        BundleUtil.getMessage("statusLabel.uploadSoundPackage"),
        MessageFormat.format(BundleUtil.getMessage(
        "statusLabel.uploadSoundPackageSuccess"), evt.getSoundPackageFile()
        .getName()),
        BundleUtil.getErrorMessage("globalError.uploadFailed"),
        false));
    // find aXbo first, then execute upload
    ApplicationEventDispatcher.getInstance().dispatchEvent(new AxboFind(this,
        uploadTask));
  }

  /**
   * A new sound gets uploaded from a sound package to aXbo.
   *
   * @param evt a new upload event
   */
  public void handle(final SoundUpload evt) {
    frame.showStatusMessage(BundleUtil.getMessage(
        "statusLabel.uploadProgress", evt.getSoundName()));
  }

  private void registerForMacOSXEvents() {
    if (OS.Mac.isCurrent()) {
      try {
        OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exit",
            (Class[]) null));
        OSXAdapter.setPreferencesHandler(this, getClass().
            getDeclaredMethod("showPrefs", (Class[]) null));
      } catch (NoSuchMethodException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  public void showPrefs() {
    ApplicationEventDispatcher.getInstance().dispatchEvent(new PrefsOpen(
        this, frame));
  }

  public void exit() {
    DeviceContext.getDeviceType().getDataInterface().stop();
    System.exit(0);
  }

  private class TaskProgressListener implements
      PropertyChangeListener {

    private final AxboFrame view;
    private final String msg;
    private final String successMsg;
    private final String failedMsg;
    private final boolean indeterminate;

    public TaskProgressListener(final AxboFrame view, final String msg,
        final String successMsg, final String failedMsg,
        final boolean indeterminate) {
      this.view = view;
      this.msg = msg;
      this.successMsg = successMsg;
      this.failedMsg = failedMsg;
      this.indeterminate = indeterminate;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {

      if ("progress".equals(evt.getPropertyName())) {
        if (!indeterminate) {
          view.setStatusProgressBarValue((Integer) evt.getNewValue());
        }
      } else if ("state".equals(evt.getPropertyName())) {
        if (SwingWorker.StateValue.STARTED.equals(evt.getNewValue())) {
          taskInProgress = true;
          view.showStatusMessage(msg);
          view.showDeviceEnabled();
          view.setStatusProgressBarValue(0);
          view.setStatusProgressBarStringPainted(!indeterminate);
          view.setStatusProgressBarIndeterminate(indeterminate);

        } else if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
          taskInProgress = false;
          view.showDeviceDisabled();
          view.setStatusProgressBarValue(0);
          view.setStatusProgressBarStringPainted(false);
          view.setStatusProgressBarIndeterminate(false);
        }
      } else if ("result".equals(evt.getPropertyName())) {
        if (AxboTask.Result.SUCCESS.equals(evt.getNewValue())) {
          view.showStatusMessage(successMsg);
        } else if (AxboTask.Result.FAILED.equals(evt.getNewValue())) {
          view.showMessage(failedMsg, true);
          view.showStatusMessage("");
        } else if (AxboTask.Result.INTERRUPTED.equals(evt.getNewValue())) {
          view.showMessage(failedMsg, true);
          view.showStatusMessage("");
        }
      }
    }
  }
}
