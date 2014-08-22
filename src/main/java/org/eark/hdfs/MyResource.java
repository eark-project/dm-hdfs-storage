package org.eark.hdfs;

import java.io.InputStream;
import java.net.URI;

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
    
    //@PUT
    //@Consumes("application/octet-stream")
    @POST
    @Path("/upload/{fileName}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response putFile(@Context HttpServletRequest a_request,
                            @PathParam("fileName") String fileName,
                            InputStream a_fileInputStream
    												) throws Throwable
    {
    	System.out.println("putFile.ping(): "+fileName);
    	URI widgetId = new URI(fileName);
    	return Response.created(widgetId).build();
    	//return Response.created(createdUri).entity(Entity.text(createdContent)).build();
    }

}
