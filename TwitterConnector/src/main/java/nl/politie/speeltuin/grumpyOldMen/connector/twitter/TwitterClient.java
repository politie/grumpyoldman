package nl.politie.speeltuin.grumpyOldMen.connector.twitter;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Arrays;

/**
 * Spring wrapper that configures and manages the hbc twitter {@Link Client}.
 */
@Service
public class TwitterClient {

    private final Client client;

    @Autowired
    public TwitterClient(
            SingletonQueueWrapper queueWrapper,
            @Value("${app.name}") String appName,
            @Value("${twitter.consumerKey}") String consumerKey,
            @Value("${twitter.consumerSecret}") String consumerSecret,
            @Value("${twitter.token}") String token,
            @Value("${twitter.secret}") String secret,
            @Value("${app.terms}") String[] terms) {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(Arrays.asList(terms));

        ClientBuilder clientBuilder = new ClientBuilder().name(appName).hosts(hosebirdHosts).authentication
                (hosebirdAuth).endpoint(hosebirdEndpoint);
        client = clientBuilder.processor(new StringDelimitedProcessor(queueWrapper.getQueue())).build();
    }

    public void readTweets() {
        client.connect();
    }

    @PreDestroy
    public void cleanUp() {
        client.stop();
    }

}
