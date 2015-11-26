#Lorgnette

This component reads the collected tweets from Kafka and the analysis from Cassandra. The tweets are filtered in the 
same manner as in the SentimentAnalyzer and send to a websocket. This process is done with Spring Integration.

The data from Cassandra is exposed via a REST API. Simple and effective.

## The UI
The UI is part of this component to avoid CORS issues.