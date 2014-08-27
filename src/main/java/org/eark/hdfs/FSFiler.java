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
    	writeFile(fileInputStream, outputStream);
    	return file.getPath();
    }

}
