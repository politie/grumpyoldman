package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class Result implements Serializable {

    private final Date timeStamp;

    private final Long id;

    private final String tweet;

    private final Double polarityIndex;

    public Result(Long id, String tweet, Double polarityIndex) {
        this.id = id;
        this.tweet = tweet;
        this.polarityIndex = polarityIndex;
        this.timeStamp = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getTweet() {
        return tweet;
    }

    public Double getPolarityIndex() {
        return polarityIndex;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result result = (Result) o;
        return Double.compare(result.polarityIndex, polarityIndex) == 0 &&
               Objects.equals(id, result.id) &&
               Objects.equals(tweet, result.tweet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tweet, polarityIndex);
    }
}
