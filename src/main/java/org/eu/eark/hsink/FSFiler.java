package org.eu.eark.hsink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FSFiler extends Filer {

  /*
   * Filer implementation for local file system
   */
  public FSFiler(String fsBasePath) {
    super(fsBasePath);
  }

  @Override
  public String writeFile(InputStream fileInputStream, String fileName)
      throws IOException {

    String path = fsBasePath + File.separator + fileName;
    File file = new File("path");
    if (!file.exists()) {
      file.createNewFile();
    }

    OutputStream outputStream = new FileOutputStream(path);
    write(fileInputStream, outputStream);
    return file.getPath();
  }

  @Override
  public void writeStream(OutputStream outputStream, String fileName)
      throws IOException {

    String path = fsBasePath + File.separator + fileName;
    File file = new File(path);
    InputStream inputStream = new FileInputStream(file);
    write(inputStream, outputStream);
  }

}
