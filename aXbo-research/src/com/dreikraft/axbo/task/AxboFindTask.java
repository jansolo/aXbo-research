/*
 * $Id: AxboFindTask.java,v 1.2 2010-12-16 23:13:53 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.task;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.data.DataInterface;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.events.AxboFound;
import com.dreikraft.axbo.util.BundleUtil;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LoadStoredDataTask
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class AxboFindTask extends AxboTask<String, String>
{

  private static final Log log = LogFactory.getLog(AxboFindTask.class);
  private final SwingWorker<?,?> followUpTask;

  public AxboFindTask(SwingWorker<?,?> followUpTask)
  {
    this.followUpTask = followUpTask;
  }

  @Override
  protected String doInBackground() throws Exception
  {
    log.info("performing task" + getClass().getSimpleName() + " ...");
    return searchAxbo();
  }

  @Override
  protected void done()
  {
    try
    {
      final String portName = get();
      log.info("task " + getClass().getSimpleName() + " performed successfully");

      // store port
      if (portName != null)
      {
        Axbo.getApplicationPreferences().put(DeviceContext.getDeviceType().name() + "."
            + Axbo.SERIAL_PORT_NAME_PREF, portName);
        setResult(Result.SUCCESS);

        // perform followup task
        ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new AxboFound(
            this, portName, followUpTask));
      }
      else
      {
        setResult(Result.FAILED);
      }
    }
    catch (InterruptedException ex)
    {
      log.error("task " + getClass().getSimpleName() + " interrupted", ex);
      setResult(Result.INTERRUPTED);
    }
    catch (ExecutionException ex)
    {
      log.error("task " + getClass().getSimpleName() + " failed", ex.getCause());
      setResult(Result.FAILED);
    }
    finally
    {
      DeviceContext.getDeviceType().getDataInterface().stop();
    }
  }

  @Override
  protected void process(final List<String> portNames)
  {
    for (final String portName : portNames)
    {
      final String msg = BundleUtil.getMessage("statusLabel.testingPort",
          portName);
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationMessageEvent(
          this, msg, false));
    }
  }

  private String searchAxbo()
  {
    final DataInterface dataInterface = DeviceContext.getDeviceType().getDataInterface();
    final List<String> commPortNames = dataInterface.getCommPortNames();

    int countTested = 0;

    // first try to find aXbo in the configured port
    if (testPort(Axbo.getPortName()))
    {
      return Axbo.getPortName();
    }
    setProgress((int) (100f / commPortNames.size() * (countTested++)));

    // test other ports
    for (String commPortName : commPortNames)
    {
      if (!commPortName.equals(Axbo.getPortName()))
      {
        setProgress((int) (100f / commPortNames.size() * (countTested++)));
        if (testPort(commPortName))
        {
          return commPortName;
        }
      }
    }

    // no port found
    return null;
  }

  private boolean testPort(final String portName)
  {
    log.info("testing system comm port: " + portName);
    publish(portName);
    try
    {
      AxboCommandUtil.runCheckCmd(portName);
      return true;
    }
    catch (Exception ex)
    {
      if (log.isDebugEnabled())
      {
        log.debug(ex.getMessage());
      }
      return false;
    }
    finally
    {
      DeviceContext.getDeviceType().getDataInterface().stop();
    }
  }
}
