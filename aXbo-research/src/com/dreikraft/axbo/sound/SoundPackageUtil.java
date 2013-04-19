package com.dreikraft.axbo.sound;

import com.dreikraft.axbo.beanutils.EnumConverter;
import com.dreikraft.axbo.crypto.CryptoException;
import com.dreikraft.axbo.crypto.CryptoUtil;
import com.dreikraft.axbo.util.ByteUtil;
import com.dreikraft.axbo.util.FileUtil;
import com.dreikraft.axbo.util.StringUtil;
import com.dreikraft.axbo.util.zip.ZipClosingInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ExtendedBaseRules;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Utilities for saving, restoring and verifying a Soundpackage file.
 *
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision
 */
public class SoundPackageUtil {

  /**
   * the commons logger category
   */
  public static final Log log = LogFactory.getLog(SoundPackageUtil.class);
  /**
   * blank character {@value}
   */
  public static final String SP = " ";
  /**
   * slash character {@value}
   */
  public static final String SL = "/";
  /**
   * the default encoding for the xml files and streams {@value}
   */
  public static final String ENCODING = "UTF-8";
  /**
   * the buffer size of the file buffer {@value}
   */
  public static final int BUF_SIZE = 1024;
  public static final String SOUND_DATA_FILE_EXT = ".axs";
  public static final String SOUNDS_PATH_PREFIX = "sounds";
  public static final String pattern = "dd MM yyyy HH:mm";
  public static final DateLocaleConverter dateConverter =
      new DateLocaleConverter(Locale.getDefault(), pattern);
  public static final int WAV_PREAMBEL_LEN = 0x3A;
  public static final String PACKAGE_INFO = "package-info.xml";
  private static final String PUBLIC_KEY_HASH =
      "E9 A1 51 5A A1 FA 27 AE DA C7 B0 00 9B 86 E9 85";
  private static final String PRIVATE_KEY_FILE =
      "/resources/sounds.priv.rsa";
  private static final String PUBLIC_KEY_FILE =
      "/resources/sounds.pub.rsa";
  private static final String LICENSE_KEY_ENTRY = "license.key";

  /**
   * Enumeration with all node types of the package-info.xml.
   */
  public static enum SoundPackageNodes {

    axboSounds, packageName, creator, creationDate, security, serialNumber,
    enforced, sounds, sound, displayName, axboFile, path, type
  };

  /**
   * Enumeration with all possible attributes of the package-info.xml.
   */
  public static enum SoundPackageAttributes {

    id
  };

  static {
    ConvertUtils.register(new EnumConverter(), SoundType.class);
    ConvertUtils.register(dateConverter, Date.class);
  }

