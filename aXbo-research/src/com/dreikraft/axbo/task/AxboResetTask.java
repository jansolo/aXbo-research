/*
 * $Id: AxboResetTask.java,v 1.1 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.task;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.events.ApplicationMessageEvent;
import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.util.BundleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AxboResetTask
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class AxboResetTask extends AxboTask<Boolean, Object>
{

  private static final Log log = LogFactory.getLog(AxboResetTask.class);

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
