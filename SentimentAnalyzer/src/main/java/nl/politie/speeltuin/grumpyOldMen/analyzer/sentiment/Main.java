package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.BasicConfigurator;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;

import nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment.dictionary.SentimentDictionary;
import scala.Tuple2;

/**
 * Clearly, Spark is more suited for Scala's functional programmings approach. Java 8 lambda's help to describe the
 * required flow.
 */
public class Main {

    private final Properties properties;

    public Main() {
        BasicConfigurator.configure();
        properties = Properties.getInstance();
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        SparkConf config = createConfig();

        JavaStreamingContext context = new JavaStreamingContext(config, new Duration(2000));

        //ignore kafka id
        JavaDStream<String> messages = createKafkaStream(context).map(Tuple2::_2);

        JavaDStream<Tuple2<Long, String>> filteredTweets = preprocess(messages);

        JavaDStream<Result> analyzedTweets = analyse(filteredTweets);

        CassandraStreamingJavaUtil.javaFunctions(analyzedTweets).writerBuilder(
            properties.get("cassandra.name.keyspace"),
            properties.get("cassandra.name.table"),
            mapToRow(Result.class)).saveToCassandra();

        context.start();
        context.awaitTermination();
    }

    private JavaDStream<Result> analyse(JavaDStream<Tuple2<Long, String>> filteredTweets) {
        SentimentDictionary dictionary = SentimentDictionary.getInstance();
        return filteredTweets.map(tweet -> {
            String[] words = tweet._2().split("\\s+");
            double score = Stream.of(words)
                .map(word -> dictionary.getSentiment(word))
                .filter(Optional::isPresent)
                .mapToDouble(s -> s.get().compute())
                .sum();

            return new Result(tweet._1(), tweet._2(), score / words.length);
        });
    }

    private JavaDStream<Tuple2<Long, String>> preprocess(JavaDStream<String> messages) {
        return messages.filter(tweet -> {
            JsonNode root = new ObjectMapper().readValue(tweet, JsonNode.class);
            JsonNode lang = root.get("lang");
            JsonNode id = root.get("id");
            JsonNode text = root.get("text");
            //return only the english tweets with an id and text
            return lang != null && "en".equals(lang.getTextValue()) && id != null && text != null;
        }).map(tweet -> {
            JsonNode root = new ObjectMapper().readValue(tweet, JsonNode.class);
            Long id = root.get("id").asLong();
            //punctuation and other symbols are discarded
            String text = root.get("text").getTextValue().replaceAll("[^a-zA-Z\\s]", "").trim().toLowerCase();
            return new Tuple2<>(id, text);
        });
    }

    private JavaPairReceiverInputDStream<String, String> createKafkaStream(JavaStreamingContext context) {
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(properties.get("kafka.topic"), properties.getInt("kafka.paralellization"));
        return KafkaUtils
            .createStream(context,
                properties.get("kafka.hosts"),
                properties.get("app.name").replace(' ', '.'),
                topicMap);
    }

    private SparkConf createConfig() {
        SparkConf config = new SparkConf().setAppName(properties.get("app.name"));
        config.set("spark.cassandra.connection.host", properties.get("cassandra.host"));
        config.setMaster(properties.get("spark.master"));
        return config;
    }
}
