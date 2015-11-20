#Sentiment Analyzer

This project is a simple example to analyze sentiment for a given Twitter stream. The tweets are collected by a 
Twitter collector and placed on a Kafka topic. 

The output of the Spark Streaming Job is written to Cassandra.

##Prepare Cassandra

Please install the following tools, if you haven't done so.

* cassandra
* python
* cqlsh

The next step is to create a namespace and table in Cassandra. Please open cqlsh and enter the following commands.

CREATE KEYSPACE mykeyspace
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
USE mykeyspace;
CREATE TABLE result (
  result_id bigint PRIMARY KEY,
  tweet text,
  polarityIndex double
);
 