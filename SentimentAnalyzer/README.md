#Sentiment Analyzer

This project is a simple example to analyze sentiment for a given Twitter stream. The tweets are collected by a 
Twitter collector and placed on a Kafka topic.
 
The code is reasonably self-explanatory, but assumes you have a basic understanding of Java 8 lambda's. 

The following [Lexicon](http://mpqa.cs.pitt.edu/lexicons/subj_lexicon/) is used for the identification of words. 
These words are rated in the Sentiment class. We emphasize that we implemented a completely arbitrary algorithm for 
our polarity rating. Please feel free to fork this repo and implement your own.

The output of the Spark Streaming Job is written to Cassandra.

##What do you need?

We tested this implementation with:

* Java 8
* Spark 1.5.2
* Cassandra 2.2.3
* python
* cqlsh

##Prepare Cassandra

The next step is to create a namespace and table in Cassandra. Please open cqlsh and enter the following commands.

```sql
CREATE KEYSPACE mykeyspace
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
USE mykeyspace;
CREATE TABLE result (
  id bigint PRIMARY KEY,
  tweet text,
  polarityindex double,
  timestamp timestamp
```

##Building and running

`mvn package` suffices for acquiring the shared jar.