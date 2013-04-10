/*
 * © 2010 3kraft GmbH & Co KG
 * $Id: Axbo.java,v 1.39 2010-12-29 15:20:34 illetsch Exp $
 */
package com.dreikraft.axbo;

import com.dreikraft.axbo.controller.AxboFrameController;
import com.dreikraft.axbo.controller.PreferencesController;
import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.events.ApplicationInitialize;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.data.DeviceType;
import com.dreikraft.axbo.data.SleepData;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import org.apache.commons.logging.*;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.plaf.basic.BasicHeaderUI;
import org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI;
import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;

/**
 * $Id: Axbo.java,v 1.39 2010-12-29 15:20:34 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.39 $
 */
public final class Axbo implements ApplicationEventEnabled
{

  public static Log log = LogFactory.getLog(Axbo.class);
  // === constants === 
  // Check that we are on Mac OS X.  This is crucial to loading and using the 
  // OSXAdapter class.
  public static final boolean MAC_OS_X = (System.getProperty("os.name").
      toLowerCase().startsWith("mac os x"));
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
  public static final String SOUND_PACKAGE_ICON = "/resources/images/music.png";
  // sleep data constants
  public static final int MAX_MOVEMENTS_DEFAULT = 100;
  public static final long CLEANER_INTERVAL_DEFAULT = 3 * 60 * 60 * 1000;
  public static final float AVERAGE_MOVEMENTS_THRESHOLD = 10;
  public static final long MINIMUM_SLEEP_DURATION = 30 * 60 * 1000;
  public static final int MINIMUM_MOVEMENTS = 100;
  // === preferences ===
  // serial port prefs
  public static final String SERIAL_PORT_NAME_PREF = "serialPort.name";
  public static final String SERIAL_PORT_NAME_DEFAULT = MAC_OS_X
      ? "/dev/tty.SLAB_USBtoUART" : "COM1";
  // language prefs
  public static final String LANGUAGES_PREF = "languages";
  public static final String LANGUAGES_DEFAULT = "en,de,ja,ru";
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

  public static void main(final String[] args)
  {
    Axbo controller = getApplicationController();
    controller.init();
 }

  public static Axbo getApplicationController()
  {
    return Axbo.CONTROLLER;
  }

  public static Preferences getApplicationPreferences()
  {
    return Preferences.userNodeForPackage(Axbo.class);
  }

  public static String getPortName()
  {
    return getApplicationPreferences().get(DeviceContext.getDeviceType() + "."
        + Axbo.SERIAL_PORT_NAME_PREF, Axbo.SERIAL_PORT_NAME_DEFAULT);
  }

  private Axbo()
  {
    super();
  }

  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public void init()
  {
    // disable the security manager
    System.setSecurityManager(null);

    // register shutdown hook
    Runtime.getRuntime().addShutdownHook(new AxboShutdownHook());

    // set desired locale
    try
    {
      String langsPref = getApplicationPreferences().get(LANGUAGES_PREF,
          LANGUAGES_DEFAULT);
      String langPref = getApplicationPreferences().get(LANGUAGE_PREF, "unset");
      if (!langPref.equals("unset"))
      {
        Locale.setDefault(new Locale(langPref));
      }
      if (langsPref.indexOf(Locale.getDefault().getLanguage()) == -1)
      {
        Locale.setDefault(new Locale(LANGUAGE_DEFAULT));
      }
    }
    catch (Exception ex)
    {
      log.error(ex.getMessage(), ex);
    }
    if (log.isDebugEnabled())
    {
      log.debug("Current Locale: " + Locale.getDefault());
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
    appDir.mkdirs();

    // create sound package dir
    File soundDir = new File(Axbo.SOUND_PACKAGES_DIR);
    soundDir.mkdirs();

    // create view and model
    new AxboFrameController();
    new PreferencesController();

    // set the device type to aXbo (currently the only supported)
    DeviceContext.setDeviceType(DeviceType.AXBO);

    //initial
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationInitialize(
        this));
  }

  public static class SPWFilenameFilter implements FilenameFilter
  {

    @Override
    public boolean accept(File dir, String name)
    {
      if (name.toLowerCase().indexOf(
          SleepData.SLEEP_DATA_FILE_EXT1.toLowerCase())
          > 0 || name.toLowerCase().indexOf(SleepData.SLEEP_DATA_FILE_EXT2.
          toLowerCase()) > 0)
      {
        return true;
      }
      else
      {
        return false;
      }
    }
  };

  public static class AXSFilenameFilter implements FilenameFilter
  {

    @Override
    public boolean accept(File dir, String name)
    {
      return name.toLowerCase().endsWith(
          Axbo.SOUND_DATA_FILE_EXT.toLowerCase());
    }
  };
}

class AxboShutdownHook extends Thread
{

  @Override
  public void run()
  {
    Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
    try
    {
      System.out.println("cleaning up resources ...");
      prefs.flush();
      System.out.println("shutting down aXbo research");
    }
    catch (Exception ex)
    {
      ex.printStackTrace(System.err);
    }
  }
}
