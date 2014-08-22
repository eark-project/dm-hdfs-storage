package org.eark.hdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    @Context
    private ServletContext application;
    
    public static String FSFILER = "fsFiler";
    public static String HDFSFILER = "hdfsFiler";
    public static String FS_BASE_PATH = "data";
    
    public static String FILER_TYPE = FSFILER;

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

    	System.out.println("putFile.ping(): "+fileName);    	
    	Filer filer = MyResource.getFiler();
    	String filePath = filer.writeFile(fileInputStream, fileName);    	
    	URI widgetId = new URI(filePath);
    	return Response.created(widgetId).build();
    	//return Response.created(createdUri).entity(Entity.text(createdContent)).build();
    }
    
    private static Filer getFiler() {
	if(FILER_TYPE.equals(FSFILER)) {
	    return new FSFiler(FS_BASE_PATH);
	} else {
	    return null;
	}
    }

}
