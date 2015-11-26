package nl.politie.speeltuin.grumpyOldMen.lorgnette.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.kafka.core.ConnectionFactory;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;
import org.springframework.integration.kafka.core.ZookeeperConfiguration;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.integration.kafka.serializer.common.StringDecoder;
import org.springframework.integration.kafka.support.ZookeeperConnect;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractPayloadTransformer;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.outbound.WebSocketOutboundMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * When looking at this code, you wonder why Spring forgot the reason they started their venture. Anyway, this is a
 * clear example of verbose xml config in java speak. The cool thing is that you can do something advanced with
 * merely config. Sure you can use lambda's to make it look even better, but doesn't help understanding the implemented
 * flow. Verbosity is simply part of enterprise speak.
 */
@Configuration
@EnableScheduling
public class KafkaWebSocketConfig {

    private static final String URL = "/tweets";

    @Value("${kafka.topic.name}")
    private String topic;

    @Value("${zookeeper.host}")
    private String zookeeperHost;

    @Autowired
    KafkaMessageDrivenChannelAdapter adapter;

    /**
     * Simple, but blunt, polling mechanism to start the flow when a websocket session starts
     */
    @Scheduled(fixedRate = 1000)
    public void checkForSessions() {
        boolean hasSessions = !serverWebSocketContainer().getSessions().isEmpty();
        if (hasSessions && !adapter.isRunning()) {
            adapter.start();
        }
        if (!hasSessions && adapter.isRunning()) {
            adapter.stop();
        }
    }

    @Bean
    ZookeeperConfiguration zkConfiguration() {
        return new ZookeeperConfiguration(new ZookeeperConnect(zookeeperHost));
    }

    @Bean
    ConnectionFactory kafkaConnectionFactory() {
        return new DefaultConnectionFactory(zkConfiguration());
    }

    @Bean
    MessageChannel kafkaChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    /**
     * The KafkaMessageDrivenChannelAdapter implements MessageProducer, reads a KafkaMessage with its Metadata and
     * sends it as a Spring Integration message to the provided MessageChannel. This is an endpoint that starts the
     * flow.
     */
    @Bean
    KafkaMessageDrivenChannelAdapter kafkaMessageDrivenChannelAdapter() {
        KafkaMessageDrivenChannelAdapter adapter = new KafkaMessageDrivenChannelAdapter(
                new KafkaMessageListenerContainer(kafkaConnectionFactory(), topic)
        );
        adapter.setKeyDecoder(new StringDecoder());
        adapter.setPayloadDecoder(new StringDecoder());
        adapter.setOutputChannel(filterChannel());
        adapter.setAutoStartup(false);
        return adapter;
    }

    @Bean
    public MessageChannel filterChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    /**
     * Filter tweets as in SentimentAnalyzer
     *
     * @return
     */
    @Bean
    @Filter(inputChannel = "filterChannel", outputChannel = "headerEnricherChannel")
    public MessageSelector filterPayload() {
        return m -> {
            try {
                JsonNode root = new ObjectMapper().readValue(m.getPayload().toString(), JsonNode.class);
                JsonNode lang = root.get("lang");
                JsonNode id = root.get("id");
                JsonNode text = root.get("text");
                //return only the english tweets with an id and text
                return lang != null && "en".equals(lang.asText()) && id != null && text != null;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        };
    }

    /**
     * Add the web socket session id in the spring message header.
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "headerEnricherChannel")
    public AbstractMessageSplitter headerEnricher() {
        AbstractMessageSplitter splitter = new AbstractMessageSplitter() {
            @Override
            protected List splitMessage(Message<?> message) {
                List results =
                        serverWebSocketContainer().getSessions().keySet().stream().map(id -> MessageBuilder.fromMessage
                                (message).setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, id).build()).collect
                                (Collectors.toList());
                return results;

            }
        };
        splitter.setOutputChannelName("transformChannel");

        return splitter;
    }

    /**
     * Transform the raw tweet to only the tweet text
     *
     * @return
     */

    @Bean
    @Transformer(inputChannel = "transformChannel", outputChannel = "dispatchChannel")
    public AbstractPayloadTransformer<?, ?> transformer() {
        return new AbstractPayloadTransformer<String, String>() {
            @Override
            protected String transformPayload(String payload) throws Exception {
                JsonNode root = new ObjectMapper().readValue(payload, JsonNode.class);
                JsonNode text = root.get("text");
                return text.asText();
            }
        };
    }


    @Bean
    public MessageChannel dispatchChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    ServerWebSocketContainer serverWebSocketContainer() {
        return new ServerWebSocketContainer(URL).withSockJs();
    }

    /**
     * Websocket endpoint.
     */
    @Bean
    @ServiceActivator(inputChannel = "dispatchChannel")
    MessageHandler webSocketOutboundAdapter() {
        return new WebSocketOutboundMessageHandler(serverWebSocketContainer());
    }

}
