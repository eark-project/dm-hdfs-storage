package org.eark.hdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
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

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

  @Context
  private ServletContext context;
    
  public static String FSFILER = "fsFiler";
  public static String HDFSFILER = "hdfsFiler";
  public static String FS_BASE_PATH = "data";
   
  public String filerType = null;
  private Properties props = null;
    
    
 
  private final static Logger LOG = Logger.getLogger(MyResource.class.getName());
 
  @PostConstruct
  public void init() {
    LOG.log(Level.INFO, "MyResource initialized");
    filerType = HDFSFILER;
    Properties props = new Properties();
    try {
	    props.load(MyResource.class.getResourceAsStream("/config.properties"));
    } catch (IOException e) {
	    LOG.log(Level.WARNING, "No config.properties file found. Setting filerType to HDFSFILER");
    } 
    String property = props.getProperty("filer");
    if(property == null || (!property.equals(FSFILER) && !property.equals(HDFSFILER)) ) {
	    LOG.log(Level.WARNING, "No filer property found. Setting filerType to HDFSFILER");
    } else {
	    filerType = property;
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
  @Path("/upload/{fileName}")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  public Response putFile(@Context HttpServletRequest request,
                          @PathParam("fileName") String fileName,
                          InputStream fileInputStream) 
                              throws Throwable
  {

    try {
	    LOG.log(Level.INFO, "putFile: "+fileName);    	
	    Filer filer = this.getFiler();
	    String filePath = filer.writeFile(fileInputStream, fileName);    	
	    URI widgetId = new URI(filePath);
	    return Response.created(widgetId).build();
	    //TODO return the ID<Long>
	    //return Response.created(createdUri).entity(Entity.text(createdContent)).build();
    } catch(Exception ex) {
	    LOG.log(Level.INFO, "Error while uploading file "+fileName, ex);
      throw new WebApplicationException(ex);
    }  
  }
    
  
  @GET
  @Path("/files/{fileName}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public /*Response*/StreamingOutput getFile(@PathParam("fileName") final String fileName)  throws Exception  {
    
    LOG.log(Level.INFO, "getFile(): "+fileName);
    final Filer filer = this.getFiler();
	
    StreamingOutput stream = new StreamingOutput() {
      public void write(OutputStream output) throws IOException, WebApplicationException {
      try {
        filer.write(output, fileName);
        //wb.write(output);
        } catch (Exception ex) {
          LOG.log(Level.INFO, "Error while downloading file "+fileName, ex);
          throw new WebApplicationException(ex);
        }
      }
    };
    //return Response.ok(stream).header("content-disposition","attachment; filename = "+fileName).build();
    return stream;
  }
    
    
  private Filer getFiler() throws IOException, URISyntaxException {
    if(filerType.equals(FSFILER)) {
      return new FSFiler(FS_BASE_PATH);
    } else if(filerType.equals(HDFSFILER)) {
      Filer f = new HDFSFiler(FS_BASE_PATH);
      return f;
    } else {
      return null;
    }	
  }
}
