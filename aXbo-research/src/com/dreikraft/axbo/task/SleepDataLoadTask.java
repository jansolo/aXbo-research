package com.dreikraft.axbo.task;

import com.dreikraft.axbo.events.SleepDataLoaded;
import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.events.SleepDataAdded;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SleepDataLoadTask
 *
 * @author jan.illetschko@3kraft.com
 */
public class SleepDataLoadTask extends SwingWorker<Integer, SleepData>
    implements ExceptionListener {

  private static final Log log = LogFactory.getLog(SleepDataLoadTask.class);
  private final File[] spwFiles;

  public SleepDataLoadTask(final File[] spwFiles) {
    this.spwFiles = Arrays.copyOf(spwFiles, spwFiles.length);
  }

  @Override
  protected Integer doInBackground() throws Exception {
    int count = 0;
    for (File file : spwFiles) {
      XMLDecoder decoder = null;
      try {
        // convert package names first for old serialized files first
        Modifier regex = new RegexModifier("com\\.dreikraft\\.infactory", 0,
            "com.dreikraft.axbo");
        decoder = new XMLDecoder(new ReaderInputStream(new ModifyingReader(
            new FileReader(file), regex), Charset.forName("UTF-8")));
        decoder.setExceptionListener(this);
        final Object obj = decoder.readObject();

        if (obj instanceof SleepData) {
          final SleepData sleepData = (SleepData) obj;
          sleepData.setDataFile(file);
          if (log.isDebugEnabled()) {
            if (log.isDebugEnabled()) {
              log.debug("sleep data loaded " + sleepData);
            }
          }
          setProgress((int) (100f / spwFiles.length * count));
          publish(sleepData);
        } else {
          log.error("invalid spw file " + file.getName());
        }
      } catch (RuntimeException ex) {
        log.error(ex.getMessage(), ex);
      } finally {
        if (decoder != null) {
          decoder.close();
        }
      }
      count++;
    }
    return count;
  }

  @Override
  protected void done() {
    try {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new SleepDataLoaded(
          this, get()));
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
    } catch (ExecutionException ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  @Override
  protected void process(final List<SleepData> sleepDates) {
    for (final SleepData sleepData : sleepDates) {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new SleepDataAdded(
          this, sleepData));
    }
  }

  @Override
  public void exceptionThrown(Exception ex) {
    if (log.isDebugEnabled()) {
      log.warn(
          "recoverable exception while deserializing sleepdata objects from disk: "
          + ex.
          getMessage());
    }
  }
}
