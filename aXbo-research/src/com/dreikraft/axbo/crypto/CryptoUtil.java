/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreikraft.axbo.crypto;

import com.dreikraft.axbo.crypto.CryptoUtil.KeyType;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jan_solo
 */
public class CryptoUtil
{
  public static final Log log = LogFactory.getLog(CryptoUtil.class);

  public static final String RSA_ALGORITHM = "RSA";
  public static final String AES_ALGORITHM = "AES";
  private static final int BUF_SIZE = 1024;

  public enum KeyType
  {
    PRIVATE, PUBLIC
  };

  
  public static Key getPublicRSAKey(final String fileName) throws 
      CryptoException
  {
    try
    {
      // create the RSA keys from keySpec
      final KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
      return keyFactory.generatePublic(
          new X509EncodedKeySpec(readKey(fileName)));
    }
    catch (InvalidKeySpecException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  
  public static Key getPrivateRSAKey(final String fileName) throws 
      CryptoException
  {
    try
    {
      // create the RSA keys from keySpec
      final KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
      return keyFactory.generatePrivate(
          new PKCS8EncodedKeySpec(readKey(fileName)));
    }
    catch (InvalidKeySpecException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  
  public static Key generateAESKey() throws CryptoException
  {
    try
    {
      KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
      keyGen.init(128);
      return keyGen.generateKey();
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  
  public static byte[] wrapKey(final Key key, final String keyFile) 
      throws CryptoException
  {
    try
    {
      final Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
      rsaCipher.init(Cipher.WRAP_MODE, getPrivateRSAKey(keyFile));
      return rsaCipher.wrap(key);
    }
    catch (IllegalBlockSizeException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (CryptoException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  
  public static Key unwrapKey(final InputStream in, final String keyFile) 
      throws CryptoException
  {
    try
    {
      final Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
      rsaCipher.init(Cipher.UNWRAP_MODE, getPublicRSAKey(keyFile));

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final byte[] buf = new byte[BUF_SIZE];
      int avail = 0;
      while ((avail = in.read(buf)) != -1)
      {
        out.write(buf, 0, avail);
      }
      return rsaCipher.unwrap(out.toByteArray(), AES_ALGORITHM,
          Cipher.SECRET_KEY);
    }
    catch (IOException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (CryptoException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  
  public static byte[] readKey(final String fileName) throws CryptoException
  {
    BufferedInputStream keyIn = null;
    ByteArrayOutputStream keyOut = null;
    try
    {
      keyIn = new BufferedInputStream(
          CryptoUtil.class.getResourceAsStream(fileName), BUF_SIZE);
      keyOut = new ByteArrayOutputStream();
      final byte[] keyBuffer = new byte[BUF_SIZE];
      int available = 0;
      while ((available = keyIn.read(keyBuffer)) != -1)
      {
        keyOut.write(keyBuffer, 0, available);
      }
      keyOut.flush();

      return keyOut.toByteArray();
    }
    catch (IOException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    finally
    {
      try
      {
        if (keyIn != null)
        {
          keyIn.close();
        }
        if (keyOut != null)
        {
          keyOut.close();
        }
      }
      catch (IOException ex)
      {
        log.warn(ex.getMessage(), ex);
      }
    }
  }

  public static InputStream encryptInput(final InputStream in, final Key key)
      throws CryptoException
  {
    try
    {
      Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
      aesCipher.init(Cipher.ENCRYPT_MODE, key);
      
      InputStream cin = new CipherInputStream(in, aesCipher);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[BUF_SIZE];
      int avail = 0;
      while ((avail = cin.read(buf)) != -1)
      {
        out.write(buf, 0, avail);
      }
      return new ByteArrayInputStream(out.toByteArray());
    }
    catch (IOException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }    
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  public static InputStream decryptInput(final InputStream in, final Key key)
      throws CryptoException
  {
    try
    {
      Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
      aesCipher.init(Cipher.DECRYPT_MODE, key);

      InputStream cin = new CipherInputStream(in, aesCipher);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[BUF_SIZE];
      int avail = 0;
      while ((avail = cin.read(buf)) != -1)
      {
        out.write(buf, 0, avail);
      }
      return new ByteArrayInputStream(out.toByteArray());
    }
    catch (IOException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }    
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  public static void crypt(Key key, InputStream in, OutputStream out,
      String algorithm, int mode) throws CryptoException
  {
    try
    {
      InputStream encIn = getCipherInputStream(key, in, algorithm, mode);
      byte[] buf = new byte[BUF_SIZE];
      int avail = 0;
      while ((avail = encIn.read(buf)) != -1)
      {
        out.write(buf, 0, avail);
      }
      out.flush();
    }
    catch (IOException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  public static CipherInputStream getCipherInputStream(Key key, InputStream in,
      String algorithm, int mode) throws CryptoException
  {
    try
    {
      Cipher rsaCipher = Cipher.getInstance(algorithm);
      rsaCipher.init(mode, key);

      // encrypt input
      return new CipherInputStream(in, rsaCipher);
    }
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  public static CipherOutputStream getCipherOutputStream(Key key,
      OutputStream out, String algorithm, int mode) throws CryptoException
  {
    try
    {
      // initialize RSA Cipher
      Cipher rsaCipher = Cipher.getInstance(algorithm);
      rsaCipher.init(mode, key);

      // encrypt input
      return new CipherOutputStream(out, rsaCipher);
    }
    catch (InvalidKeyException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchPaddingException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CryptoException(ex.getMessage(), ex);
    }
  }

  public static byte[] calcMD5(byte[] b) throws NoSuchAlgorithmException
  {
    MessageDigest md = MessageDigest.getInstance("MD5");
    return md.digest(b);
  }
}
