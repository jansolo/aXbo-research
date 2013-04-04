/*
 * $Id: AxboTask.java,v 1.1 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.task;

import com.dreikraft.axbo.data.DeviceContext;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AxboTask
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public abstract class AxboTask<T, V> extends SwingWorker<T, V>
{

  private static final Log log = LogFactory.getLog(AxboTask.class);

  public enum Result
  {

    NOT_READY, SUCCESS, FAILED, INTERRUPTED
  };
  private Result result = Result.NOT_READY;

  @Override
  protected void done()
  {
    try
    {
      get();
      log.info("task " + getClass().getSimpleName() + " performed successfully");
      setResult(Result.SUCCESS);
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

  public Result getResult()
  {
    return result;
  }

  public void setResult(final Result result)
  {
    final Result oldResult = this.result;
    this.result = result;
    firePropertyChange("result", oldResult, result);
  }
}
