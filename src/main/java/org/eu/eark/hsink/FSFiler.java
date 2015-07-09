package org.eu.eark.hsink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FSFiler extends Filer {

  /*
   * Filer implementation for local file system
   */
  public FSFiler(String fsBasePath) {
    super(fsBasePath);
  }
  
  public String writeFile(InputStream fileInputStream, String fileName, String dirName)
      throws IOException {
    
    String path = "";
    String filePath = "";
    if(dirName != null && !dirName.equals("")) {
      path = dirName+'/';
      filePath = this.fsBasePath+File.separator+dirName;
      File file = new File(filePath);
      if(file.exists() || file.isDirectory())
         throw new IOException("attempt to create existing directory "+filePath);
      if(!file.mkdir())
        throw new IOException("error creating directury "+filePath);
    }    
    
    path = path + fileName;
    filePath = filePath+File.separator+fileName; 
    File file = new File(filePath);
    if(file.exists())
      throw new IOException("attempt to create existing file "+filePath);
    if (!file.createNewFile()) {
      throw new IOException("error creating file "+filePath);
    }

    OutputStream outputStream = new FileOutputStream(filePath);
    write(fileInputStream, outputStream);
    return path;
  }

  @Override
  public String writeFile(InputStream fileInputStream, String fileName)
      throws IOException {

    return writeFile(fileInputStream, fileName, null);
  }

  @Override
  public void writeStream(OutputStream outputStream, String fileName)
      throws IOException {

    String path = fsBasePath + File.separator + fileName;
    File file = new File(path);
    InputStream inputStream = new FileInputStream(file);
    write(inputStream, outputStream);
  }

  @Override
  public ArrayList<String> getElements(String dirName) throws IOException {
    File file = new File(fsBasePath + "/" + dirName);
    ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list())); 
    return names;
  }

}
