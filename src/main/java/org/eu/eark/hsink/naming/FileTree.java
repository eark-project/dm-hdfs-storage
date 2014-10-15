package org.eu.eark.hsink.naming;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eu.eark.hsink.Main;

public class FileTree {
  
  private final static Logger LOG = Logger.getLogger(Main.class.getName());
  private static FileTree fileTree = null; 
  protected TreeSet<DirName> dirNames = null; 
  
  protected FileTree() {
    this.dirNames = new TreeSet<DirName>();
    LOG.fine("file tree initialized");
  }
  
  public static FileTree getInstance() {
    if(fileTree == null)
      fileTree = new FileTree();
    return fileTree;
  }
  
  public synchronized String nextDirName() {
    long count = 0L;
    if(dirNames.size() > 0) {
      DirName last = dirNames.last();
      count = Long.valueOf(last.getCount());
    } 
    DirName next = new DirName();
    next.setCount(new Long(count+1L).toString());
    dirNames.add(next);
    LOG.fine("new dir name: "+next);
    return next.toString();
  }
  
  class DirName implements Comparable<DirName> {
    
    String count = null;
    String date = null;    
    public  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public DirName() {
      date = dateFormat.format(new Date());
    }
        
    public String toString() {
      return new StringBuffer().append(count).append('.').append(date).toString();
    }
    
    public String getCount() {
      return count;
    }
    public void setCount(String count) {
      this.count = count;
    }
    public String getDate() {
      return date;
    }
    public void setDate(String date) {
      this.date = date;
    }
    
    @Override
    public int compareTo(DirName dirName) {
      // TODO Auto-generated method stub
      return this.toString().compareTo(dirName.toString());
    }
  }
  
}
