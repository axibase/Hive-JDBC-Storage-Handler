#Hive Storage Handler for JDBC#

The **Hive Storage Handler For JDBC** by [Axibase](www.axibase.com), which is a fork of [HiveJdbcStorageHandler](https://github.com/qubole/Hive-JDBC-Storage-Handler), helps users read from [Axibase Time Series Database](http://axibase.com/products/axibase-time-series-database/) using Hive, and also enabling them to run SQL queries to analyze data that resides in JDBC tables.


##Building from Source##
* Download the code from Github:
```
  $ git clone https://github.com/axibase/Hive-JDBC-storage-Handler.git
  $ cd Hive-JDBC-storage-Handler
```

* Build using Maven (add ```-DskipTests``` to build without running tests):

```
  $ mvn clean install -Phadoop-1
```

* The JARs for the storage handler can be found in the ```target/``` folder. Use ```qubole-hive-JDBC-0.0.4.jar``` in the hive session (see below).

##Usage##
* Add the JAR to the Hive session. ```<path-to-jar>``` is the path to the above mentioned JAR. For using this with Qubole hive, upload the JAR to an S3 bucket and provide its path.
  
``` 
  ADD JAR <path-to-jar>;
```

* Each record in the JDBC corresponds to a row in the Hive table.

* While creating the Hive table, use 
  
```
  STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
```
  
##Table Creation##

```
CREATE EXTERNAL TABLE HiveTable
row format serde 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcSerDe'
STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
TBLPROPERTIES (
  "mapred.jdbc.driver.class"="com.mysql.jdbc.Driver",
  "mapred.jdbc.url"="jdbc:mysql://localhost:3306/rstore",
  "mapred.jdbc.username"="root",
  "mapred.jdbc.input.table.name"="JDBCTable",
  "mapred.jdbc.output.table.name" = "JDBCTable",
  "mapred.jdbc.password"="",
  "mapred.jdbc.hive.lazy.split"= "false"
);
```


####Queries to Read from ATSD ####
```sql
hive> SELECT * 
          FROM disk_used 
        LIMIT 1;
nurswgvml006	2016-02-05 15:43:01	0.0	tmpfs	/dev/shm

hive> SELECT value, `tags$mount_point`, datetime  
          FROM disk_used 
        WHERE entity = 'nurswgvml301' 
          LIMIT 1;
704.0	/run	2016-08-25 10:43:26

hive> SELECT value, datetime 
          FROM disk_used 
        WHERE entity = 'nurswgvml301' AND `tags$mount_point`='/dev' 
          LIMIT 1;
4.0	2016-08-25 10:43:26

hive> SELECT value, `tags$mount_point`, `tags$file_system` 
          FROM disk_used 
        WHERE entity = 'nurswgvml301' AND datetime > '2016-08-24T19:00:00.000Z' 
          LIMIT 10;
704.0	/run	tmpfs
704.0	/run	tmpfs
704.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs

hive> SELECT value, `tags$mount_point`, `tags$file_system` 
          FROM disk_used 
        WHERE entity = 'nurswgvml301' 
          AND datetime > '2016-08-24T23:00:00.000Z' AND datetime <= '2016-08-25T10:45:00.000Z' sort by value desc;
1316576.0	/	/dev/sda1
1316576.0	/	/dev/sda1
1315536.0	/	/dev/sda1
704.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
700.0	/run	tmpfs
4.0	/dev	udev
4.0	/dev	udev
4.0	/dev	udev
4.0	/dev	udev
0.0	/sys/fs/cgroup	none
0.0	/sys/fs/cgroup	none
0.0	/run/user	none
0.0	/run/user	none
...
0.0	/run/shm	none
0.0	/run/shm	none
0.0	/run/shm	none
0.0	/run/lock	none
0.0	/run/lock	none
0.0	/run/lock	none
```

##Contributions##
* https://github.com/myui/HiveJdbcStorageHandler
* https://github.com/hava101
* https://github.com/stagraqubole
* https://github.com/divyanshu25
* https://github.com/qubole/Hive-JDBC-Storage-Handler