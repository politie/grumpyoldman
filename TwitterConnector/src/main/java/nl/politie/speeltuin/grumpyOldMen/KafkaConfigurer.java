package nl.politie.speeltuin.grumpyOldMen;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

/**
 * Leaves Kafka producer config to defaults.
 */
@Component
public class KafkaConfigurer {


    private final String brokerList;

    @Autowired
    public KafkaConfigurer(@Value("${kafka.broker.list}") String[] brokerList) {
        String arraySting = Arrays.toString(brokerList);
        int length = arraySting.length();
        this.brokerList = arraySting.substring(1,length-1);
    }

    public Producer<String, String> createProducer() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", brokerList);
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());


        return new KafkaProducer<String, String>(properties);
    }

}
