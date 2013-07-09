package com.dreikraft.axbo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FileUtil
 *
 * @author jan.illetschko@3kraft.com
 */
public class FileUtil {
  
  public static final Log log = LogFactory.getLog(FileUtil.class);

  /**
   * Creates a new instance of FileUtil
   */
  public FileUtil() {
  }

  /**
   * Gets the file extension without the dot ".".
   *
   * @param f File of which to get the extension
   * @return file extension without the dot "."
   */
  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    
    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase(Locale.ENGLISH);
    }
    return ext;
  }

  /**
   * Truncate the file's extension.
   *
   * @param f the file from which the extension gets truncated
   * @return file name without extension
   */
  public static String stripExtension(File f) {
    String name = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    
    if (i > 0 && i < s.length() - 1) {
      name = s.substring(0, i).toLowerCase(Locale.ENGLISH);
    }
    return name;
  }

  /**
   * Gets the path of the file
   *
   * @param f File from which to get the path
   * @return the path
   */
  public static String getPath(File f) {
    String path = null;
    String s = f.getAbsolutePath();
    int i = s.lastIndexOf(File.separatorChar);
    
    if (i > 0 && i < s.length() - 1) {
      path = s.substring(0, i);
    }
    return path;
  }

  /**
   * Creates a tempory directory in the user home's directory named by the
   * String provided.
   *
   * @param folder Name of the directory that has to be created in the user
   * directory.
   * @return the created file
   */
  public static File createTempDir(String folder) {
    File tempDir = new File(System.getProperty("user.home") + File.separator
        + folder);
    if (!tempDir.mkdirs())
      log.warn("failed to create tmp dir: " + tempDir.getAbsolutePath());
    if (log.isDebugEnabled())
      log.debug("Tempory directory " + tempDir.getAbsolutePath() + " created.");
    return tempDir;
  }

  /**
   * Creates a file from the provided {@link InputStream}
   *
   * @param io the <CODE>InputStream</CODE>
   * @param fileName path and name of the created file
   */
  public static void createFileFromInputStream(InputStream io, String fileName)
      throws FileNotFoundException, IOException {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(fileName);
      byte[] buf = new byte[256];
      int read;
      while ((read = io.read(buf)) > 0) {
        fos.write(buf, 0, read);
      }
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException ex) {
          log.warn(ex.getMessage(), ex);
        }
      }
    }
  }

  /**
   * Copy a file with java NIO channels
   *
   * @param in
   * @param out
   * @throws java.io.IOException
   */
  public static void copyFile(File in, File out)
      throws IOException {
    FileChannel inChannel = new FileInputStream(in).getChannel();
    FileChannel outChannel = new FileOutputStream(out).getChannel();
    try {
      inChannel.transferTo(0, inChannel.size(),
          outChannel);
    } catch (IOException e) {
      throw e;
    } finally {
      if (inChannel != null) {
        inChannel.close();
      }
      if (outChannel != null) {
        outChannel.close();
      }
    }
  }
}
