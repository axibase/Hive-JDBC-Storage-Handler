```sql
hive> ADD JAR /home/axibase/atsd-jdbc-1.2.10-SNAPSHOT-DEPS.jar;
hive> ADD JAR /home/axibase/qubole-hive-JDBC-0.0.6.jar;

hive> CREATE EXTERNAL TABLE cpu_busy
      row format serde 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcSerDe'
      STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
      TBLPROPERTIES (
        "mapred.jdbc.driver.class"="com.axibase.tsd.driver.jdbc.AtsdDriver",
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://nur.axibase.com/api/sql",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.input.table.name"="'mpstat.cpu_busy'",
        "mapred.jdbc.output.table.name"="'mpstat.cpu_busy'",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.hive.lazy.split"= "true"
      );

hive> describe cpu_busy;
entity              	string              	from deserializer   
datetime            	timestamp           	from deserializer   
value               	float               	from deserializer   

hive> select * from cpu_busy limit 1;
nurswgvml007	2014-09-25 13:58:04	10.31

hive> select value, datetime from cpu_busy where entity = 'nurswgvml212' limit 1;
100.0	2015-10-20 11:36:05

hive> select value, datetime from cpu_busy where entity = 'nurswgvml212' and datetime > '2015-10-19T19:00:00.000Z' limit 10;
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

hive> select value, datetime from cpu_busy where entity = 'nurswgvml212' and datetime > '2015-10-19T19:00:00.000Z' and datetime <= '2015-10-20T19:00:00.000Z';
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

hive> CREATE EXTERNAL TABLE disk_used
      row format serde 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcSerDe'
      STORED BY 'org.apache.hadoop.hive.jdbc.storagehandler.JdbcStorageHandler'
      TBLPROPERTIES (
        "mapred.jdbc.driver.class"="com.axibase.tsd.driver.jdbc.AtsdDriver",
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://nur.axibase.com/api/sql",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.input.table.name"="'df.disk_used'",
        "mapred.jdbc.output.table.name"="'df.disk_used'",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.hive.lazy.split"= "true"
      );
hive> describe disk_used;
entity              	string              	from deserializer   
datetime            	timestamp           	from deserializer   
value               	float               	from deserializer   
tags$file_system    	string              	from deserializer   
tags$mount_point    	string              	from deserializer   

hive> select * from disk_used limit 1;
nurswgvml006	2016-02-05 15:43:01	0.0	tmpfs	/dev/shm

hive> select value, `tags$mount_point`, datetime  from disk_used where entity = 'nurswgvml301' limit 1;
704.0	/run	2016-08-25 10:43:26

hive> select value, datetime from disk_used where entity = 'nurswgvml301' and `tags$mount_point`='/dev' limit 1;
4.0	2016-08-25 10:43:26

hive> select value, `tags$mount_point`, `tags$file_system` from disk_used where entity = 'nurswgvml301' and datetime > '2016-08-24T19:00:00.000Z' limit 10;
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

hive> select value, `tags$mount_point`, `tags$file_system` from disk_used where entity = 'nurswgvml301' and datetime > '2016-08-24T23:00:00.000Z' and datetime <= '2016-08-25T10:45:00.000Z' sort by value desc;
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
        "mapred.jdbc.url"="jdbc:axibase:atsd:https://nur.axibase.com/api/sql",
        "mapred.jdbc.username"="username",
        "mapred.jdbc.input.table.name"="atsd_series",
        "mapred.jdbc.output.table.name"="atsd_series",
        "mapred.jdbc.password"="password",
        "mapred.jdbc.hive.lazy.split"= "true"
      );

hive> describe atsd_series;
entity              	string              	from deserializer   
metric              	string              	from deserializer   
value               	float               	from deserializer   
tags                	string              	from deserializer   
time                	bigint              	from deserializer   
datetime            	timestamp           	from deserializer   
entity$tags         	string              	from deserializer   
metric$tags         	string              	from deserializer   
entity$groups       	string              	from deserializer   

hive> select * from atsd_series where metric = 'df.disk_used' limit 1;
nurswgvml006	df.disk_used	0.0	file_system=tmpfs;mount_point=/dev/shm	1454686981000	2016-02-05 15:43:01	app=Hadoop/HBASE;ip=10.102.0.5;loc_area=dc1;os=Linux		java-loggers;nmon-linux;nmon-linux-beta;nmon-sub-group;nur-collectors;scollector-linux;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs

hive> select * from atsd_series where metric = 'df.disk_used' and entity = 'nurswgvml301' and datetime > '2016-08-24T23:00:00.000Z' and datetime <= '2016-08-25T10:45:00.000Z' limit 1;
nurswgvml301	df.disk_used	704.0	file_system=tmpfs;mount_point=/run	1472121806000	2016-08-25 10:43:26			nmon-linux;nmon-linux-beta;nur-collectors

hive> select * from atsd_series where metric = 'mpstat.cpu_busy' limit 1;
nurswgvml007	mpstat.cpu_busy	10.31		1411653484000	2014-09-25 13:58:04	alias=007;app=ATSD;environment=prod;ip=10.102.0.6;loc_area=dc1;loc_code=nur,nur;os=Linux		java-loggers;java-virtual-machine;jetty-web-server;nmon-linux;nmon-linux-beta;nmon-sub-group;nur-collectors;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs

hive> select * from atsd_series where metric = 'mpstat.cpu_busy' and entity = 'nurswgvml212' and datetime > '2015-10-19T19:00:00.000Z' and datetime <= '2015-10-20T19:00:00.000Z' limit 1;
nurswgvml212	mpstat.cpu_busy	100.0		1445340965000	2015-10-20 11:36:05	app=ITM;ip=10.102.0.32;loc_area=dc2;os=Red Hat Enterprise Linux Server 6.4		nmon-linux;nmon-linux-beta;scollector-linux;scollector-nur;solarwind-vmware-vm;tcollector - linux;VMware VMs
Time taken: 2.035 seconds, Fetched: 1 row(s)

```