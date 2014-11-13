package org.eu.eark.hsink.naming;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
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
    if(strNames == null) return;
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
    LOG.fine("generating next dir name");
    DirName next = new DirName();
    if(dirNames.size() > 0) {
      DirName last = dirNames.first();
      LOG.fine("last dir name: "+last);
      LOG.fine("next.date: "+next.date+" last.date: "+last.date+" equals? "+next.date.equals(last.date));
      if(next.date.equals(last.date)) {
        next.setCount(last.getCount()+1L);
      }
    } 
    LOG.fine("new dir name: "+next);
    dirNames.add(next);
    return next.toString();
  }

  static class DirName implements Comparable<DirName> {
    
    long count = -1L;
    Date date = null;    
    public static final String DELIMITER = ".";
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public DirName() {
      try {
        date = dateFormat.parse(dateFormat.format(new Date()));
      } catch(ParseException e) {
        LOG.log(Level.SEVERE, "Unexpected error creating new DirName", e);
      }
      count = 1L;
    }
    
    private DirName(long count, String date) throws ParseException {
      this.count = count;
      this.date = dateFormat.parse(date);
    }
        
    public String toString() {
      return new StringBuffer().append(count).append(DELIMITER).append(dateFormat.format(date)).toString();
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
      return dateFormat.format(date);
    }
    public void setDate(String date) throws ParseException {
      this.date = dateFormat.parse(date);
    }
    
    @Override
    public int compareTo(DirName dirName) {
      //this.toString().compareTo(dirName.toString());      
      if(this.date.before(dirName.date)) return 1;
      if(this.date.after(dirName.date)) return -1;
      if(this.count < dirName.count) return 1;
      if(this.count > dirName.count) return -1;  
      return 0;
    }
  }
  
}