  /**
   * Reads meta information from package-info.xml (as stream)
   *
   * @param packageInfoXmlStream the package-info.xml FileInputStream
   * @return the sound package info read from the stream
   * @throws com.dreikraft.infactory.sound.SoundPackageException encapsulates
   * all low level (IO) exceptions
   */
  public static SoundPackage readPackageInfo(InputStream packageInfoXmlStream)
      throws SoundPackageException {
    Digester digester = new Digester();
    digester.setValidating(false);
    digester.setRules(new ExtendedBaseRules());

    digester.addObjectCreate(SoundPackageNodes.axboSounds.toString(),
        SoundPackage.class);

    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.packageName, "name");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.creator, "creator");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.creationDate, "creationDate");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.security + SL + SoundPackageNodes.serialNumber,
        "serialNumber");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.security + SL + SoundPackageNodes.enforced,
        "securityEnforced");

    digester.addObjectCreate(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds, ArrayList.class);
    digester.addSetNext(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds, "setSounds");

    digester.addObjectCreate(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound, Sound.class);
    digester.addSetNext(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound, "add");
    digester.addSetProperties(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound, "id", "id");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound + SL
        + SoundPackageNodes.displayName, "name");

    digester.addObjectCreate(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound + SL
        + SoundPackageNodes.axboFile, SoundFile.class);
    digester.addSetNext(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound + SL
        + SoundPackageNodes.axboFile, "setAxboFile");
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound + SL
        + SoundPackageNodes.axboFile + SL + SoundPackageNodes.path);
    digester.addBeanPropertySetter(SoundPackageNodes.axboSounds + SL
        + SoundPackageNodes.sounds + SL + SoundPackageNodes.sound + SL
        + SoundPackageNodes.axboFile + SL + SoundPackageNodes.type);

    try {
      SoundPackage soundPackage = (SoundPackage) digester.parse(
          packageInfoXmlStream);
      return soundPackage;
    } catch (Exception ex) {
      throw new SoundPackageException(ex);
    }
  }

  /**
   * retrieves an entry from the package file (ZIP file)
   *
   * @param packageFile the sound package file
   * @param entryName the name of the entry in the ZIP file
   * @throws com.dreikraft.infactory.sound.SoundPackageException encapsulates
   * all low level (IO) exceptions
   * @return the entry data as stream
   */
  public static InputStream getPackageEntryStream(File packageFile,
      String entryName) throws SoundPackageException {
    if (packageFile == null) {
      throw new SoundPackageException(new IllegalArgumentException(
          "missing package file"));
    }

    InputStream keyIn = null;
    try {
      ZipFile packageZip = new ZipFile(packageFile);

      // get key from package
      ZipEntry keyEntry = packageZip.getEntry(LICENSE_KEY_ENTRY);
      keyIn = packageZip.getInputStream(keyEntry);
      Key key = CryptoUtil.unwrapKey(keyIn, PUBLIC_KEY_FILE);

      // read entry
      ZipEntry entry = packageZip.getEntry(entryName);
      return new ZipClosingInputStream(packageZip,
          CryptoUtil.decryptInput(packageZip.getInputStream(entry), key));
    } catch (ZipException ex) {
      throw new SoundPackageException(ex);
    } catch (IOException ex) {
      throw new SoundPackageException(ex);
    } catch (CryptoException ex) {
      throw new SoundPackageException(ex);
    } finally {
      try {
        if (keyIn != null)
          keyIn.close();
      } catch (IOException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  /**
   * Extracts the packageFile and writes its content in temporary directory
   *
   * @param packageFile the packageFile (zip format)
   * @param tempDir the tempDir directory
   */
  public static void extractPackage(File packageFile, File tempDir)
      throws SoundPackageException {

    if (packageFile == null) {
      throw new SoundPackageException(new IllegalArgumentException(
          "missing package file"));
    }

    ZipFile packageZip = null;
    try {
      packageZip = new ZipFile(packageFile);
      Enumeration<?> entries = packageZip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        String entryName = entry.getName();
        if (log.isDebugEnabled())
          log.debug("ZipEntry name: " + entryName);
        if (entry.isDirectory()) {
          File dir = new File(tempDir, entryName);
          if (dir.mkdirs())
            log.info("successfully created dir: " + dir.getAbsolutePath());
        } else {
          FileUtil.createFileFromInputStream(getPackageEntryStream(packageFile,
              entryName),
              tempDir + File.separator + entryName);
        }
      }
    } catch (FileNotFoundException ex) {
      throw new SoundPackageException(ex);
    } catch (IOException ex) {
      throw new SoundPackageException(ex);
    } finally {
      try {
        if (packageZip != null)
          packageZip.close();
      } catch (IOException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  /**
   * saves a sound package with all meta information and audio files to a ZIP
   * file and creates the security tokens.
   *
   * @param packageFile the zip file, where the soundpackage should be stored
   * @param soundPackage the sound package info
   * @throws com.dreikraft.infactory.sound.SoundPackageException encapsulates
   * all low level (IO) exceptions
   */
  public static void exportSoundPackage(final File packageFile,
      final SoundPackage soundPackage)
      throws SoundPackageException {

    if (packageFile == null) {
      throw new SoundPackageException(new IllegalArgumentException(
          "null package file"));
    }

    if (packageFile.delete()) {
      log.info("successfully deleted file: " + packageFile.getAbsolutePath());
    }


    ZipOutputStream out = null;
    InputStream in = null;
    try {
      out = new ZipOutputStream(new FileOutputStream(packageFile));
      out.setLevel(9);

      // write encryption key
      Key key = CryptoUtil.generateAESKey();
      ByteArrayInputStream keyIn = new ByteArrayInputStream(
          CryptoUtil.wrapKey(key, PRIVATE_KEY_FILE));
      writeZipEntry(LICENSE_KEY_ENTRY, out, keyIn);

      // write package info
      writePackageInfoZipEntry(soundPackage, out, key);

      // create path entries
      ZipEntry soundDir = new ZipEntry(SOUNDS_PATH_PREFIX + SL);
      out.putNextEntry(soundDir);
      out.flush();
      out.closeEntry();

      // write files
      for (Sound sound : soundPackage.getSounds()) {
        File axboFile = new File(sound.getAxboFile().getPath());

        in = new FileInputStream(axboFile);
        writeZipEntry(SOUNDS_PATH_PREFIX + SL + axboFile.getName(),
            out, in);
        in.close();
      }
    } catch (FileNotFoundException ex) {
      throw new SoundPackageException(ex);
    } catch (IOException ex) {
      throw new SoundPackageException(ex);
    } catch (CryptoException ex) {
      throw new SoundPackageException(ex);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException ex) {
          log.error("failed to close ZipOutputStream", ex);
        }
      }
      try {
        if (in != null)
          in.close();
      } catch (IOException ex) {
        log.error("failed to close FileInputStream", ex);
      }
    }
  }

  private static void writePackageInfoZipEntry(final SoundPackage soundPackage,
      final ZipOutputStream out, final Key key) throws
      UnsupportedEncodingException,
      IOException, CryptoException {
    // write xml to temporary byte array, because of stripping problems, when
    // directly writing to encrypted stream
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    OutputFormat format = OutputFormat.createPrettyPrint();
    format.setEncoding(ENCODING);
    XMLWriter writer = new XMLWriter(bOut, format);
    writer.setEscapeText(true);
    writer.write(createPackageInfoXml(soundPackage));
    writer.close();

    // write temporary byte array to encrypet zip entry
    ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
    writeZipEntryEncrypted(PACKAGE_INFO, out, bIn, key);
  }

  private static void writeZipEntry(final String entryName,
      final ZipOutputStream out, final InputStream in)
      throws IOException, CryptoException {
    try {
      ZipEntry fileEntry = new ZipEntry(entryName);
      out.putNextEntry(fileEntry);
      final byte[] buf = new byte[BUF_SIZE];
      int avail;
      while ((avail = in.read(buf)) != -1) {
        out.write(buf, 0, avail);
      }
    } finally {
      out.flush();
      out.closeEntry();
    }
  }

  private static void writeZipEntryEncrypted(final String entryName,
      final ZipOutputStream out, final InputStream in, final Key key)
      throws IOException, CryptoException {
    try {
      ZipEntry fileEntry = new ZipEntry(entryName);
      out.putNextEntry(fileEntry);
      InputStream encIn = CryptoUtil.encryptInput(in, key);
      final byte[] buf = new byte[BUF_SIZE];
      int avail;
      while ((avail = encIn.read(buf)) != -1) {
        out.write(buf, 0, avail);
      }
    } finally {
      out.flush();
      out.closeEntry();
    }
  }

  /**
   * creates a package-info.xml from the SoundPackage Bean
   *
   * @param soundPackage a SoundPackage Bean containing all the meta information
   * @return a dom4j document
   */
  public static Document createPackageInfoXml(final SoundPackage soundPackage) {

    Document document = DocumentHelper.createDocument();
    document.setXMLEncoding(ENCODING);

    Element rootNode =
        document.addElement(SoundPackageNodes.axboSounds.toString());
    rootNode.addElement(SoundPackageNodes.packageName.toString()).
        addText(soundPackage.getName());
    rootNode.addElement(SoundPackageNodes.creator.toString()).
        addText(soundPackage.getCreator());
    rootNode.addElement(SoundPackageNodes.creationDate.toString()).
        addText(new SimpleDateFormat(pattern).format(soundPackage.
        getCreationDate()));

    Element securityNode =
        rootNode.addElement(SoundPackageNodes.security.toString());
    securityNode.addElement(SoundPackageNodes.serialNumber.toString()).
        addText(soundPackage.getSerialNumber());
    securityNode.addElement(SoundPackageNodes.enforced.toString()).
        addText("" + soundPackage.isSecurityEnforced());

    Element soundsNode =
        rootNode.addElement(SoundPackageNodes.sounds.toString());
    int id = 1;
    for (Sound sound : soundPackage.getSounds()) {
      Element soundNode =
          soundsNode.addElement(SoundPackageNodes.sound.toString());
      soundNode.addAttribute(SoundPackageAttributes.id.toString(),
          String.valueOf(id));
      soundNode.addElement(SoundPackageNodes.displayName.toString()).addText(
          sound.getName());

      Element axboFileNode = soundNode.addElement(
          SoundPackageNodes.axboFile.toString());
      axboFileNode.addElement(SoundPackageNodes.path.toString()).setText(
          sound.getAxboFile().extractName());
      axboFileNode.addElement(SoundPackageNodes.type.toString()).setText(
          sound.getAxboFile().getType().toString());

      id++;
    }
    return document;
  }

  /**
   * verifies the security token of the SoundPackage. Verifies that the
   * SoundPackage has not been altered
   *
   * @param packageFile the SoundPackage ZIP file
   * @return true if the sound package has not been altered. False if somebody
   * changed the contents of the sound package.
   */
  public static boolean verifyPackage(File packageFile) {
    try {
      // check, whether the public key file has not been changed
      byte[] pubKeyBytes = CryptoUtil.readKey(PUBLIC_KEY_FILE);
      if (!PUBLIC_KEY_HASH.equals(
          ByteUtil.dumpByteArray(CryptoUtil.calcMD5(pubKeyBytes)).trim())) {
        return false;
      }
      return true;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return false;
    }
  }

  /**
   * calculate the size of all audio files in this package that will be uploaded
   * to the aXbo clock.
   *
   * @param soundPackage
   * @return
   */
  public static long calculateSoundFilesSize(SoundPackage soundPackage) {
    int size = 0;
    for (Sound sound : soundPackage.getSounds()) {
      File f = new File(sound.getAxboFile().getPath());
      size += (f.length() - WAV_PREAMBEL_LEN);
    }
    return size;
  }

  /**
   * Checks if for every {@link Sound} object a name and axbo file was set.
   * Return
   * <CODE>null</CODE> if all names and files were provided otherwise a
   * {@link String} with the bundle key for the error message is returned.
   *
   * @param sounds <CODE>List</CODE> with {@link Sound} objects
   * @return <CODE>null</CODE> if all names and files are set otherwise return
   * {@link String} with resource bundle key for the according error message.
   */
  public static void validateSoundPackage(SoundPackage soundPackage) throws
      SoundPackageException {
    // check if soundpackage name was typed in
    if (StringUtil.isEmpty(soundPackage.getName())) {
      throw new MissingSoundPackageNameException();
    }
    // check if serial number is empty
    if (StringUtil.isEmpty(soundPackage.getSerialNumber())) {
      throw new MissingSerialNumberException();
    }
    // check if every sound is complete
    List<Sound> sounds = soundPackage.getSounds();
    for (Sound sound : sounds) {
      if (StringUtil.isEmpty(sound.getName())) {
        throw new MissingSoundNameException();
      }
      if (sound.getAxboFile() == null) {
        throw new MissingSoundFileException();
      }
    }
  }

  public static File validateSoundPackageFilename(File f) {
    String filename = f.getName();
    if (!filename.endsWith(SOUND_DATA_FILE_EXT)) {
      return new File(f.getPath() + SOUND_DATA_FILE_EXT);
    } else {
      return f;
    }
  }
}
