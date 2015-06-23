package org.eu.eark.hsink;

import org.eu.eark.hsink.logging.BasicLogFormatter;
import org.eu.eark.hsink.properties.ConfigProperties;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * Main class for starting Grizzly HTTP server and exposing JAX-RS resources.
 */
public class Main {
  
  private final static Logger LOG = Logger.getLogger(Main.class.getName());
  
  public static String BASE_URI = "http://localhost:8081/hsink/";

  public static HttpServer startServer() throws IOException {
    
    String baseUri = ConfigProperties.getInstance().getProperty("BASE_URI");
    if(baseUri != null) {
      BASE_URI = baseUri; 
      LOG.info("base uri set to: "+baseUri);
    }
    final ResourceConfig rc = new ResourceConfig().packages("org.eu.eark.hsink");

    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
        URI.create(BASE_URI), rc, false);

    ServerConfiguration serverConfig = server.getServerConfiguration();

    NetworkListener nL = server.getListener(server.getListeners().iterator()
        .next().getName()); // grizzly
    // disable server timeouts
    nL.setChunkingEnabled(true);
    nL.setDisableUploadTimeout(true);
    nL.getKeepAlive().setIdleTimeoutInSeconds(-1); // 256
    // server.getServerConfiguration();

    server.start();
    return server;
  }

  public static void main(String[] args) throws IOException {

    try {
      InputStream is = FileResource.class
          .getResourceAsStream("/logging.properties");
      System.out.println("cl: " + LogManager.class.getClassLoader());
      // .loadClass("org.eark.logging.BasicLogFormatter");
      LogManager.getLogManager().readConfiguration(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    for(String arg : args) {
      String[] keyValue = arg.split("=");
      if(keyValue.length == 2) {
        ConfigProperties.getInstance().setProperty(keyValue[0], keyValue[1]);
        LOG.fine("Setting or overwriting config property: "+arg);
      } else {
        LOG.fine("Invalid config property (key=value). Ignoring: "+arg);
      }
    }

    /*
     * ConsoleHandler cH = new ConsoleHandler(); cH.setFormatter(new
     * BasicLogFormatter()); Logger.getGlobal().addHandler(cH);
     * System.out.println("handlers: "+Logger.getGlobal().getHandlers().length);
     * Enumeration<String> en = LogManager.getLogManager().getLoggerNames();
     * while(en.hasMoreElements()) { System.out.println(en.nextElement()); }
     */
    // [0].setFormatter(new BasicLogFormatter());
    
    HttpServer server = null;
    
    try {
	    server = startServer();
	    
	    Enumeration<String> en = LogManager.getLogManager().getLoggerNames();
	    while(en.hasMoreElements()) { 
	      String loggerName = en.nextElement();
	      //suppress logging of EOFException at fine level
	      if(loggerName.equals("org.glassfish.grizzly.nio.transport.TCPNIOTransport") ||
	         loggerName.equals("org.glassfish.grizzly.filterchain.DefaultFilterChain")) {
	        Logger logger = LogManager.getLogManager().getLogger(loggerName);
	        logger.setLevel(Level.INFO);
	        System.out.println(loggerName+" ... level set to INFO");
	      } else {
	        //System.out.println(loggerName);
	      }
	    }
     
	    // ServerConfiguration serverConfig = server.getServerConfiguration().;
	    //System.out.println(String.format(
	    //    "Jersey app started with WADL available at "
	    //        + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
	
	    System.out.println(String.format(
	        "Jersey app started with WADL available at "
	            + "%sapplication.wadl", BASE_URI));
	    
    //System.in.read();
    //server.shutdown();
    
	    Thread.currentThread().join();
    } catch (Exception ioe) {
    	System.err.println(ioe);
    } finally {
    	try {
    		server.shutdown();
    	} catch (Exception ioe) {
    		System.err.println(ioe);
    	}
    }
  }
}
