package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment.dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * In memory dictionary containing the subjectivity lexicon from http://mpqa.cs.pitt.edu/lexicons/
 */
public class SentimentDictionary {

    private final ConcurrentMap<String, Sentiment> cache = new ConcurrentHashMap<String, Sentiment>(8222);

    private static final Logger log = LoggerFactory.getLogger(SentimentDictionary.class);

    private static final String LEXICON_FILE = "lexicon.txt";

    private SentimentDictionary() {
        try {
            Stream<String> lines = Files
                    .lines(Paths.get(Sentiment.class.getClassLoader().getResource(LEXICON_FILE).toURI()));
            lines.map(Sentiment::map).forEach(s -> cache.putIfAbsent(s.getWord(), s));
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
        }
    }

    public static SentimentDictionary getInstance() {
        return SentimentDictionaryHolder.INSTANCE;
    }

    public Optional<Sentiment> getSentiment(String word){
        return Optional.ofNullable(cache.get(word));
    }

    private static class SentimentDictionaryHolder {

        private static final SentimentDictionary INSTANCE = new SentimentDictionary();
    }

}
