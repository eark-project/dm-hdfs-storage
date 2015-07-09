package org.eu.eark.hsink;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class Filer {

  protected String fsBasePath;

  private final static Logger LOG = Logger.getLogger(Filer.class.getName());

  /*
   * Abstract class for handling file IO, which should be subclassed for
   * different file systems.
   */
  public Filer() {
  }

  public Filer(String fsBasePath) {
    this.fsBasePath = fsBasePath;
  }

  public void write(InputStream fileInputStream, OutputStream outputStream)
      throws IOException {
    try {

      LOG.fine("Filer.writeFile()");
      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        LOG.finest("read " +bytesRead + " bytes");
        outputStream.write(buffer, 0, bytesRead);
      }
      fileInputStream.close();
      outputStream.flush();
      LOG.fine("Filer.writeFile() - done");
    } catch(EOFException eof) {
      LOG.fine("writing: end of File reached");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      outputStream.close();
    }
  }

  public abstract String writeFile(InputStream fileInputStream, String fileName, String dirName)
      throws IOException;
  
  public abstract String writeFile(InputStream fileInputStream, String fileName)
      throws IOException;

  public abstract void writeStream(OutputStream outputStream, String fileName)
      throws IOException;
  
  public abstract ArrayList<String> getElements(String dirName)
      throws IOException;
  
  public ArrayList<String> getDirNames() throws IOException {
    return getElements("");
  }

  // public abstract int getMD5Sum(String path);

}
