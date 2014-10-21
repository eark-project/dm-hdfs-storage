package org.eu.eark.hsink.properties;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eu.eark.hsink.FileResource;

public class ConfigProperties {
  
  private final static Logger LOG = Logger.getLogger(ConfigProperties.class.getName());
  protected static ConfigProperties serverProperties = null;
  protected Properties properties = null;
  
  protected ConfigProperties() {
    this.properties = new Properties();
    try {
      properties.load(FileResource.class.getResourceAsStream("/config.properties"));
    } catch (IOException e) {
      LOG.log(Level.WARNING, "No config.properties file found.");
    } 
    LOG.fine("server properties initialized");
  }
  
  public static ConfigProperties getInstance() {
    if(serverProperties == null)
      serverProperties = new ConfigProperties();
    return serverProperties;
  }
  
  public Properties getProperties() {
    return properties;
  }

  public String getProperty(String property) {
    return properties.getProperty(property);
  }
  
  public void setProperty(String key, String value) {
    this.properties.setProperty(key, value);
  }
  
}
