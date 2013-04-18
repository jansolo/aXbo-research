/*
 * $Id: AxboStatusGetTask.java,v 1.1 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.task;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationEventEnabled;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.data.AxboInfo;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.events.AxboStatusGot;
import com.dreikraft.axbo.events.InfoEvent;
import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AxboResetTask
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class AxboStatusGetTask extends AxboTask<AxboInfo, Object> implements
    ApplicationEventEnabled {

  private static final Log log = LogFactory.getLog(AxboStatusGetTask.class);
  private AxboInfo infoData;

  @Override
  protected AxboInfo doInBackground() throws Exception {
    log.info("performing task" + getClass().getSimpleName() + " ...");

    ApplicationEventDispatcher.getInstance().registerApplicationEventHandler(
        InfoEvent.class, this);

    AxboCommandUtil.runReadStatus(Axbo.getPortName());

    try {
      synchronized (this) {
        while (infoData == null) {
          wait(DeviceContext.getDeviceType().getTimeout());
        }
      }
    } catch (InterruptedException ex) {
      log.error(ex.getMessage());
    }

    return infoData;
  }

  @Override
  protected void done() {
    try {
      final AxboInfo axboData = get();
      log.info("task " + getClass().getSimpleName() + " performed successfully");
      setResult(Result.SUCCESS);

      ApplicationEventDispatcher.getInstance().
          dispatchGUIEvent(new AxboStatusGot(this, axboData));
    } catch (InterruptedException ex) {
      log.error("task " + getClass().getSimpleName() + " interrupted", ex);
      setResult(Result.INTERRUPTED);
    } catch (ExecutionException ex) {
      log.error("task " + getClass().getSimpleName() + " failed", ex.getCause());
      setResult(Result.FAILED);
    } finally {
      DeviceContext.getDeviceType().getDataInterface().stop();
      ApplicationEventDispatcher.getInstance()
          .deregisterApplicationEventHandler(
          InfoEvent.class, this);
    }
  }

  public void handle(final InfoEvent evt) {
    synchronized (this) {
      this.infoData = evt.getData();
      notifyAll();
    }
  }
}
