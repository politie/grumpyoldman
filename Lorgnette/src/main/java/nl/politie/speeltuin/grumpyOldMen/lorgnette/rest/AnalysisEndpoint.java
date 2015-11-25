package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping(AnalysisEndpoint.URL)
public class AnalysisEndpoint {

    public static final String URL = "analysis";

    public static final String SENTIMENT_TIME = "sentiment-time";

    private static final String SENTIMENT_TIME_ALL = SENTIMENT_TIME +"-all";

    private final Session session = null;

//    @Autowired
//    public AnalysisEndpoint(@Value("${cassandra.host}") String cassandraHost, @Value("${cassandra.keyspace}")
//    String keyspace) {
//        session = Cluster.builder().addContactPoint(cassandraHost).build().connect(keyspace);
//    }

    @RequestMapping(value = SENTIMENT_TIME, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentimentTimestamp> getSentimentOverTime() {
        Statement query = QueryBuilder.select().column("polarityindex").column("timestamp").from("result").where
                (QueryBuilder.gt("timestamp", Instant.now().minus(Duration.ofHours(2))));
        return session.execute(query).all().stream()
                      .map(r -> new SentimentTimestamp(r.getDouble("polarityindex"), r.getDate("timestamp")))
                      .collect(Collectors.toList());
    }
    @RequestMapping(value = SENTIMENT_TIME_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentimentTimestamp> getAllSentimentOverTime() {
        Statement query = QueryBuilder.select().column("polarityindex").column("timestamp").from("result");
        return session.execute(query).all().stream()
                      .map(r -> new SentimentTimestamp(r.getDouble("polarityindex"), r.getDate("timestamp")))
                      .collect(Collectors.toList());
    }

}
