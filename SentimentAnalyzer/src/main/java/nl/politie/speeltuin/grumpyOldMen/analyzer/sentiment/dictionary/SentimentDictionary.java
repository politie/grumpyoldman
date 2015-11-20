package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment.dictionary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In memory dictionary containing the subjectivity lexicon from http://mpqa.cs.pitt.edu/lexicons/
 */
public class SentimentDictionary implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(SentimentDictionary.class);
    private static final String LEXICON_FILE = "/lexicon.txt";
    private final ConcurrentMap<String, Sentiment> cache = new ConcurrentHashMap<String, Sentiment>(8222);

    private SentimentDictionary() {

        System.out.println("Trying to get file " + LEXICON_FILE + " from classpath");
        InputStream inputStream = getClass().getResourceAsStream(LEXICON_FILE);
        Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines();
        lines.map(Sentiment::map).forEach(s -> cache.putIfAbsent(s.getWord(), s));
    }

    public static SentimentDictionary getInstance() {
        return SentimentDictionaryHolder.INSTANCE;
    }

    public Optional<Sentiment> getSentiment(String word) {
        return Optional.ofNullable(cache.get(word));
    }

    private static class SentimentDictionaryHolder {

        private static final SentimentDictionary INSTANCE = new SentimentDictionary();
    }
}
