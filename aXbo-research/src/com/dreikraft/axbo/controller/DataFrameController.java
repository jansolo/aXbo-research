/*
 * © 2008 3kraft
 * $Id: DataFrameController.java,v 1.33 2010-12-17 10:11:41 illetsch Exp $
 */
package com.dreikraft.axbo.controller;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.timeseries.KeyTimeSeries;
import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.timeseries.SleepDataTimeSeries;
import com.dreikraft.axbo.events.DiagramCopy;
import com.dreikraft.axbo.events.DiagramPrint;
import com.dreikraft.axbo.events.DiagramSaveAsPNG;
import com.dreikraft.axbo.events.DiagramStatsUpdate;
import com.dreikraft.axbo.events.DiagramUpdate;
import com.dreikraft.axbo.events.DiagramZoom;
import com.dreikraft.axbo.events.DiagramClose;
import com.dreikraft.axbo.events.DiagramClosed;
import com.dreikraft.axbo.events.SleepDataSave;
import com.dreikraft.axbo.gui.AxboFrame;
import com.dreikraft.axbo.gui.DataFrame;
import com.dreikraft.axbo.util.BundleUtil;
import java.awt.print.PageFormat;
import org.apache.commons.logging.*;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * $Id: DataFrameController.java,v 1.33 2010-12-17 10:11:41 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.33 $
 */
public class DataFrameController implements ApplicationEventEnabled
{

  public static final Log log = LogFactory.getLog(DataFrameController.class);
  private final DataFrame view;
  private final SleepData sleepData;

  public DataFrameController(final SleepData sleepData)
  {
    this.view = new DataFrame();
    this.sleepData = sleepData;
  }

  public void init()
  {
    // register events
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramCopy.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramPrint.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramSaveAsPNG.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramStatsUpdate.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramUpdate.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramZoom.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        DiagramClose.class, this);
    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        SleepDataSave.class, this);

    view.init();
    view.createNewChart(
        createDataset(),
        sleepData.calculateStartTime(),
        sleepData.calculateSleepStart(),
        sleepData.getWakeIntervalStart(),
        sleepData.calculateWakeIntervalEnd(),
        sleepData.getWakeupTime(),
        createKeyDataset(),
        createSnoozeDataset());

    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramStatsUpdate(
        this));
  }

  public void handle(final DiagramClose evt)
  {
    if (view.equals(evt.getDataView()))
    {
      view.close();

      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramCopy.class, this);
      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramPrint.class, this);
      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramSaveAsPNG.class, this);
      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramStatsUpdate.class, this);
      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramUpdate.class, this);
      ApplicationEventDispatcher.getInstance().deregisterApplicationEventHandler(
          DiagramZoom.class, this);

      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new DiagramClosed(
          this, this));
    }
  }

  public DataFrame getView()
  {
    return view;
  }

  public SleepData getSleepData()
  {
    return sleepData;
  }

  public void handle(final DiagramCopy evt)
  {
    if (evt.getSource().equals(this.view))
    {
      view.doCopy();
    }
  }

  public void handle(final DiagramPrint evt)
  {
    if (evt.getSource().equals(this.view)
        || evt.getSource() instanceof AxboFrame)
    {
      final PageFormat pf = evt.getPrintJob().defaultPage();
      pf.setOrientation(PageFormat.LANDSCAPE);

      evt.getBook().append(view, pf);
    }
  }

  public void handle(final DiagramSaveAsPNG evt)
  {
    if (evt.getSource().equals(this.view))
    {
      view.doSaveAsPNG();
    }
  }

  public void handle(final DiagramStatsUpdate evt)
  {
    view.updateStats(sleepData);
  }

  public void handle(final DiagramUpdate evt)
  {
    view.updateChart();
  }

  public void handle(final DiagramZoom evt)
  {
    view.zoom(sleepData, evt.getZoomStart(), evt.getZoomDuration(), evt.getZoomRange());
  }

  public void handle(final SleepDataSave evt)
  {
    if (evt.getSleepData().equals(sleepData))
    {
      view.updateStats(evt.getSleepData());
    }
  }

  private IntervalXYDataset createDataset()
  {
    final TimeSeriesCollection dataset = new TimeSeriesCollection();
    final SleepDataTimeSeries sleepDataTimeSeries = new SleepDataTimeSeries(
        BundleUtil.getMessage("chart.timeseries.label"), sleepData,
        Minute.class, Axbo.MAX_MOVEMENTS_DEFAULT);
    dataset.addSeries(sleepDataTimeSeries);
    return dataset;
  }

  private KeyTimeSeries createKeyDataset()
  {
    final KeyTimeSeries keyTimeSeries = new KeyTimeSeries(BundleUtil.getMessage(
        "chart.keyseries.label"), sleepData, Minute.class, MovementData.KEY);
    return keyTimeSeries;
  }

  private KeyTimeSeries createSnoozeDataset()
  {
    final KeyTimeSeries keyTimeSeries = new KeyTimeSeries(BundleUtil.getMessage(
        "chart.snoozeseries.label"), sleepData, Minute.class, MovementData.SNOOZE);
    return keyTimeSeries;
  }
}
