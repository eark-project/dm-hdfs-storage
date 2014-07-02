dm-hdfs-storage
===============

eArk WP6 - reference implementation: bulk load into HDFS

TODOs
------
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
