package org.eark.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSFiler extends Filer {

  // private static Log LOG = LogFactory.getLog(HDFSFiler.class);

  /**
   * Hadoop Filesystem handle.
   */
  private/* final */FileSystem hdfs;

  /**
   * File to handle by this filer
   */
  private Path basePath;

  public HDFSFiler(String fsBasePath) throws IOException, URISyntaxException {
    super(fsBasePath);
    org.apache.log4j.BasicConfigurator.configure();
    org.apache.log4j.Logger.getRootLogger().setLevel(
        org.apache.log4j.Level.ERROR);
    URI fsBaseURI = new URI(fsBasePath);
    this.basePath = new Path(fsBaseURI);
    // Log LOG = LogFactory.getLog(Configuration.class);
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
    hdfs = FileSystem.get(hadoopConf);
    basePath = new Path(hdfs.getHomeDirectory(), basePath);
  }

  // @Override
  public String writeFile(InputStream fileInputStream, String fileName)
      throws IOException {

    Path outFile = new Path(basePath, fileName);
    OutputStream outputStream = hdfs.create(outFile);
    writeFile(fileInputStream, outputStream);
    return outFile.toUri().toString();

  }

  @Override
  public void write(OutputStream outputStream, String fileName)
      throws IOException {
    // TODO Auto-generated method stub

  }

}
