package com.dreikraft.axbo.task;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.data.DataInterfaceException;
import com.dreikraft.axbo.data.DeviceContext;
import com.dreikraft.axbo.events.SoundPackageUploadComplete;
import com.dreikraft.axbo.events.SoundUpload;
import com.dreikraft.axbo.sound.Sound;
import com.dreikraft.axbo.sound.SoundPackage;
import com.dreikraft.axbo.sound.SoundPackageException;
import com.dreikraft.axbo.sound.SoundPackageUtil;
import com.dreikraft.axbo.util.BundleUtil;
import com.dreikraft.events.ApplicationEventDispatcher;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Background task for uploading a SoundPackage from file system to aXbo.
 *
 * @author jan.illetschko@3kraft.com
 */
public class SoundPackageUploadTask extends AxboTask<SoundPackage, String> {

  private static final Log log = LogFactory.getLog(SoundPackageUploadTask.class);
  private File soundPackageFile;

  /**
   * Creates a new upload task.
   *
   * @param soundPackageFile an aXbo sound package file
   */
  public SoundPackageUploadTask(final File soundPackageFile) {
    this.soundPackageFile = soundPackageFile;
  }

  /**
   * Uploads a sound package from file to aXbo in background.
   *
   * @return the sound package
   * @throws Exception if the upload fails.
   */
  @Override
  protected SoundPackage doInBackground() throws Exception {

    log.info("performing task" + getClass().getSimpleName() + " ...");
    // read sound package meta data
    final SoundPackage soundPackage = SoundPackageUtil.readPackageInfo(
        SoundPackageUtil.getPackageEntryStream(soundPackageFile,
        SoundPackageUtil.PACKAGE_INFO));
    soundPackage.setPackageFile(soundPackageFile);

    // synchronize state between axbo and PC
    final String portName = Axbo.getPortName();
    AxboCommandUtil.syncInterface(Axbo.getPortName());

    // clear the aXbo memory header
    AxboCommandUtil.clearHeader(portName);

    // reset mem params
    int startPage = 1;
    int step = 0;
    final int stepCount = soundPackage.getSounds().size();
    final float stepRate = 100 / stepCount;
    // for each sound
    for (Sound sound : soundPackage.getSounds()) {
      InputStream soundIn = null;
      ByteArrayOutputStream byteOut;
      ByteArrayOutputStream headerOut;
      try {
        if (log.isDebugEnabled()) {
          log.debug("soundpackage upload progress: " + getProgress());
        }

        // first remove wav header header and tail
        // aXbo only uses the raw data of the ulaw encoded wav file
        soundIn =
            new BufferedInputStream(SoundPackageUtil.getPackageEntryStream(
            soundPackage.getPackageFile(),
            SoundPackageUtil.SOUNDS_PATH_PREFIX + SoundPackageUtil.SL + sound.
            getAxboFile().getPath()), AxboCommandUtil.BUF_SIZE);

        // write sound file to byte array
        byteOut = new ByteArrayOutputStream();
        headerOut = new ByteArrayOutputStream();
        int pos = 0;
        int b;
        int dataLen = Integer.MAX_VALUE;
        boolean isData = false;
        // skip tail
        while (((b = soundIn.read()) != -1) && pos < dataLen) {
          // only write data
          if (isData) {
            byteOut.write(b);
            pos++;
          } else if (containsDataKeyword(headerOut)) {
            // get sound data length
            dataLen = b + soundIn.read() * 256 + soundIn.read() * 256 * 256 + soundIn.
                read() * 256 * 256 * 256;
            isData = true;
          } else {
            // skip header
            headerOut.write(b);
          }
        }
        byteOut.flush();

        // fill the last page with 0xFF
        int pageCount = pos / AxboCommandUtil.PAGE_SIZE;
        final int rest = pos - (pageCount * AxboCommandUtil.PAGE_SIZE);
        if (rest > 0) {
          byte[] fillBytes = new byte[AxboCommandUtil.PAGE_SIZE - rest];
          Arrays.fill(fillBytes, (byte) 0xFF);
          byteOut.write(fillBytes);
          pageCount++;
        }

        // write sound data
        byte[] soundData = byteOut.toByteArray();
        sound.setData(soundData);
        sound.setStartPage(startPage);
        sound.setPageCount(pageCount);
        publish(sound.getName());
        writeSoundData(portName, sound, step, stepRate);

        // new startFrame
        startPage += pageCount;
        step++;
        setProgress((int) (step * stepRate));
      } catch (IOException ex) {
        throw new DataInterfaceException(ex.getMessage(), ex);
      } catch (SoundPackageException ex) {
        throw new DataInterfaceException(ex.getMessage(), ex);
      } finally {
        try {
          if (soundIn != null) {
            soundIn.close();
          }
        } catch (IOException ex) {
          log.warn(ex.getMessage(), ex);
        }
      }
    }

    // write the sounds header table into the first axbo memory page
    publish(BundleUtil.getMessage("sound.header"));
    AxboCommandUtil.writeHeader(portName, soundPackage);
    setProgress(100);

    return soundPackage;
  }

  private boolean containsDataKeyword(ByteArrayOutputStream headerOut)
      throws UnsupportedEncodingException, IOException {
    headerOut.flush();
    return headerOut.toString("US-ASCII").indexOf("data") > -1;
  }

  private void writeSoundData(final String portName, final Sound sound,
      final int step, final float stepRate)
      throws DataInterfaceException {
    // calculate frame count (use half sized frames, because of extra info bytes)
    for (int page = 0; page < sound.getPageCount(); page++) {
      if (log.isDebugEnabled()) {
        log.debug("writing sound: " + sound.getName() + ", page: " + (sound.
            getStartPage() + page));
      }
      setProgress((int) (step * stepRate
          + stepRate * page / sound.getPageCount()));
      // write complete page into buffer
      AxboCommandUtil.writePage(portName, sound.getData(), page);
      // write buffer to flash page
      AxboCommandUtil.writeBufferToPage(portName, sound.getStartPage() + page);
    }
  }

  /**
   * Wait for task to finish. Notifies the GUI.
   */
  @Override
  protected void done() {

    try {
      final SoundPackage soundPackage = get();
      log.info("task " + getClass().getSimpleName() + " performed successfully");
      setResult(AxboTask.Result.SUCCESS);

      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(
          new SoundPackageUploadComplete(this, soundPackage));
    } catch (InterruptedException ex) {
      log.error("task " + getClass().getSimpleName() + " interrupted", ex);
      setResult(Result.INTERRUPTED);
    } catch (ExecutionException ex) {
      log.error("task " + getClass().getSimpleName() + " failed", ex.getCause());
      setResult(Result.FAILED);
    } finally {
      DeviceContext.getDeviceType().getDataInterface().stop();
    }
  }

  /**
   * Updates the current progress of the upload and notifies the GUI.
   *
   * @param sounds the currently processed sounds.
   */
  @Override
  protected void process(final List<String> sounds) {
    for (final String sound : sounds) {
      ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SoundUpload(
          this, sound));
    }
  }
}
