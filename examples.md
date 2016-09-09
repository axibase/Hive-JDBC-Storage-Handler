## Example Categories

* [Tables for metrics](#jdbcstoragehandler)
    * [Hive table for metric without tags](#hive-table-for-metric-without-tags)
    * [Hive table for metric with tags](#hive-table-for-metric-with-tags)
    * [Hive table for all metrics](#hive-table-for-atsd_series)
* [Tables for HBase](#hbasestoragehandler)
    * [Hive table for atsd_entity](#hive-table-for-atsd_entity)
    * [Hive table for atsd_metric](#hive-table-for-atsd_metric)
    * [Hive table for atsd_entity_group](#hive-table-for-atsd_entity_group)
    * [Hive table for atsd_entity_lookup](#hive-table-for-atsd_entity_lookup)
    * [Hive table for atsd_properties](#hive-table-for-atsd_properties)

##JdbcStorageHandler

###Hive table for metric without tags

```sql
hive> CREATE EXTERNAL TABLE cpu_busy
      row format serde 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcSerDe'
      STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
      TBLPROPERTIES (
        "mapred.jdbc.driver.class"="com.axibase.tsd.driver.jdbc.AtsdDriver",
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://10.102.0.6:8443/api/sql\;trustServerCertificate=true\;strategy=file",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.input.table.name"="'mpstat.cpu_busy'",
        "mapred.jdbc.output.table.name"="'mpstat.cpu_busy'",
        "mapred.jdbc.hive.lazy.split"= "true"
      );

hive> DESCRIBE cpu_busy;
entity              	string              	from deserializer   
datetime            	timestamp           	from deserializer   
value               	float               	from deserializer   
```

####Usage
```sql
hive> SELECT * 
          FROM cpu_busy 
        LIMIT 1;
nurswgvml007	2014-09-25 13:58:04	10.31

hive> SELECT value, datetime 
          FROM cpu_busy 
        WHERE entity = 'nurswgvml212' 
          LIMIT 1;
100.0	2015-10-20 11:36:05

hive> SELECT value, datetime 
          FROM cpu_busy 
        WHERE entity = 'nurswgvml212' AND datetime > '2015-10-19T19:00:00.000Z' 
          LIMIT 10;
100.0	2015-10-20 11:36:05
100.0	2015-10-20 11:36:21
100.0	2015-10-20 11:36:37
100.0	2015-10-20 11:36:53
100.0	2015-10-20 11:37:09
100.0	2015-10-20 11:37:25
100.0	2015-10-20 11:37:41
100.0	2015-10-20 11:37:57
100.0	2015-10-20 11:38:07
100.0	2015-10-20 11:38:13

hive> SELECT value, datetime 
          FROM cpu_busy 
        WHERE entity = 'nurswgvml212' 
          AND datetime > '2015-10-19T19:00:00.000Z' AND datetime <= '2015-10-20T19:00:00.000Z';
100.0	2015-10-20 11:36:05
100.0	2015-10-20 11:36:21
100.0	2015-10-20 11:36:37
...
5.05	2015-10-20 12:10:30
49.5	2015-10-20 12:10:46
100.0	2015-10-20 12:11:02
100.0	2015-10-20 12:11:18
100.0	2015-10-20 12:11:34
3.96	2015-10-20 12:11:50
3.03	2015-10-20 12:12:06
5.0	2015-10-20 12:12:22
100.0	2015-10-20 12:12:38
100.0	2015-10-20 12:12:54
100.0	2015-10-20 12:13:10
100.0	2015-10-20 12:13:26
```

###Hive table for metric with tags

```sql
hive> CREATE EXTERNAL TABLE disk_used
      row format serde 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcSerDe'
      STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
      TBLPROPERTIES (
        "mapred.jdbc.driver.class"="com.axibase.tsd.driver.jdbc.AtsdDriver",
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://10.102.0.6:8443/api/sql\;trustServerCertificate=true\;strategy=file",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.input.table.name"="'df.disk_used'",
        "mapred.jdbc.output.table.name"="'df.disk_used'",
        "mapred.jdbc.hive.lazy.split"= "true"
      );
hive> DESCRIBE disk_used;
entity              	string              	from deserializer   
datetime            	timestamp           	from deserializer   
value               	float               	from deserializer   
tags$file_system    	string              	from deserializer   
tags$mount_point    	string              	from deserializer   
```

####Usage
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

###Hive table for atsd_series

```sql
hive> CREATE EXTERNAL TABLE atsd_series(
          entity string,
          metric string,
          value float,
          tags string,
          time bigint,
          datetime timestamp,
          `entity$tags` string,
          `metric$tags` string,
          `entity$groups` string
      )
      STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
      TBLPROPERTIES (
        "mapred.jdbc.driver.class"="com.axibase.tsd.driver.jdbc.AtsdDriver",
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://10.102.0.6:8443/api/sql\;trustServerCertificate=true\;strategy=file",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.input.table.name"="atsd_series",
        "mapred.jdbc.output.table.name"="atsd_series",
        "mapred.jdbc.hive.lazy.split"= "true"
      );
hive> DESCRIBE atsd_series;
entity              	string              	from deserializer   
metric              	string              	from deserializer   
value               	float               	from deserializer   
tags                	string              	from deserializer   
time                	bigint              	from deserializer   
datetime            	timestamp           	from deserializer   
entity$tags         	string              	from deserializer   
metric$tags         	string              	from deserializer   
entity$groups       	string              	from deserializer   
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_series 
        WHERE metric = 'df.disk_used' 
          LIMIT 1;
nurswgvml006	df.disk_used	0.0	file_system=tmpfs;mount_point=/dev/shm	1454686981000	2016-02-05 15:43:01	
app=Hadoop/HBASE;ip=10.102.0.5;loc_area=dc1;os=Linux		
java-loggers;nmon-linux;nmon-linux-beta;nmon-sub-group;nur-collectors;scollector-linux;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs

hive> SELECT * 
          FROM atsd_series 
        WHERE metric = 'df.disk_used' AND entity = 'nurswgvml301' 
          AND datetime > '2016-08-24T23:00:00.000Z' AND datetime <= '2016-08-25T10:45:00.000Z' 
        LIMIT 1;
nurswgvml301	df.disk_used	704.0	file_system=tmpfs;mount_point=/run	1472121806000	2016-08-25 10:43:26			
nmon-linux;nmon-linux-beta;nur-collectors

hive> SELECT * 
          FROM atsd_series 
        WHERE metric = 'mpstat.cpu_busy' 
          LIMIT 1;
nurswgvml007	mpstat.cpu_busy	10.31		1411653484000	2014-09-25 13:58:04	
alias=007;app=ATSD;environment=prod;ip=10.102.0.6;loc_area=dc1;loc_code=nur,nur;os=Linux		
java-loggers;java-virtual-machine;jetty-web-server;nmon-linux;nmon-linux-beta;nmon-sub-group;nur-collectors;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs

hive> SELECT * 
          FROM atsd_series 
        WHERE metric = 'mpstat.cpu_busy' AND entity = 'nurswgvml212' 
          AND datetime > '2015-10-19T19:00:00.000Z' AND datetime <= '2015-10-20T19:00:00.000Z' 
        LIMIT 1;
nurswgvml212	mpstat.cpu_busy	100.0		1445340965000	2015-10-20 11:36:05	
app=ITM;ip=10.102.0.32;loc_area=dc2;os=Red Hat Enterprise Linux Server 6.4		
nmon-linux;nmon-linux-beta;scollector-linux;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs
```


##HBaseStorageHandler

###Hive table for atsd_entity

```
hive> CREATE EXTERNAL TABLE atsd_entity(row_key string, tags map<string,string>, disabled boolean, id string)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,t:,n:disabled, n:id",
"hbase.table.default.storage.type" = "binary"
)
TBLPROPERTIES("hbase.table.name" = "atsd_entity");
hive> describe atsd_entity;
row_key             	string              	from deserializer   
tags                	map<string,string>  	from deserializer   
disabled            	boolean             	from deserializer   
id                  	string              	from deserializer 
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_entity 
        LIMIT 1;
atsd	{"os":"ubuntu"}	true	9        
```

###Hive table for atsd_metric

```sql
hive> CREATE EXTERNAL TABLE atsd_metric(row_key string, tags map<string,string>, disabled boolean, id string)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,t:,n:disabled, n:id",
"hbase.table.default.storage.type" = "binary"
)
TBLPROPERTIES("hbase.table.name" = "atsd_metric");
hive> describe atsd_metric
row_key             	string              	from deserializer   
tags                	map<string,string>  	from deserializer   
disabled            	boolean             	from deserializer   
id                  	string              	from deserializer  
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_metric 
        LIMIT 1;
cpu.busy	{"table":"common"}	false   �
```

###Hive table for atsd_entity_group

```sql
hive> CREATE EXTERNAL TABLE atsd_entity_group(row_key string, e map<string,string>,  g map<string,string>)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,e:,g:",
"hbase.table.default.storage.type" = "binary"
)
TBLPROPERTIES("hbase.table.name" = "atsd_entity_group");
hive> describe atsd_entity_group;
row_key             	string              	from deserializer   
e                   	map<string,string>  	from deserializer   
g                   	map<string,string>  	from deserializer
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_entity_group 
        LIMIT 1;
cadvisor-hosts	{}	{"__expression__":"tags.container_host = 'true'","__portals_new__":"8,9,10,11"}
```

###Hive table for atsd_entity_lookup

```sql
hive> CREATE EXTERNAL TABLE atsd_entity_lookup(row_key string, c map<string,string>)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,c:",
"hbase.table.default.storage.type" = "binary"
)
TBLPROPERTIES("hbase.table.name" = "atsd_entity_lookup");
hive> describe atsd_entity_lookup;
row_key             	string              	from deserializer   
c                   	map<string,string>  	from deserializer   
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_entity_lookup 
        LIMIT 1;
replacement_table	{"_":"A=C\nB=e\nb=f\nD=Z"}
```

###Hive table for atsd_properties

```sql
hive> CREATE EXTERNAL TABLE atsd_properties(row_key string, c map<string,string>)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
"hbase.columns.mapping" = ":key,c:",
"hbase.table.default.storage.type" = "binary"
)
TBLPROPERTIES("hbase.table.name" = "atsd_properties");
hive> describe atsd_properties;
row_key             	string              	from deserializer   
c                   	map<string,string>  	from deserializer 
```

####Usage
```sql
hive> SELECT * 
          FROM atsd_properties 
        LIMIT 1;
"java.log_aggregator.operating_system":"atsd-distributed":"command":"com.axibase.tsd.Server"	{"arch":"amd64",
"availableprocessors":"12","maxfiledescriptorcount":"1048576","name":"Linux","totalphysicalmemorysize":"66954264576",
"totalswapspacesize":"34342825984","version":"3.13.0-83-generic"}        
```

