package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SentimentTimestamp implements Serializable{

    private Double sentiment;

    private Date timestamp;

    public SentimentTimestamp(Double sentiment, Date timestamp) {
        this.sentiment = sentiment;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Double getSentiment() {
        return sentiment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SentimentTimestamp that = (SentimentTimestamp) o;
        return Objects.equals(sentiment, that.sentiment) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentiment, timestamp);
    }
}
