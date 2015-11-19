package nl.politie.speeltuin.grumpyOldMen;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;

/**
 * Simple tweet handler that reads the tweets at a fixed interval and pushes them to a kafka topic. The principle
 * should be self-balancing as the {@link Scheduled} annotation ensures the execution on a separate thread. This
 * implies that on heavy load many instance of this class will process the tweets.
 */
@Component
public class ScheduledTwitterHandler {

    private final BlockingQueue<String> queue;

    private final String topic;

    private final Producer<String, String> producer;

    @Autowired
    public ScheduledTwitterHandler(SingletonQueueWrapper queueWrapper, KafkaConfigurer kafkaConfigurer, @Value
            ("${kafka.topic}") String topic) {
        this.queue = queueWrapper.getQueue();
        producer = kafkaConfigurer.createProducer();
        this.topic = topic;
    }

    @Scheduled(fixedRate = 500)
    public void handle() throws InterruptedException {

        while (!queue.isEmpty()) {
            String msg = queue.take();
            System.out.println(msg);
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, msg);
            producer.send(data);
        }
    }

    @PreDestroy
    public void cleanUp() {
        producer.close();

    }

}
