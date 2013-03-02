package com.lemoulinstudio.bikefriend.webapp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 *
 * @author Vincent Cantin
 */
public class InputStreamSequence extends InputStream {
  
  private final Iterator<InputStream> it;
  private InputStream in;

  public InputStreamSequence(Iterator<InputStream> it) {
    this.it = it;
  }

  @Override
  public int available() throws IOException {
    if (in == null) {
      if (it.hasNext()) {
        in = it.next();
      }
      else {
        return 0;
      }
    }
    
    return in.available();
  }

  @Override
  public long skip(long n) throws IOException {
    if (in == null) {
      if (it.hasNext()) {
        in = it.next();
      }
      else {
        return 0;
      }
    }
    
    long remaining = n;
    
    while (true) {
      long skipped = in.skip(remaining);
      remaining -= skipped;
      
      if (remaining > 0) {
        in.close();
        
        if (it.hasNext()) {
          in = it.next();
        }
        else {
          break;
        }
      }
    }
    
    return n - remaining;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (in == null) {
      if (it.hasNext()) {
        in = it.next();
      }
      else {
        return 0;
      }
    }
    
    while (true) {
      int nbBytes = in.read(b, off, len);
      
      if (nbBytes > 0) {
        return nbBytes;
      }
      else {
        in.close();
        
        if (it.hasNext()) {
          in = it.next();
        }
        else {
          return 0;
        }
      }
    }
  }

  @Override
  public int read() throws IOException {
    if (in == null) {
      if (it.hasNext()) {
        in = it.next();
      }
      else {
        return -1;
      }
    }
    
    while (true) {
      int value = in.read();

      if (value != -1) {
        return value;
      }
      else {
        in.close();
        
        if (it.hasNext()) {
          in = it.next();
        }
        else {
          return -1;
        }
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (in == null) {
      if (it.hasNext()) {
        in = it.next();
      }
      else {
        return;
      }
    }
    
    while (it.hasNext()) {
      in = it.next();
      in.close();
    }
  }
  
}
