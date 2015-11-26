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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AnalysisEndpoint.URL)
public class AnalysisEndpoint {

    public static final String URL = "analysis";

    public static final String SENTIMENT_TIME = "sentiment-time";

    private static final String SENTIMENT_TIME_ALL = SENTIMENT_TIME + "-all";

    private final Session session;

    @Autowired
    public AnalysisEndpoint(@Value("${cassandra.host}") String cassandraHost, @Value("${cassandra.keyspace}")
    String keyspace) {
        session = Cluster.builder().addContactPoint(cassandraHost).build().connect(keyspace);
    }

    @RequestMapping(value = SENTIMENT_TIME, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentimentTimestamp> getSentimentOverTime() {
        Instant past = Instant.now().minus(Duration.ofSeconds(3));
        return getAllSentimentOverTime().stream().filter(s ->s.getTimestamp().toInstant().isAfter(past)).collect(
                Collectors.toList());
    }

    @RequestMapping(value = SENTIMENT_TIME_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentimentTimestamp> getAllSentimentOverTime() {
        Statement query = QueryBuilder.select().column("polarityindex").column("timestamp").from("result");
        return session.execute(query).all().stream()
                      .map(r -> new SentimentTimestamp(r.getDouble("polarityindex"), r.getDate("timestamp")))
                      .collect(Collectors.toList());
    }

    @RequestMapping(value = "dummy", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SentimentTimestamp> getDummy() {
        Instant now = Instant.now();
        Instant past = new Date(System.currentTimeMillis() - (1000 * 5)).toInstant();
        List<SentimentTimestamp> result = new ArrayList<>();
        for (long i = past.toEpochMilli(); i < now.toEpochMilli(); i += 500) {
            double sentiment = -2 + (Math.random() * 4);
            Date date = new Date(i);
            result.add(new SentimentTimestamp(sentiment, date));
        }
        return result;
    }


}
