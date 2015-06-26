#HSink - A REST service for transfing large files 

* Supports standard REST clients like *curl* or *wget* 
* A Java client is available by [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client)
* Supports the FileSystem and HDFS for storing the received files

##About
HSINk is a simple HTTP service for storing (and retrieving) potentially large files on a remote file system or using the Hadoop Distributed File Sytem (HDFS). The service is implemented using Apache Jersey 2.11 and uses an embedded Grizzly HttpServer if run in standalone mode. HSink utilizes HTTP chunked transfer encoding to support the transfer of large files. After uploading a file, the service returns a URL in the HTTP response which can be used to download the file again. The service can be called using standard HTTP clients like *curl* or *wget*, details are provided below. A Java client that invokes the service using the Jersey client API is [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client).

##Starting the services using a Servlet container.
Jersey applications can be hosted in using different deployment methods and environments. For deploying hsink in a servlet container please consult the official Jersey documentation, section [4.7. Servlet-based Deployment](https://jersey.java.net/documentation/2.11/deployment.html#deployment.servle).

##Building the application
The application depends on ...
The service can be easily built using Maven.
```bash
mvn clean package
```
Please note that dependencies on are are set to.... 

##Starting the service using embedded HTTP sever.
Hsink can be started in standalone mode using the embedded Grizzely service using the command below.  
```bash
java -jar target/hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar
```
Whith references to Hadoop installation:
```bash
  java -cp ./hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar:/usr/lib/hadoop/hadoop-common-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/log4j-1.2.17.jar:/usr/lib/hadoop/lib/commons-configuration-1.6.jar:/usr/lib/hadoop/hadoop-auth-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar:/usr/lib/hadoop/lib/slf4j-log4j12-1.6.1.jar:/usr/lib/hadoop-hdfs/hadoop-hdfs-2.0.0-cdh4.2.2.jar org.eu.eark.hsink.Main filer=hdfsFiler fs.default.name=hdfs://localhost:8020 BASE_URI=http://81.189.135.189:8081/hsink/ > grizzly.out 2>&1 &
```

TODOs
-----
- use embedded jetty woth jaxrs/jersey or resteasy to create REST service
- single JAR which can be run "-jar"
- communicate needs with ESS
- upload raw data to HDFS
- maybe create entry in Lily on AIP level (just 1 entry for whole package)
- in the end notify callback EPP
- unpack payload and put into hdfs also
  - maybe extract content from aip and put into
- maybe create folder watcher that iuploads file arriving in ftp folder
- test integration with EPP
- experiment about 2G boundary when using HTTP put/post - can we upload hige files?
- data integrity during upload - AIP checksum? -> check checksum
- give unique ID to new AIP
- store with unique ID into Lily (or is the ID the Lily ID?)
