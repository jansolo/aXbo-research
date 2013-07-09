/*
 * © 2010 3kraft IT GmbH & Co KG
 * $Id: PreferencesController.java,v 1.13 2010-11-29 15:42:24 illetsch Exp $
 */
package com.dreikraft.axbo.controller;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.events.ApplicationInitialize;
import com.dreikraft.events.ApplicationInitialized;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.OS;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.data.SensorID;
import com.dreikraft.axbo.events.PrefsClose;
import com.dreikraft.axbo.events.PrefsOpen;
import com.dreikraft.axbo.util.GuiUtils;
import com.dreikraft.axbo.gui.PreferencesDialog;
import com.dreikraft.axbo.util.BundleUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.*;


/*
 * $Id: PreferencesController.java,v 1.13 2010-11-29 15:42:24 illetsch Exp $
 *
 * @author  3Kraft - $Author: illetsch $
 * @version $Revision: 1.13 $
 */
public class PreferencesController implements ApplicationEventEnabled
{

  public static final Log log = LogFactory.getLog(PreferencesController.class);
  
  private PreferencesDialog view;
  private String[] langCodes;

  @SuppressWarnings("LeakingThisInConstructor")
  public PreferencesController()
  {
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationInitialize.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        ApplicationInitialized.class, this);
  }

  public void handle(final ApplicationInitialize evt)
  {
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        PrefsOpen.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        PrefsClose.class, this);
  }

  public void handle(final ApplicationInitialized evt)
  {
  }

  public void handle(final PrefsOpen evt)
  {
    view = new PreferencesDialog(evt.getParentFrame(), true, this);

    // list available com ports
    final List<String> portNames = DeviceContext.getDeviceType().
        getDataInterface().getCommPortNames();
    Collections.sort(portNames);

    // set selected com port
    final String deviceTypeName = Axbo.getApplicationPreferences().
        get(Axbo.DEVICE_TYPE_PREF, Axbo.DEVICE_TYPE_DEFAULT);
    view.initComPortCB(portNames, Axbo.getApplicationPreferences().
        get(deviceTypeName + "." + Axbo.SERIAL_PORT_NAME_PREF,
        OS.get().getDefaultPort()));

    // list languages
    langCodes = Axbo.LANGUAGES_DEFAULT.replaceAll(" ", "").split(",");
    Arrays.sort(langCodes);
    final List<String> languages = new ArrayList<String>();
    for (String langCode : langCodes)
    {
      languages.add(BundleUtil.getMessage(langCode));
    }
    view.initLanguageCB(languages, BundleUtil.getMessage(
        Locale.getDefault().getLanguage()));

    // set sensor user names
    view.setSensor1Name(Axbo.getApplicationPreferences().get(
        SensorID.P1.toString(), SensorID.P1.getDefaultName()));
    view.setSensor2Name(Axbo.getApplicationPreferences().get(
        SensorID.P2.toString(), SensorID.P2.getDefaultName()));

    // Zentriere den Dialog
    GuiUtils.center(evt.getParentFrame(), view);
    view.setVisible(true);
  }

  public void handle(final PrefsClose evt)
  {
    if (evt.isSave())
    {
      // set language
      Axbo.getApplicationPreferences().put(Axbo.LANGUAGE_PREF,
          langCodes[view.getLanguageIndex()]);

      // set com port
      Axbo.getApplicationPreferences().put(Axbo.SERIAL_PORT_NAME_PREF,
          view.getComPortValue());

      // set persons
      Axbo.getApplicationPreferences().put(SensorID.P1.toString(),
          view.getSensor1Name());
      Axbo.getApplicationPreferences().put(SensorID.P2.toString(),
          view.getSensor2Name());
    }
    view.dispose();
  }
}
