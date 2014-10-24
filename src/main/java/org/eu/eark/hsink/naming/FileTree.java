package org.eu.eark.hsink.naming;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eu.eark.hsink.Filer;
import org.eu.eark.hsink.Main;

public class FileTree {
  
  private final static Logger LOG = Logger.getLogger(FileTree.class.getName());
  private static FileTree fileTree = null; 
  protected TreeSet<DirName> dirNames = null;
  protected Filer filer = null;
  
  protected FileTree(Filer filer) throws IOException {
    this.dirNames = new TreeSet<DirName>();
    this.filer = filer;
    initialize();
    LOG.fine("file tree initialized");
  }
  
  private void initialize() throws IOException {
    ArrayList<String> strNames = filer.getDirNames();
    Iterator<String> it = strNames.iterator();
    while(it.hasNext()) {
      String strName = it.next();
      DirName dirName = DirName.parse(strName);
      if(dirName != null) dirNames.add(dirName);
    }
      
  }
  
  public static FileTree getInstance(Filer filer) throws IOException {
    if(fileTree == null)
      fileTree = new FileTree(filer);
    return fileTree;
  }
  
  public synchronized String nextDirName() {
    long count = 0L;
    if(dirNames.size() > 0) {
      DirName last = dirNames.last();
      count = Long.valueOf(last.getCount());
    } 
    DirName next = new DirName();
    next.setCount(new Long(count+1L));
    dirNames.add(next);
    LOG.fine("new dir name: "+next);
    return next.toString();
  }
  
  static class DirName implements Comparable<DirName> {
    
    long count = -1L;
    String date = null;    
    public static final String DELIMITER = ".";
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public DirName() {
      date = dateFormat.format(new Date());
    }
    
    private DirName(long count, String date) {
      this.count = count;
      this.date = date;
    }
        
    public String toString() {
      return new StringBuffer().append(count).append(DELIMITER).append(date).toString();
    }
    
    public static DirName parse(String dirName) {
      try {
        LOG.fine("Parsing dirName: "+dirName);
        String[] valval = dirName.split(Pattern.quote(DELIMITER));
        LOG.fine("valval.length: "+ (valval == null ? "-null-" : valval.length));
        LOG.fine("Parsing result: "+valval[0]+" " +valval[1]);
        if(valval.length != 2) throw new Exception("error parsing directory name: "+dirName);
        String count = valval[0];
        Date date = dateFormat.parse(valval[1]);
        return new DirName(Long.valueOf(count), dateFormat.format(date));
      } catch(Exception e) {
        LOG.info("Ignoring invalid directory name: "+dirName+" cause: "+e);
        return null;
      }
    }
    
    public long getCount() {
      return count;
    }
    public void setCount(long count) {
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
