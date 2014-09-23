package org.eark.hdfs;

import org.eark.logging.BasicLogFormatter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

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

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @throws IOException 
     */
    public static HttpServer startServer() throws IOException {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.eark.hdfs package
        final ResourceConfig rc = new ResourceConfig().packages("org.eark.hdfs");
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, false);
        
        ServerConfiguration serverConfig = server.getServerConfiguration();
        
        NetworkListener nL = server.getListener(server.getListeners().iterator().next().getName()); //grizzly
        //disable server timeouts
        nL.setChunkingEnabled(true);
        nL.setDisableUploadTimeout(true);
        nL.getKeepAlive().setIdleTimeoutInSeconds(-1); //256
        //server.getServerConfiguration();

        server.start();
        return server;
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

	//Properties p = System.getProperties();
	//p.setProperty("derby.system.home", "C:\databases\sample");
	//TODO 
	//Test BasicLogFormatter here!
	try {
	    InputStream is = MyResource.class.getResourceAsStream("/logging.properties");	  
	    System.out.println("cl: "+LogManager.class.getClassLoader());
	    //.loadClass("org.eark.logging.BasicLogFormatter");
	    LogManager.getLogManager().readConfiguration(is);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	/*
	ConsoleHandler cH = new ConsoleHandler();
	cH.setFormatter(new BasicLogFormatter());
	Logger.getGlobal().addHandler(cH);
	System.out.println("handlers: "+Logger.getGlobal().getHandlers().length);
	Enumeration<String> en = LogManager.getLogManager().getLoggerNames();
	while(en.hasMoreElements()) {
	    System.out.println(en.nextElement());
	}
	*/

	
	//[0].setFormatter(new BasicLogFormatter());
	final HttpServer server = startServer();
	
                
        //ServerConfiguration serverConfig = server.getServerConfiguration().;
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }
}

