package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment.dictionary;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SentimentMapperTest {

    private static final String CORRECT_INPUT = "type=weaksubj len=1 word1=abandoned pos1=adj stemmed1=n " +
                                                "priorpolarity=negative";

    private static final String WORD = "abandoned";

    @Test
    public void should_map_correctly() {
        Sentiment sentiment = Sentiment.map(CORRECT_INPUT);
        assertThat(sentiment.getWord(), is(equalTo(WORD)));
    }

}
