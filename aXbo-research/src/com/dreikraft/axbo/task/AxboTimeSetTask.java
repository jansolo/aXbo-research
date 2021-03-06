/*
 * $Id: AxboTimeSetTask.java,v 1.1 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2010
 */
package com.dreikraft.axbo.task;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AxboResetTask
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class AxboTimeSetTask extends AxboTask<Boolean, Object>
{

  private static final Log log = LogFactory.getLog(AxboTimeSetTask.class);

  @Override
  protected Boolean doInBackground() throws Exception
  {
    log.info("performing task" + getClass().getSimpleName() + " ...");
    AxboCommandUtil.runSetClockDate(Axbo.getPortName());
    return true;
  }
}
