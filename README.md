#HSink - A REST service for transfering large files 

* Works with standard REST clients like *curl* (supporting HTTP/1.1)  
* A Java client is available through [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client)
* Supports the FileSystem and HDFS for storing data

##About
HSink is a simple HTTP service for storing (and retrieving) potentially large files on a remote file system or using the Hadoop Distributed File System (HDFS). The service is implemented using Apache Jersey 2.11 and uses an embedded Grizzly HttpServer if run in standalone mode. HSink utilizes HTTP chunked transfer encoding to support the transfer of large files. After uploading a file, the service returns a URL in the HTTP response which can be used to download the file again. The service can be called using standard HTTP clients like *curl* or *wget*, details are provided below. A Java client that invokes the service using the Jersey client API is [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client).

##Building the application
The application depends on the Maven projects [global-configuration](https://github.com/eark-project/global-configuration) and [dm-parent](https://github.com/eark-project/dm-parent). Both projects must be downloaded, built, and installed before HSink can be compiled.

```bash
## download the project
git clone <THE_PROJECT_URL>
## move to the project's main directory
## build an install the project
mvn clean install
```

HSink can be easily built using Maven. Please check Maven dependencies and versions in case the service is utilzed in combination with HDFS. 

```bash
git clone https://github.com/eark-project/dm-hdfs-storage.git
cd dm-hdfs-storage
mvn clean package
```

##Starting the service using embedded HTTP sever.
Hsink can be started in standalone mode based on an embedded Grizzely service using the command below. The application should output a text similar to: "Jersey app started with WADL available at http://localhost:8081/hsink/application.wadl".   

```bash
java -jar target/hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Please note that the Maven dependency scope for Hadoop related libraries is set to *provided* by default. If HSink is used in combination with HDFS, it is therefore required to either include the required libraries with the Jar-File by changing the dependency scope to *compile*, or to launch HSink's main class *org.eu.eark.hsink.Main* with a Java classpath pointing to the required libraries of an Hadoop installation, as shown below. (Please not that the service is started in the background):

```bash
  java -cp ./hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar:/usr/lib/hadoop/hadoop-common-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/log4j-1.2.17.jar:/usr/lib/hadoop/lib/commons-configuration-1.6.jar:/usr/lib/hadoop/hadoop-auth-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar:/usr/lib/hadoop/lib/slf4j-log4j12-1.6.1.jar:/usr/lib/hadoop-hdfs/hadoop-hdfs-2.0.0-cdh4.2.2.jar org.eu.eark.hsink.Main filer=hdfsFiler fs.default.name=hdfs://localhost:8020 BASE_URI=http://81.189.135.189:8081/hsink/ > grizzly.out 2>&1 &
```
##Starting the services using a Servlet container.
Jersey applications can be hosted in using different deployment methods and environments. For deploying hsink in a servlet container please consult the official Jersey documentation, section [4.7. Servlet-based Deployment](https://jersey.java.net/documentation/2.11/deployment.html#deployment.servle).

##Configuring the Service
The main configuration file for HSink is *config.properties* is residing under *src/main/resources/*. The files *commons-logging.properties* and *logging.properties configuration* configure the logging granularity used by HSink and libraries it depends on. All configuration files are packaged with the application when creating a Jar file.

In order to configure HSink one can either overwrite the default properties by passing new key-value pairs as command-line parameters or change the file *config.properties* and re-package the service in order persist the configuration. The main configuration parameters are:

| *property-name* | *default value* | *meaning*  |
|-----------------|:---------------:|-----------:|  
| filer           | fsFiler         | Switch for configuring target file system. Accepted values are *fsFiler* or *hdfsFiler*. |
| BASE_URI        | http://localhost:8081/hsink | Default Address the service is bound to. This must be changed if a service should be accessible from a location different then localhost. |
| FS_BASE_PATH    | data            | A relative path which serves as the basis for storing data on the service. The directory is either relative to the location where the service has been started using fsFiler, or relative to the HDFS home directory of the user starting the service using hdfsFiler mode. | 
| fs.default.name | hdfs://localhost:8020 | URI on HDFS Namenode to accept file-system requests. Typical values are *hdfs://localhost:8020* or *hdfs://localhost:9000*. |
| core-site       | optional property | Path to Hadoop core-site.xml configuration file. Not required of HDFS URL is correctly configured. |
| hdfs-site       | optional property | Path to Hadoop hdfs-site.xml configuration file. Not required of HDFS URL is correctly configured. |

Overwriting config properties using the command-line, for example FS_BASE_PATH:
```bash
java -jar target/hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar FS_BASE_PATH=uploads
```

##Using the Service with curl
HSink makes use of chunked transfer encoding allowing a client to transmit large files using HTTP. The data transfer mechanism makes use of the Transfer-Encoding HTTP header and therefore requires a client that supports HTTP/1.1 like *curl*. 

Example curl request:
```bash
curl -v -X PUT -H "Content-Type:application/octet-stream" -H "Transfer-Encoding: chunked" http://localhost:8081/hsink/fileresource/files/cmd.txt -T ./cmd.txt
```

The curl example uploads a file called *./cmd.txt* to HSink. The specified remote filename is also *./cmd.txt*. Once the file has been received by the HSink, the service responds with an URL of the generated resource which includes a generated identifier for the upload, as shown below. The generated URL can be used to download the file.     

Generated HTTP request:
```bash
PUT /hsink/fileresource/files/cmd.txt HTTP/1.1
User-Agent: curl/7.35.0
Host: localhost:8081
Accept: */*
Content-Type:application/octet-stream
Transfer-Encoding: chunked
Expect: 100-continue
```

Received HTTP response:
```bash
HTTP/1.1 100 Continue
HTTP/1.1 201 Created
Date: Tue, 30 Jun 2015 08:52:27 GMT
Location: http://localhost:8081/hsink/fileresource/files/2.2015-06-30/cmd.txt
Content-Length: 0
```

##Using the Service from Java
A Java client for HSink that makes use of the Jersey client API is available through [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client). 

```bash
usage: java -jar JARFILE [options] [source URI] [target URI]
...file upload:   java -jar client.jar -u ./file [http://localhost:8081/hsink]
...file download: java -jar client.jar -d http://localhost:8081/hsink/.../file ./file
...roundtrip test: java -jar client.jar -t ./file [http://localhost:8081/hsink]
```

##Configuring Apache ReverseProxy
TODO



##TODOs (E-ARK Project)
- Data integrity during upload - AIP checksum?
- Give unique ID to new AIP
- Store with unique ID into Lily (or is the ID the Lily ID?)
