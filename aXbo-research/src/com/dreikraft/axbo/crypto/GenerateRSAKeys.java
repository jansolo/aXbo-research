// $Id
package com.dreikraft.axbo.crypto;

import com.dreikraft.axbo.util.ByteUtil;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class GenerateRSAKeys
{
  public static final Log log = LogFactory.getLog(GenerateRSAKeys.class);

  public static void main(String[] args) throws java.io.IOException
  {
    try
    {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(new RSAKeyGenParameterSpec(2048,
          RSAKeyGenParameterSpec.F4));

      KeyPair keyPair = keyGen.generateKeyPair();

      RSAPrivateKey secretKey = (RSAPrivateKey) keyPair.getPrivate();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

      // Write out the ASN.1 and raw key files
      FileOutputStream fOut = new FileOutputStream("private_infactory.key");
      fOut.write(secretKey.getEncoded());
      fOut.close();

      fOut = new FileOutputStream("private_infactory.key.md5");
      fOut.write(ByteUtil.dumpByteArray(CryptoUtil.calcMD5(
          secretKey.getEncoded())).getBytes());
      fOut.close();

      fOut = new FileOutputStream("public_infactory.key");
      fOut.write(publicKey.getEncoded());
      fOut.close();

      fOut = new FileOutputStream("public_infactory.key.md5");
      fOut.write(ByteUtil.dumpByteArray(CryptoUtil.calcMD5(
          publicKey.getEncoded())).getBytes());
      fOut.close();

      log.info("RSA keys generated successfully.");
    }
    catch (NoSuchAlgorithmException ex)
    {
      log.fatal(ex.getMessage(), ex);
      return;
    }
    catch (InvalidAlgorithmParameterException ex)
    {
      log.fatal(ex.getMessage(), ex);
      return;
    }
  }
}

