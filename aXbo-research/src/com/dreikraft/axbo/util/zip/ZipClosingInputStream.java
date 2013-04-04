/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dreikraft.axbo.util.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 *
 * @author jan_solo
 */
public class ZipClosingInputStream extends InputStream {

  private ZipFile zip;
  private InputStream in;
  
  public ZipClosingInputStream(ZipFile zip, InputStream in)
  {
    this.zip = zip;
    this.in = in;
  }
  
  @Override
  public int read() throws IOException
  {
    return in.read();
  }

  @Override
  public int available() throws IOException
  {
    return in.available();
  }

  @Override
  public void close() throws IOException
  {
    in.close();
    zip.close();
  }

  @Override
  public synchronized void mark(int readlimit)
  {
    in.mark(readlimit);
  }

  @Override
  public boolean markSupported()
  {
    return in.markSupported();
  }

  @Override
  public int read(byte[] b) throws IOException
  {
    return in.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException
  {
    return in.read(b, off, len);
  }

  @Override
  public synchronized void reset() throws IOException
  {
    in.reset();
  }

  @Override
  public long skip(long n) throws IOException
  {
    return in.skip(n);
  }

  
}
