#HSINK - A REST service for transfing large files 

* Supports standard REST clients like *curl* or *wget* 
* A Java Client is available by [dm-hdfs-storage-client](https://github.com/eark-project/dm-hdfs-storage-client)
* Supports the FileSystem and HDFS for storing the received files

##About
HSINk is a 

eArk WP6 - reference implementation: bulk load into HDFS

A REST service to dump data onto HDFS.

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
