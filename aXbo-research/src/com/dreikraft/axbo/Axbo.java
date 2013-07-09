package com.dreikraft.axbo;

import com.dreikraft.axbo.controller.AxboFrameController;
import com.dreikraft.axbo.controller.PreferencesController;
import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.events.ApplicationInitialize;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.data.DeviceType;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.sound.SoundPackage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.logging.*;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.plaf.basic.BasicHeaderUI;
import org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI;
import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;


/**
 * aXbo startup class.
 *
 * @author jan.illetschko@3kraft.com
 */
public final class Axbo implements ApplicationEventEnabled {

  public static final Log log = LogFactory.getLog(Axbo.class);
  // default dirs
  public static final String APPLICATION_DIR = "aXbo";
  public static final String PROJECT_DIR_DEFAULT =
      System.getProperty("user.home") + System.getProperty("file.separator")
      + Axbo.APPLICATION_DIR + System.getProperty("file.separator") + "projects";
  public static final String SOUND_PACKAGES_DIR =
      System.getProperty("user.home") + System.getProperty("file.separator")
      + Axbo.APPLICATION_DIR + System.getProperty("file.separator") + "sounds";
  // file constants
  public static final String SOUND_DATA_FILE_EXT = ".axs";
  // images and icons
  public static final String BACKGROUND_IMAGE_DEFAULT =
      "/resources/images/background_soft.jpg";
  public static final String ICON_IMAGE_DEFAULT =
      "/resources/images/32x32px_researchicon.png";
  public static final String INTERNAL_ICON_IMAGE_DEFAULT =
      "/resources/images/32x32px_researchicon.png";
  // sleep data constants
  public static final int MAX_MOVEMENTS_DEFAULT = 100;
  public static final long CLEANER_INTERVAL_DEFAULT = 3 * 60 * 60 * 1000;
  public static final float AVERAGE_MOVEMENTS_THRESHOLD = 10;
  public static final long MINIMUM_SLEEP_DURATION = 30 * 60 * 1000;
  public static final int MINIMUM_MOVEMENTS = 100;
  // === preferences ===
  // serial port prefs
  public static final String SERIAL_PORT_NAME_PREF = "serialPort.name";
  // language prefs
  public static final String LANGUAGES_PREF = "languages";
  public static final String LANGUAGES_DEFAULT = "en,de,fr,ja,ru";
  public static final String LANGUAGE_PREF = "language";
  public static final String LANGUAGE_DEFAULT = "en";
  // diagramm prefs
  public static final String TIME_PERIOD_CLASS_PREF = "timePeriodClass";
  public static final String TIME_PERIOD_CLASS_DEFAULT =
      "org.jfree.data.time.Minute";
  // deviceType pref
  public static final String DEVICE_TYPE_PREF = "deviceType";
  public static final String DEVICE_TYPE_DEFAULT = "AXBO";
  public static final int COMPARE_OFFSET = 12;
  public static final String STANDALONE_UPDATER_ID = "349";
  public static final String SILENT_UPDATER_ID = "389";
  // === members ===
  // application singleton
  private static Axbo CONTROLLER = new Axbo();

  public static void main(final String[] args) {
    Axbo controller = getApplicationController();
    controller.init();
  }

  public static Axbo getApplicationController() {
    return Axbo.CONTROLLER;
  }

  public static Preferences getApplicationPreferences() {
    return Preferences.userNodeForPackage(Axbo.class);
  }

  public static String getPortName() {
    return getApplicationPreferences().get(DeviceContext.getDeviceType() + "."
        + Axbo.SERIAL_PORT_NAME_PREF, OS.get().getDefaultPort());
  }

  private Axbo() {
    super();
  }

  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public void init() {
    // disable the security manager
    System.setSecurityManager(null);

    // register shutdown hook
    Runtime.getRuntime().addShutdownHook(new AxboShutdownHook());

    // set desired locale
    try {
      String langsPref = getApplicationPreferences().get(LANGUAGES_PREF,
          LANGUAGES_DEFAULT);
      String langPref = getApplicationPreferences().get(LANGUAGE_PREF, "unset");
      if (!langPref.equals("unset")) {
        Locale.setDefault(new Locale(langPref));
      }
      if (langsPref.indexOf(Locale.getDefault().getLanguage()) == -1) {
        Locale.setDefault(new Locale(LANGUAGE_DEFAULT));
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    if (log.isDebugEnabled()) {
      log.debug("Current Locale: " + Locale.getDefault());
    }
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException ex) {
      log.warn("failed to set system look & feel", ex);
    } catch (InstantiationException ex) {
      log.warn("failed to set system look & feel", ex);
    } catch (IllegalAccessException ex) {
      log.warn("failed to set system look & feel", ex);
    } catch (UnsupportedLookAndFeelException ex) {
      log.warn("failed to set system look & feel", ex);
    }

    // OSX laf 
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.brushMetalLook", "true");

    // fix missing laf ui classes for used jx components
    UIManager.put(JXMonthView.uiClassID, BasicMonthViewUI.class.getName());
    UIManager.put(JXHyperlink.uiClassID, BasicHyperlinkUI.class.getName());
    UIManager.put(JXHeader.uiClassID, BasicHeaderUI.class.getName());

    // create the application and project dir
    File appDir = new File(Axbo.PROJECT_DIR_DEFAULT);
    if (appDir.mkdirs())
      log.warn("successfully created project dir: " + appDir.getAbsolutePath());

    // create sound package dir
    File soundDir = new File(Axbo.SOUND_PACKAGES_DIR);
    if (soundDir.mkdirs())
      log.info("successfully created sound dir: " + soundDir.getAbsolutePath());

    // create view and model
    new AxboFrameController();
    new PreferencesController();

    // set the device type to aXbo (currently the only supported)
    DeviceContext.setDeviceType(DeviceType.AXBO);

    //initial
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
        new ApplicationInitialize(
        this));
  }

  public static class SPWFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
      return Pattern.compile(SleepData.SLEEP_DATA_FILE_EXT_PATTERN,
          Pattern.CASE_INSENSITIVE).matcher(name).matches();
    }
  };

  public static class AXSFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
      return Pattern.compile(SoundPackage.FILE_PATTERN,
          Pattern.CASE_INSENSITIVE).matcher(name).matches();
    }
  };
}
class AxboShutdownHook extends Thread {

  @Override
  public void run() {
    Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
    try {
      System.out.println("cleaning up resources ...");
      prefs.flush();
      System.out.println("shutting down aXbo research");
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }
}
