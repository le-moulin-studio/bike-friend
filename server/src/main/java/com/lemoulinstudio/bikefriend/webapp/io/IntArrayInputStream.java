package com.lemoulinstudio.bikefriend.webapp.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Vincent Cantin
 */
public class IntArrayInputStream extends InputStream {
  
  private final int[] data;
  private int index; // Index in the stream, it counts the bytes.

  public IntArrayInputStream(int[] data) {
    this.data = data;
    this.index = 0;
  }

  @Override
  public int read() throws IOException {
    int val = data[index / 4] >> ((index % 4) * 8) & 0xff;
    index++;
    return val;
  }

  @Override
  public int available() throws IOException {
    return data.length * 4 - index;
  }

  @Override
  public boolean markSupported() {
    return false;
  }

}
