package org.eu.eark.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FSFiler extends Filer {

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
    writeFile(fileInputStream, outputStream);
    return file.getPath();
  }

  @Override
  public void write(OutputStream outputStream, String fileName)
      throws IOException {

    String path = fsBasePath + File.separator + fileName;
    File file = new File(path);
    InputStream inputStream = new FileInputStream(file);
    
    byte[] buffer = new byte[1024];
    int bytesRead;

    while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > 0) {
      outputStream.write(buffer, 0, bytesRead);
      outputStream.flush();
    }

    outputStream.close();
    inputStream.close();

  }

}
