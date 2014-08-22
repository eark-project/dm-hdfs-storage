package org.eark.hdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FSFiler extends Filer {

    public FSFiler(String fsBasePath) {
	super(fsBasePath);
    }
    
    @Override
    public String writeFile(InputStream fileInputStream, String fileName) throws IOException {
	
	String path = fsBasePath+File.separator+fileName;
    	File file = new File("path");
    	if (!file.exists()) {
    	    file.createNewFile();
    	}

    	OutputStream outputStream = new FileOutputStream(path);
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
    	return file.getPath();
    }

}
