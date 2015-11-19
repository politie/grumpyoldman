package nl.politie.speeltuin.grumpyOldMen.connector.twitter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Event listener that bootstraps the {@link TwitterClient} after Spring boot is (re)initialized.
 */
public class ClientStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        TwitterClient client = context.getBean(TwitterClient.class);
        client.readTweets();
    }
}
