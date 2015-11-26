# Grumpy Old Men

The Grumpy Old Men is a demo project about Big Data, Continuous Development and Micro Services. Sentiment analysis is
 a common example for explaining Big Data techniques. This example analyzes the sentiment of tweets. We filter the positive and negative words and provide these words with a value. The rating algorithm is completely arbitrary and does not represent any production-like environment. This example is merely used for explaining the development and interaction of different components in a distributed environment like an Open Stack cluster. It is not about doing sentiment analysis.

You need the following services in order to proceed:

- Kafka
- Zookeeper
- Spark
- Cassandra

Optional, if you also would like to see an example implementation of Continuous Integration:

- Debian cluster environment
- Jenkins
- Nexus

##Components

### ContinuousIntegration

This folder contains a parent pom that describes a continuous integration process. The idea is that you define a git trigger that starts a Jenkins pipeline. That pipeline builds, tests and deploys both a java artifact and a debian package. The debian servers hosting the java artifact use Nexus as deb repository. A simple apt-get update suffices for the automatic deployment.

### TwitterConnector

The TwitterConnector is a simple Spring Boot application that reads tweets and publishes them on a Kafka topic.

### SentimentAnalyzer

The SentimentAnalyzer is a Spark Streaming Job that consumes the tweets from Kafka and perform a 'sentiment analysis'. The results are published to Cassandra.

### Lorgnette
The word 'Lorgnette' is French for a type of opera glass. It is a data visualizer, based upon Spring Integration.

##Micro Services

Each component is an autonomous application with it's own life cycle. The interdependencies are managed by Kafka and 
Cassandra. This makes the design of the components itself simple, but the design of application as a whole more 
complex. This example is nice example of this phenomena.