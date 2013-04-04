/*
 * © 2008 3kraft
 * $Id: SoundPackageFrameController.java,v 1.12 2010-12-06 15:31:43 illetsch Exp $
 */
package com.dreikraft.axbo.controller;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.data.AxboCommandUtil;
import com.dreikraft.axbo.data.DataInterfaceException;
import com.dreikraft.axbo.gui.SoundPackageFrame;
import com.dreikraft.axbo.model.UploadDialogModel;
import com.dreikraft.axbo.sound.Sound;
import com.dreikraft.axbo.sound.SoundPackage;
import com.dreikraft.axbo.sound.SoundPackageException;
import com.dreikraft.axbo.sound.SoundPackageUtil;
import com.dreikraft.axbo.util.BundleUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * $Id: SoundPackageFrameController.java,v 1.12 2010-12-06 15:31:43 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.12 $
 */
public class SoundPackageFrameController
{
  public static final Log log =
      LogFactory.getLog(SoundPackageFrameController.class);
  public static final int UNSET = -1;
  private AxboFrameController parentCtrl;
  private UploadDialogController uploadDialogCtrl;
  private SoundPackageFrame view;
  private SoundPackage soundPkg;
  private ExecutorService soundPlayerThread;
  private ExecutorService uploadThread;
  private SoundPlayer player;
  private UploadDialogModel dialogModel;
  private boolean isUpload = false;
 
  public SoundPackageFrameController(AxboFrameController parentCtrl)
  {
    this.parentCtrl = parentCtrl;
    
    this.dialogModel = new UploadDialogModel();
    this.uploadDialogCtrl = new UploadDialogController(parentCtrl, dialogModel);

    soundPlayerThread = Executors.newSingleThreadExecutor();
    uploadThread = Executors.newSingleThreadExecutor();
    
    // add listener for info events
//    Axbo.getApplicationController().getAxboEventDispatcher().
//        addAxboEventListener(this);
  }

  public void close() 
  {
//    for (Object internalFrame : getParentCtrl().getInternalFrames())
//    {
//      if (SoundPackageFrame.class.isAssignableFrom(internalFrame.getClass()))
//      {
//        ((SoundPackageFrame) internalFrame).dispose();
//        break;
//      }
//    }
  }
  
  public void openSoundPkg(SoundPackage soundPkg)
  {
    close();
    this.setSoundPkg(soundPkg);
    view = new SoundPackageFrame(this);
    //getParentCtrl().openNewInternalFrame(view);
    view.bringToFront();
  }

  public AxboFrameController getParentCtrl()
  {
    return parentCtrl;
  }

  public SoundPackage getSoundPkg()
  {
    return soundPkg;
  }

  public void playSound(List<Sound> sounds)
  {
    if (player != null && player.isPlaying())
    {
      player.setPlaying(false);
    }
    //playbackStarted(sounds.size() > 1);
    soundPlayerThread.execute(player = new SoundPlayer(this, sounds));
  }

  public void setSoundPkg(SoundPackage soundPkg)
  {
    this.soundPkg = soundPkg;
  }

  public void skipBack()
  {
    player.skipBack();
  }

  public void skipForward()
  {
    player.skipForward();
  }

  public void stopSound()
  {
    if (player != null)
    {
      player.setPlaying(false);
    }
    //playbackStopped();
  }

  public void upload()
  {
    try
    {
//      parentCtrl.getFrame().
//          showStatusMessage(BundleUtil.getMessage("statusLabel.uploadSoundPackage",
//          soundPkg.getName()));
      isUpload = true;
      AxboCommandUtil.runReadStatus(Axbo.getPortName());
    }
    catch (DataInterfaceException ex)
    {
      log.error(ex.getMessage(), ex);
      String title =
          BundleUtil.getMessage("errorMessageBox.title");
      String msg =
          BundleUtil.getErrorMessage("globalError.uploadFailed");
//      parentCtrl.getFrame().showMessage(msg, title, true);
//      parentCtrl.getFrame().showStatusMessage(msg);
      isUpload = false;
    }
  }

