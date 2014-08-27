package org.eark.hdfs;
//package org.apache.hadoop.conf;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Configuration implements Iterable<Map.Entry<String,String>>,
                                      Writable {
    
  
  private static final Log LOG =
    LogFactory.getLog(Configuration.class);

  private boolean quietmode = true;
  
  /**
   * List of configuration resources.
   */
  private ArrayList<Object> resources = new ArrayList<Object>();

  /**
   * List of configuration parameters marked <b>final</b>. 
   */
  private Set<String> finalParameters = new HashSet<String>();
  
  private boolean loadDefaults = true;
  
  /**
   * Configuration objects
   */
  private static final WeakHashMap<Configuration,Object> REGISTRY = 
    new WeakHashMap<Configuration,Object>();
  
  /**
   * List of default Resources. Resources are loaded in the order of the list 
   * entries
   */
  private static final CopyOnWriteArrayList<String> defaultResources =
    new CopyOnWriteArrayList<String>();
  
  /**
   * The value reported as the setting resource when a key is set
   * by code rather than a file resource.
   */
  static final String UNKNOWN_RESOURCE = "Unknown";
  
  /**
   * Stores the mapping of key to the resource which modifies or loads 
   * the key most recently
   */
  private HashMap<String, String> updatingResource;
  
  /*
  static{
    System.out.println("conf: 1");
    //print deprecation warning if hadoop-site.xml is found in classpath
    ClassLoader cL = Thread.currentThread().getContextClassLoader();
    if (cL == null) {
      cL = Configuration.class.getClassLoader();
    }
    if(cL.getResource("hadoop-site.xml")!=null) {
      LOG.warn("DEPRECATED: hadoop-site.xml found in the classpath. " +
          "Usage of hadoop-site.xml is deprecated. Instead use core-site.xml, "
          + "mapred-site.xml and hdfs-site.xml to override properties of " +
          "core-default.xml, mapred-default.xml and hdfs-default.xml " +
          "respectively");
    }
    addDefaultResource("core-default.xml");
    addDefaultResource("core-site.xml");
    System.out.println("conf: 2");
  }
  */
  
  private Properties properties;
  private Properties overlay;
  /*
  private ClassLoader classLoader;
  {
    System.out.println("conf: 3");
    classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = Configuration.class.getClassLoader();
    }
    System.out.println("conf: 4");
  }
  */
  
  /** A new configuration. */
  public Configuration() {
    this(true);
  }
  
  public static String getNix() {
      return "nix";
  }

  /** A new configuration where the behavior of reading from the default 
   * resources can be turned off.
   * 
   * If the parameter {@code loadDefaults} is false, the new instance
   * will not load resources from the default files. 
   * @param loadDefaults specifies whether to load from the default files
   */
  public Configuration(boolean loadDefaults) {
    System.out.println("Config: 1");
    this.loadDefaults = loadDefaults;
    updatingResource = new HashMap<String, String>();
/*
    synchronized(Configuration.class) {
      REGISTRY.put(this, null);
    }
*/
  }
  
  /** 
   * A new configuration with the same settings cloned from another.
   * 
   * @param other the configuration from which to clone settings.
   */
  /*
  @SuppressWarnings("unchecked")
  public Configuration(Configuration other) {
    this.resources = (ArrayList) other.resources.clone();
    synchronized (other) {
      if (other.properties != null) {
        this.properties = (Properties) other.properties.clone();
      }

      if (other.overlay != null) {
        this.overlay = (Properties) other.overlay.clone();
      }

      this.updatingResource = new HashMap<String, String>(
          other.updatingResource);
    }

    this.finalParameters = new HashSet<String>(other.finalParameters);
    synchronized (Configuration.class) {
      REGISTRY.put(this, null);
    }
  }
  */
  
  /**
   * Get an {@link Iterator} to go through the list of <code>String</code> 
   * key-value pairs in the configuration.
   * 
   * @return an iterator over the entries.
   */
  public Iterator<Map.Entry<String, String>> iterator() {
    // Get a copy of just the string to string pairs. After the old object
    // methods that allow non-strings to be put into configurations are removed,
    // we could replace properties with a Map<String,String> and get rid of this
    // code.
    Map<String,String> result = new HashMap<String,String>();
    /*
    for(Map.Entry<Object,Object> item: getProps().entrySet()) {
      if (item.getKey() instanceof String && 
          item.getValue() instanceof String) {
        result.put((String) item.getKey(), (String) item.getValue());
      }
    }
    */
    return result.entrySet().iterator();
  }

@Override
public void readFields(DataInput arg0) throws IOException {
    // TODO Auto-generated method stub
    
}

@Override
public void write(DataOutput arg0) throws IOException {
    // TODO Auto-generated method stub
    
}

}
