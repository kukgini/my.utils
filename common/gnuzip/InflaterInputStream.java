/* java.util.zip.InflaterInputStream
   Copyright (C) 2001 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnuzip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This filter stream is used to decompress data compressed in the "deflate"
 * format. The "deflate" format is described in RFC 1951.
 *
 * This stream may form the basis for other decompression filters, such
 * as the <code>GZIPInputStream</code>.
 *
 * @author John Leuner
 * @since JDK 1.1
 */

public class InflaterInputStream extends FilterInputStream {

  //Variables

  /**
   * Decompressor for this filter 
   */

  protected Inflater inf;

  /**
   * Byte array used as a buffer 
   */

  protected byte[] buf;

  /**
   * Size of buffer   
   */

  protected int len;


  //We just use this if we are decoding one byte at a time with the read() call

  private byte[] onebytebuffer = new byte[1];

  //Constructors


  /**
   * Create an InflaterInputStream with the default decompresseor
   * and a default buffer size.
   *
   * @param in the InputStream to read bytes from
   */

  public InflaterInputStream(InputStream in) 
  {
    this(in, new Inflater(), 4096);
  }

  /**
   * Create an InflaterInputStream with the specified decompresseor
   * and a default buffer size.
   *
   * @param in the InputStream to read bytes from
   * @param inf the decompressor used to decompress data read from in
   */

  public InflaterInputStream(InputStream in, Inflater inf) 
  {
    this(in, inf, 4096);
  }

  /**
   * Create an InflaterInputStream with the specified decompresseor
   * and a specified buffer size.
   *
   * @param in the InputStream to read bytes from
   * @param inf the decompressor used to decompress data read from in
   * @param size size of the buffer to use
   */

  public InflaterInputStream(InputStream in, Inflater inf, int size) 
  {
    super(in);
    this.inf = inf;
    this.len = 0;
    
    if (size <= 0)
      throw new IllegalArgumentException("size <= 0");
    buf = new byte[size]; //Create the buffer
  }

  //Methods

  /**
   * Returns 0 once the end of the stream (EOF) has been reached.
   * Otherwise returns 1.
   */

  public int available() throws IOException
  {
    return inf.finished() ? 0 : 1;
  }

  /**
   * Closes the input stream
   */
  public void close() throws IOException
  {
    in.close();
  }

  /**
   * Fills the buffer with more data to decompress.
   */
  protected void fill() throws IOException
  {
    len = in.read(buf, 0, buf.length);

    if (len < 0)
      throw new ZipException("Deflated stream ends early.");
    inf.setInput(buf, 0, len);
  }

  /**
   * Reads one byte of decompressed data.
   *
   * The byte is in the lower 8 bits of the int.
   */
  public int read() throws IOException
  { 
    int nread = read(onebytebuffer, 0, 1); //read one byte
    if (nread > 0)
      return onebytebuffer[0] & 0xff;
    return -1;
  }

  /**
   * Decompresses data into the byte array
   *
   *
   * @param b the array to read and decompress data into
   * @param off the offset indicating where the data should be placed
   * @param len the number of bytes to decompress
   */
  public int read(byte[] b, int off, int len) throws IOException
  {
    for (;;)
      {
	int count;
	try
	  {
	    count = inf.inflate(b, off, len);
	  } 
	catch (DataFormatException dfe) 
	  {
	    throw new ZipException(dfe.getMessage());
	  }

	if (count > 0)
	  return count;
	
	if (inf.needsDictionary())
	  throw new ZipException("Need a dictionary");
	else if (inf.finished())
	  return -1;
	else if (inf.needsInput())
	  fill();
	else
	  throw new InternalError("Don't know what to do");
      }
  }

  /**
   * Skip specified number of bytes of uncompressed data
   *
   * @param n number of bytes to skip
   */

  public long skip(long n) throws IOException
  {
    if (n < 0)
      throw new IllegalArgumentException();
    int len = 2048;
    if (n < len)
      len = (int) n;
    byte[] tmp = new byte[len];
    return (long) read(tmp);
  }
}
