package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment.dictionary;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simplified Java representation of a MPQA subjectivity lexicon entry. See http://mpqa.cs.pitt
 * .edu/lexicons/subj_lexicon/.
 */
public class Sentiment implements Serializable {

    private final String type;

    private final String word;

    private final String pos;

    private final String priorPolarity;

    public Sentiment(String type, String word, String pos, String
        priorPolarity) {
        this.type = type;
        this.word = word;
        this.pos = pos;
        this.priorPolarity = priorPolarity;
    }

    static Sentiment map(String input) {
        String[] keyValues = input.split("\\s+");
        int length = keyValues.length;
        String type = null, word = null, pos = null, priorPolarity = null;
        for (int i = 0; i < length; i++) {
            String[] keyValue = keyValues[i].split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            switch (key) {
                case "type": {
                    type = value;
                    break;
                }
                case "word1": {
                    word = value;
                    break;
                }
                case "pos1": {
                    pos = value;
                    break;
                }
                case "priorpolarity": {
                    priorPolarity = value;
                    break;
                }
            }
        }
        return new Sentiment(type, word, pos, priorPolarity);
    }

    public String getWord() {
        return word;
    }

    public double compute() {
        double emphasis_multiplier = "strongsubj".equals(type) ? 2f : 1f;
        double speech_multiplier = determineSpeechMultiplier(pos);
        double polarity_multiplier = determinePolarityMultiplier(priorPolarity);

        return (emphasis_multiplier + speech_multiplier) * polarity_multiplier;
    }

    private double determinePolarityMultiplier(String priorPolarity) {
        switch (priorPolarity) {
            case "positive":
                return 2f;
            case "negative":
                return -2f;
            case "neutral":
                return 1f;
            case "both":
                return 1f;
            case "weakneg":
                return -1.5f;
            default:
                return 1;
        }
    }

    private double determineSpeechMultiplier(String pos) {
        switch (pos) {
            case "anypos":
                return 1f;
            case "adj":
                return 3f;
            case "verb":
                return 2f;
            case "noun":
                return 3f;
            case "adverb":
                return 1.5f;
            default:
                return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sentiment sentiment = (Sentiment) o;
        return
            Objects.equals(type, sentiment.type) &&
                Objects.equals(word, sentiment.word) &&
                Objects.equals(pos, sentiment.pos) &&
                Objects.equals(priorPolarity, sentiment.priorPolarity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, word, pos, priorPolarity);
    }
}
