package org.eu.eark.hsink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.eu.eark.hsink.properties.ConfigProperties;

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
    //hadoop requires log4j initialization
    org.apache.log4j.BasicConfigurator.configure();
    org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ERROR);
    URI fsBaseURI = new URI(fsBasePath);
    this.basePath = new Path(fsBaseURI);
    org.apache.hadoop.conf.Configuration hadoopConf = 
        new org.apache.hadoop.conf.Configuration(true);
    String pathToCoreSite = ConfigProperties.getInstance().getProperty("core-site");
    String pathToHdfsSite = ConfigProperties.getInstance().getProperty("hdfs-site");
    if(pathToCoreSite != null && !pathToCoreSite.equals(""))
      hadoopConf.addResource(pathToCoreSite);
    if(pathToHdfsSite != null && !pathToHdfsSite.equals(""))
      hadoopConf.addResource(pathToHdfsSite);
    // hadoopConf.set("fs.defaultFS", "hdfs://localhost:8020");
    if(hadoopConf.get("fs.default.name") == null || 
       hadoopConf.get("fs.default.name").equals("") ||
       hadoopConf.get("fs.default.name").equals("file:///")) {
      String defaultFsDefaultName = ConfigProperties.getInstance().getProperty("fs.default.name");
      if(defaultFsDefaultName == null || defaultFsDefaultName.equals(""))
        defaultFsDefaultName = "hdfs://localhost:8020";
      LOG.info("fs.default.name not detected from core-site.xml, defaulting to " +
          defaultFsDefaultName);      
      hadoopConf.set("fs.default.name", defaultFsDefaultName);
    }
    // configure implementation for local and remote fs
    hadoopConf.set("fs.hdfs.impl",
        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
    hadoopConf.set("fs.file.impl",
        org.apache.hadoop.fs.LocalFileSystem.class.getName());
    LOG.fine("HDFSFiler() - before getFS");
    hdfs = FileSystem.get(hadoopConf);
    LOG.fine("HDFSFiler() - after getFS");
    LOG.fine("HDFS working dir: "+hdfs.getWorkingDirectory()+ " home dir: "+hdfs.getHomeDirectory()+ " basePath: "+basePath.toString()
          + "fs.default.name: "+hadoopConf.get("fs.default.name"));
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
  
  @Override
  public ArrayList<String> getDirNames() throws IOException {
    Path path=basePath;
    LOG.info("getDirNames() in: "+basePath);
    LOG.info("getDirNames() in: "+fsBasePath);
    ArrayList<String> names = new ArrayList<String>();
    
    //RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(new Path(fsBasePath), false);
    //RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(basePath, false);
    
    FileStatus[] status = hdfs.listStatus(basePath);
    LOG.fine("found entries in data directory: "+status.length);
    
    for (int i=0;i<status.length;i++){
        String fileName = status[i].getPath().getName();
        LOG.info("HDFS found dirName in fsBasePath: "+fileName);
        names.add(fileName);
    }
    
    //while(it.hasNext()) {
    //  String fileName = it.next().getPath().getName();
    //  LOG.info("HDFS found dirName in fsBasePath: "+fileName);
    //  names.add(fileName);
    //}

    return names;
    
  }

}
