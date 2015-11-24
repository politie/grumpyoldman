package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;

import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketHandlerDecorator implements WebSocketHandler {


    private final WebSocketHandler delegate;

    private final KafkaMessageDrivenChannelAdapter kafkaMessageProducer;

    private final ServerWebSocketContainer container;

    public WebSocketHandlerDecorator(KafkaMessageDrivenChannelAdapter kafkaMessageProducer, WebSocketHandler
            delegate, ServerWebSocketContainer container) {
        this.delegate = delegate;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.container = container;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        delegate.afterConnectionEstablished(session);
        kafkaMessageProducer.start();

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        delegate.handleTransportError(session, exception);
        if (container.getSessions().isEmpty()) {
            kafkaMessageProducer.stop();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        delegate.afterConnectionClosed(session, closeStatus);
        if (container.getSessions().isEmpty()) {
            kafkaMessageProducer.stop();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
