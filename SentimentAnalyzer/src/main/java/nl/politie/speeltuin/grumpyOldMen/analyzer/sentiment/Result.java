package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment;

import java.util.Objects;

public class Result {

    private Long id;

    private String tweet;

    private Double polarityIndex;

    public Result(Long id, String tweet, Double polarityIndex) {
        this.id = id;
        this.tweet = tweet;
        this.polarityIndex = polarityIndex;
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