  void playbackStarted(boolean enableSkip)
  {
//    parentCtrl.getFrame().showStatusMessage(BundleUtil.getMessage(
//        "statusLabel.startPlayback"));
    view.playbackStarted(enableSkip);
  }

  void playbackStopped()
  {
//    parentCtrl.getFrame().showStatusMessage(BundleUtil.getMessage(
//        "statusLabel.stopPlayback"));
    view.playbackStopped();
  }

  void soundStarted(Sound sound)
  {
//    parentCtrl.getFrame().showStatusMessage(BundleUtil.getMessage(
//        "statusLabel.playSound", sound.getName()));
    sound.setPlaying(true);
    view.soundStarted(sound);
  }

  void soundStopped(Sound sound)
  {
//    parentCtrl.getFrame().showStatusMessage(BundleUtil.getMessage(
//        "statusLabel.stopSound", sound.getName()));
    sound.setPlaying(false);
    view.soundStopped(sound);
  }

//  public void process(AxboEvent axboEvent)
//  {
//    if (isUpload && InfoEvent.class.isAssignableFrom(axboEvent.getClass())) {
//      final InfoEvent infoEvent = (InfoEvent)axboEvent;
//      uploadThread.execute(new Runnable()
//      {
//        public void run()
//        {
//          try
//          {
//            Axbo appCtrl = Axbo.getApplicationController();
//            AxboCommandUtil.runReadStatus(appCtrl.getPortName(),
//                appCtrl.getDeviceType());
//            if (!SoundPackageUtil.verifyPackage(soundPkg.getPackageFile()))
//            {
//              throw new SoundPackageException("globalError.invalidSignature");
//            }
//            if (soundPkg.isSecurityEnforced())
//            {
//              if (soundPkg.getSerialNumber().equals(
//                  SoundPackage.SERIAL_NUMBER.NONE.name()))
//              {
//                if (infoEvent.getData().getSerialNumber().length() > 0)
//                  throw new SoundPackageException("globalError.invalidSerialnumber");
//              }
//              else if (!soundPkg.getSerialNumber().equals(
//                  SoundPackage.SERIAL_NUMBER.GLOBAL.name()))
//              {
//                if (!infoEvent.getData().getSerialNumber().equals(soundPkg.getSerialNumber()))
//                  throw new SoundPackageException("globalError.invalidSerialnumber");
//              }
//            }
//            AxboCommandUtil.runWriteSoundPackage(appCtrl.getPortName(),
//                appCtrl.getDeviceType(), soundPkg, dialogModel);
//            parentCtrl.getFrame().showStatusMessage(BundleUtil.getMessage(
//                "statusLabel.uploadSoundPackageSuccess", soundPkg.getName()));
//          }
//          catch (SoundPackageException ex)
//          {
//            try
//            {
//              log.error(ex.getMessage(), ex);
//              String title =
//                  BundleUtil.getMessage("errorMessageBox.title");
//              String msg =
//                  BundleUtil.getErrorMessage(ex.getMessage());
//              parentCtrl.getFrame().showMessage(msg, title, true);
//              parentCtrl.getFrame().showStatusMessage(msg);
//              Thread.sleep(500);
//              uploadDialogCtrl.hide();
//            }
//            catch (InterruptedException ex1)
//            {
//              log.warn(ex1.getMessage(), ex1);
//            }
//          }
//          catch (DataInterfaceException ex)
//          {
//            try
//            {
//              log.error(ex.getMessage(), ex);
//              String title =
//                  BundleUtil.getMessage("errorMessageBox.title");
//              String msg =
//                  BundleUtil.getErrorMessage("globalError.uploadFailed");
//              parentCtrl.getFrame().showMessage(msg, title, true);
//              parentCtrl.getFrame().showStatusMessage(msg);
//              Thread.sleep(500);
//              uploadDialogCtrl.hide();
//            }
//            catch (InterruptedException ex1)
//            {
//              log.warn(ex1.getMessage(), ex1);
//            }
//          }
//          isUpload = false;
//        }
//      });
//      uploadDialogCtrl.show();
//    }
//  }
}
class SoundPlayer implements Runnable
{
  public static final Log log = LogFactory.getLog(SoundPlayer.class);
  private static final int BUF_SIZE = 64;
  private SoundPackageFrameController ctrl;
  private List<Sound> sounds;
  private boolean playing = true;
  private Skip skip = Skip.none;

