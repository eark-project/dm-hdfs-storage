package org.eu.eark.hsink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
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

  // @Override
  public String writeFile(InputStream fileInputStream, String fileName)
      throws IOException {

    LOG.fine("HDFSFiler.writeFile()");
    Path outFile = new Path(basePath, fileName);
    OutputStream outputStream = hdfs.create(outFile);
    write(fileInputStream, outputStream);
    LOG.fine("HDFSFiler.writeFile() - done");    
    return outFile.toUri().toString();
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

}
