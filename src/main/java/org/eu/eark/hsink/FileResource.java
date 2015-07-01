package org.eu.eark.hsink;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;

import org.apache.hadoop.conf.Configuration;
import org.eu.eark.hsink.naming.FileTree;
import org.eu.eark.hsink.properties.ConfigProperties;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("fileresource")
public class FileResource {

  @Context
  private ServletContext context;
    
  public final static String FSFILER = "fsFiler";
  public final static String HDFSFILER = "hdfsFiler";
  public final static String FS_BASE_PATH = "data";  
  public final static String WEB_BASE_PATH = "files";
   
  public static String fsBasePath;  
  public String filerType = null;
  private Properties props = null;
    
    
 
  private final static Logger LOG = Logger.getLogger(FileResource.class.getName());
 
  @PostConstruct
  public void init() {
    LOG.log(Level.INFO, "FileResource initialized");
    filerType = HDFSFILER;
    fsBasePath = FS_BASE_PATH;  

    String property = ConfigProperties.getInstance().getProperty("filer");
    if(property == null || (!property.equals(FSFILER) && !property.equals(HDFSFILER)) ) {
	    LOG.log(Level.WARNING, "No filer property found. Setting filerType to HDFSFILER");
    } else {
	    filerType = property;
	    LOG.log(Level.INFO, "Filer set to "+filerType);
    }
    property = ConfigProperties.getInstance().getProperty("FS_BASE_PATH");
    if(property == null || property.equals("")) {
	    LOG.log(Level.WARNING, "No FS_BASE_PATH property found. Setting relative path to: "+fsBasePath);
    } else {
	    fsBasePath = property;
	    LOG.log(Level.INFO, "Filer set to "+filerType);
    }
  }

  /**
   * Method handling HTTP GET requests. The returned object will be sent
   * to the client as "text/plain" media type.
   *
   * @return String that will be returned as a text/plain response.
   */
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getIt() {
     return "Got it!";
   }
    
    
  //@Consumes("application/octet-stream")
  //@POST
  @PUT
  @Path("/files/{fileName}")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public Response putFile(@Context HttpServletRequest request,
                          @PathParam("fileName") String fileName,
                          InputStream fileInputStream) 
                              throws Throwable
  {

    try {
	    LOG.log(Level.INFO, "putFile: "+fileName);    	
      URI resourcePath = UriBuilder.fromResource(FileResource.class).build();
      LOG.fine("resourcePath: "+resourcePath);
	    Filer filer = this.getFiler();
	    String dirName = FileTree.getInstance(filer).nextDirName();
	    LOG.log(Level.FINE, "directory id: "+dirName);
	    String filePath = filer.writeFile(fileInputStream, fileName, dirName);
	    LOG.log(Level.FINE, "filePath: "+filePath);
	    try {
	    	sendMessage(fsBasePath + filePath);
	    } catch (Exception ex) {
	    	LOG.log(Level.WARNING, "Can't send message", ex);
	    }
	    URI widgetId = new URI(resourcePath.toString()+'/'+this.WEB_BASE_PATH+'/'+filePath);
	    LOG.log(Level.FINE, "putFile: "+widgetId.toASCIIString()+" done");
	    return Response.created(widgetId).build();
	    //TODO return the ID<Long>
	    //return Response.created(createdUri).entity(Entity.text(createdContent)).build();
    } catch(Exception ex) {
	    LOG.log(Level.INFO, "Error while uploading file "+fileName, ex);
      throw new WebApplicationException(ex);
    }  
  }
    
  
	private void sendMessage(String filePath) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		byte[] messageBodyBytes = "files".getBytes();
		channel.basicPublish("", "file-ingest", null, messageBodyBytes);
		
		channel.close();
		connection.close();
	}

@GET
  @Path("/files/{fileName}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  
  public /*Response*/StreamingOutput getFile(@PathParam("fileName") final String fileName)  throws Exception  {
    
    //TODO propagate errors back to client!
    LOG.log(Level.INFO, "getFile(): "+fileName);
    final Filer filer = this.getFiler();
    LOG.log(Level.FINE, "getFile(): after filer initialization");
	
    StreamingOutput stream = new StreamingOutput() {
      public void write(OutputStream output) throws IOException, WebApplicationException {
      try {
        LOG.log(Level.FINE, "streamingOutput.write() called");
        LOG.log(Level.FINE, "streamingOutput.write() witing to outputStream: "+output);
        filer.writeStream(output, fileName);
        } catch (Exception ex) {
          LOG.log(Level.INFO, "Error while downloading file "+fileName, ex);
          throw new WebApplicationException(ex);
        }
      }
    };
    
    return stream;
  }
  
  @GET
  @Path("/files/{pathName}/{fileName}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  
  public /*Response*/StreamingOutput getFile(@PathParam("pathName") final String pathName, @PathParam("fileName") final String fileName)  throws Exception  {
    
    //TODO propagate errors back to client!
    LOG.log(Level.INFO, "getFile(): "+pathName+"/"+fileName);
    return this.getFile(pathName+"/"+fileName);
  }
    
    
  private Filer getFiler() throws IOException, URISyntaxException {
    if(filerType.equals(FSFILER)) {
      return new FSFiler(fsBasePath);
    } else if(filerType.equals(HDFSFILER)) {
      Filer f = new HDFSFiler(fsBasePath);
      return f;
    } else {
      return null;
    }	
  }
}
