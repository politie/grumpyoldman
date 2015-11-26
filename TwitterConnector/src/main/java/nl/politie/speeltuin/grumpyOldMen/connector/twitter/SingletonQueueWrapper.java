package nl.politie.speeltuin.grumpyOldMen.connector.twitter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Singleton queue to cache tweets. A bit dirty, but it works.
 */
@Component
@Scope("singleton")
public class SingletonQueueWrapper {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(100000);

    public BlockingQueue<String> getQueue() {
        return queue;
    }
}
