package com.dreikraft.axbo.task;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.util.BundleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Resets aXbo and sets the current date/time.
 *
 * @author jan.illetschko@3kraft.com
 */
public class AxboResetTask extends AxboTask<Boolean, Object>
{

  private static final Log log = LogFactory.getLog(AxboResetTask.class);
  
  /**
   * Reset aXbo in background task (SwingWorker).
   * @return success
   * @throws Exception if execution fails  
   */
  @Override
  protected Boolean doInBackground() throws Exception
  {
    log.info("performing task" + getClass().getSimpleName() + " ...");
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new ApplicationMessageEvent(
        this, BundleUtil.getMessage("statusLabel.resetingAxbo"), false));
    AxboCommandUtil.runTestCmd(Axbo.getPortName(),
        (byte) 0x08);
    AxboCommandUtil.sleep(3000);
    AxboCommandUtil.runSetClockDate(Axbo.getPortName());
    return true;
  }
}
