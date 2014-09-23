package org.eark.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Filer {
    
    protected String fsBasePath;
    
    public Filer() {
    }
        
    public Filer(String fsBasePath) {
	this.fsBasePath = fsBasePath;
    }

    public abstract String writeFile(InputStream fileInputStream, String fileName) throws IOException;
    
    public void writeFile(InputStream fileInputStream, OutputStream outputStream) throws IOException {
	try {
    	    byte[] buffer = new byte[1024];
    	    int bytesRead;

    	    while((bytesRead = fileInputStream.read(buffer)) !=-1) {
    		outputStream.write(buffer, 0, bytesRead);
    	    }
    	    fileInputStream.close();
    	    outputStream.flush();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} finally {
    	    outputStream.close();
    	}
    }
    
    public abstract void write(OutputStream outputStream, String fileName) throws IOException;

    
    //public abstract int getMD5Sum(String path);

}
