#HSink - A REST service for transfering large files 

* Supports standard REST clients like *curl* or *wget* 
* A Java client is available by [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client)
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
Hsink can be started in standalone mode using the embedded Grizzely service using the command below. The application should output a text similar to: "Jersey app started with WADL available at http://localhost:8081/hsink/application.wadl".   

```bash
java -jar target/hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Please note that the Maven dependency scope for Hadoop related libraries is set to *provided* by default. If HSink is used in combination with HDFS, it is therefore required to either include the required libraries with the Jar-File by changing the dependency scope to *compile*, or to launch HSink's main class *org.eu.eark.hsink.Main* with a Java classpath pointing to the required libraries of an Hadoop installation, as shown below:

```bash
  java -cp ./hsink-service-1.0-SNAPSHOT-jar-with-dependencies.jar:/usr/lib/hadoop/hadoop-common-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/log4j-1.2.17.jar:/usr/lib/hadoop/lib/commons-configuration-1.6.jar:/usr/lib/hadoop/hadoop-auth-2.0.0-cdh4.2.2.jar:/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar:/usr/lib/hadoop/lib/slf4j-log4j12-1.6.1.jar:/usr/lib/hadoop-hdfs/hadoop-hdfs-2.0.0-cdh4.2.2.jar org.eu.eark.hsink.Main filer=hdfsFiler fs.default.name=hdfs://localhost:8020 BASE_URI=http://81.189.135.189:8081/hsink/ > grizzly.out 2>&1 &
```
##Starting the services using a Servlet container.
Jersey applications can be hosted in using different deployment methods and environments. For deploying hsink in a servlet container please consult the official Jersey documentation, section [4.7. Servlet-based Deployment](https://jersey.java.net/documentation/2.11/deployment.html#deployment.servle).

##Configuring the Service


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
