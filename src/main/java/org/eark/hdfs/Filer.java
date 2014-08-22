package org.eark.hdfs;

import java.io.IOException;
import java.io.InputStream;

public abstract class Filer {
    
    protected String fsBasePath;
    
    public Filer() {
    }
    
    
    public Filer(String fsBasePath) {
	super();
	this.fsBasePath = fsBasePath;
    }

    public abstract String writeFile(InputStream fileInputStream, String fileName) throws IOException;
    
    //public abstract int getMD5Sum(String path);

}