  private enum Skip
  {
    none, forward, backward
  };  
  
  public SoundPlayer(SoundPackageFrameController ctrl, List<Sound> sounds) 
  {
    this.ctrl = ctrl;
    this.sounds = sounds;
    this.playing = true;
  }

  public void run()
  {
    int curPlaying = 0;
    ctrl.playbackStarted(sounds.size() > 1);
    while (curPlaying < sounds.size())
    {
      final Sound sound = sounds.get(curPlaying);
      InputStream in = null;
      SourceDataLine line = null;
      {
        try
        {
          in = new BufferedInputStream(SoundPackageUtil.getPackageEntryStream(
              ctrl.getSoundPkg().getPackageFile(),
              SoundPackageUtil.SOUNDS_PATH_PREFIX +
              SoundPackageUtil.SL + sound.getAxboFile().getPath()), BUF_SIZE);

          // set the output encoding, MAC OS only support PCM 44.1kHz for ouput
          // convert the audio input stream to the correct output format
          final AudioInputStream pcmAudioIn = AudioSystem.getAudioInputStream(
              AudioFormat.Encoding.PCM_SIGNED,
              AudioSystem.getAudioInputStream(in));

          // create a ouput line with the ouput encoding
          final AudioFormat targetFormat = pcmAudioIn.getFormat();
          final DataLine.Info info =
              new DataLine.Info(SourceDataLine.class, targetFormat);
          line = (SourceDataLine) AudioSystem.getLine(info);
          line.addLineListener(new LineListener()
          {
            public void update(LineEvent evt)
            {
              if (LineEvent.Type.START.equals(evt.getType()))
              {
                ctrl.soundStarted(sound);
              }
              if (LineEvent.Type.STOP.equals(evt.getType()))
              {
                ctrl.soundStopped(sound);
              }
            }
          });
          line.open();

          // start the sound
          line.start();

          // write sound file to line
          int bytesRead = 0;
          byte[] data = new byte[BUF_SIZE];
          while (bytesRead != -1 && Skip.none.equals(skip) && isPlaying())
          {
            bytesRead = pcmAudioIn.read(data, 0, data.length);
            if (bytesRead >= 0)
            {
              line.write(data, 0, bytesRead);
            }
          }
          line.drain();

          if (Skip.backward.equals(skip))
          {
            curPlaying = curPlaying > 0 ? curPlaying - 1 : 0;
          }
          else
          {
            curPlaying++;
          }
          skip = Skip.none;
        }
        catch (LineUnavailableException ex)
        {
          log.error(ex.getMessage(), ex);
        }
        catch (UnsupportedAudioFileException ex)
        {
          log.error(ex.getMessage(), ex);
        }
        catch (IOException ex)
        {
          log.error(ex.getMessage(), ex);
        }
        catch (SoundPackageException ex)
        {
          log.error(ex.getMessage(), ex);
        }
        finally
        {
          try
          {
            in.close();
          }
          catch (IOException ex)
          {
            log.warn(ex.getMessage(), ex);
          }
          line.stop();
          line.close();
        }
      }
    }
    ctrl.playbackStopped();
  }

  public boolean isPlaying()
  {
    return playing;
  }

  public void setPlaying(boolean playing)
  {
    this.playing = playing;
  }

  public void skipForward()
  {
    skip = Skip.forward;
  }

  public void skipBack()
  {
    skip = Skip.backward;
  }
}
