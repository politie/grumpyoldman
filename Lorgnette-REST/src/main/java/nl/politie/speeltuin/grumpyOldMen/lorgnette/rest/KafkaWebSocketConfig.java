package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.kafka.core.ConnectionFactory;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;
import org.springframework.integration.kafka.core.ZookeeperConfiguration;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.integration.kafka.serializer.common.StringDecoder;
import org.springframework.integration.kafka.support.ZookeeperConnect;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.outbound.WebSocketOutboundMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Configuration
public class KafkaWebSocketConfig {

    private static final String URL = "/tweets";


    @Bean
    ZookeeperConfiguration zkConfiguration(@Value("${zookeeper.host}") String zookeeperHost) {
        return new ZookeeperConfiguration(new ZookeeperConnect(zookeeperHost));
    }

    @Bean
    ConnectionFactory kafkaConnectionFactory(ZookeeperConfiguration zkConfiguration) {
        return new DefaultConnectionFactory(zkConfiguration);
    }

    @Bean
    MessageChannel kafkaChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    /**
     * The KafkaMessageDrivenChannelAdapter implements MessageProducer, reads a KafkaMessage with its Metadata and
     * sends it as a Spring Integration message to the provided MessageChannel.
     *
     * @param kafkaChannel
     *         A DirectChannel to expose messages to the transformer
     * @param kafkaConnectionFactory
     *
     * @return
     */
    @Bean
    KafkaMessageDrivenChannelAdapter kafkaMessageDrivenChannelAdapter(MessageChannel kafkaChannel,
            ConnectionFactory kafkaConnectionFactory, @Value("${kafka.topic.name}") String topic) {
        KafkaMessageDrivenChannelAdapter adapter = new KafkaMessageDrivenChannelAdapter(
                new KafkaMessageListenerContainer(kafkaConnectionFactory, topic)
        );
        adapter.setKeyDecoder(new StringDecoder());
        adapter.setPayloadDecoder(new StringDecoder());
        adapter.setOutputChannel(requestChannel());
//        adapter.setAutoStartup(false);
        return adapter;
    }

    @Bean(name = "webSocketFlow.input")
    MessageChannel requestChannel() {
        return new DirectChannel();
    }

    @Bean
    IntegrationFlow webSocketFlow() {
        return f -> {
            Function<Message, Object> splitter = m -> serverWebSocketContainer()
                    .getSessions()
                    .keySet()
                    .stream()
                    .map(s -> MessageBuilder.fromMessage(m)
                                            .setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, s)
                                            .build())
                    .collect(Collectors.toList());
            f.split(Message.class, splitter)
             .channel(c -> c.executor(Executors.newCachedThreadPool()))
             .handle(webSocketOutboundAdapter());
        };
    }


    @Bean
    ServerWebSocketContainer serverWebSocketContainer() {
        return new ServerWebSocketContainer(URL).withSockJs();
    }

    @Bean
    MessageHandler webSocketOutboundAdapter() {
        return new WebSocketOutboundMessageHandler(serverWebSocketContainer());
    }
}
