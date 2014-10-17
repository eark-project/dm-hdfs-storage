package org.eu.eark.hsink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSFiler extends Filer {

  private final static Logger LOG = Logger.getLogger(HDFSFiler.class.getName());

  private FileSystem hdfs;
  private Path basePath;

  /*
   * Filer implmenetation for HDFS
   */
  public HDFSFiler(String fsBasePath) throws IOException, URISyntaxException {
    super(fsBasePath);
    LOG.fine("HDFSFiler()");
    org.apache.log4j.BasicConfigurator.configure();
    org.apache.log4j.Logger.getRootLogger().setLevel(
        org.apache.log4j.Level.ERROR);
    URI fsBaseURI = new URI(fsBasePath);
    this.basePath = new Path(fsBaseURI);
    org.apache.hadoop.conf.Configuration hadoopConf = new org.apache.hadoop.conf.Configuration(
        true);
    hadoopConf.addResource("/home/rainer/develop/hadoop/conf/core-site.xml");
    hadoopConf.addResource("/home/rainer/develop/hadoop/conf/hdfs-site.xml");
    // hadoopConf.set("fs.defaultFS", "hdfs://localhost:8020");
    hadoopConf.set("fs.default.name", "hdfs://localhost:9000/");
    // configure implementation for local and remote fs
    hadoopConf.set("fs.hdfs.impl",
        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
    hadoopConf.set("fs.file.impl",
        org.apache.hadoop.fs.LocalFileSystem.class.getName());
    LOG.fine("HDFSFiler() - before getFS");
    hdfs = FileSystem.get(hadoopConf);
    LOG.fine("HDFSFiler() - after getFS");
    basePath = new Path(hdfs.getHomeDirectory(), basePath);
    LOG.fine("HDFSFiler() - initialized. basePath="+basePath.toString());    
  }
  
  public String writeFile(InputStream fileInputStream, String fileName, String dirName)
      throws IOException {
    
    String path = "";
    Path filePath = basePath;
    if(dirName != null && !dirName.equals("")) {
      path = dirName+'/';
      filePath = new Path(filePath, dirName);
      if( hdfs.isFile(filePath) || hdfs.isDirectory(filePath))
         throw new IOException("attempt to create existing directory "+filePath);
      if(!hdfs.mkdirs(filePath))
        throw new IOException("error creating directury "+filePath);
    }    
    
    path = path + fileName;
    filePath = new Path(filePath, fileName); 
    if(hdfs.isFile(filePath) || hdfs.isDirectory(filePath))
      throw new IOException("attempt to create existing file "+filePath);
    OutputStream outputStream = hdfs.create(filePath);
    write(fileInputStream, outputStream);
    LOG.fine("HDFSFiler.writeFile() - done");    
    return path;
  }
      
  // @Override
  public String writeFile(InputStream fileInputStream, String fileName)
      throws IOException {

    return writeFile(fileInputStream, fileName, null);
  }

  @Override
  public void writeStream(OutputStream outputStream, String fileName)
      throws IOException {
    
    LOG.fine("HDFSFiler.writeStream()");
    Path inFile = new Path(basePath, fileName);
    InputStream inputStream = hdfs.open(inFile).getWrappedStream();
    write(inputStream, outputStream);
    LOG.fine("HDFSFiler.writeStream() - done");
  }
  
  public List readDirectories() {
    //hdfs.g
    return null;
  }

}
