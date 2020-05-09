package com.graphqlio.server.http.proxy.websocket;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphqlio.server.http.proxy.reactive.OutdatedPublisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class ProxyWebSocketHandler extends AbstractWebSocketHandler implements SubProtocolCapable {

    @Autowired
    private OutdatedPublisher outdatedPublisher;
    private final Logger logger = LoggerFactory.getLogger(ProxyWebSocketHandler.class);
    private static final String SUB_PROTOCOL_TEXT = "graphql-ws";


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("ProxyWebSocketHandler afterConnectionEstablished session    :" + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("ProxyWebSocketHandler afterConnectionClosed session    :" + session);
    }

    @Override
    public List<String> getSubProtocols() {
        return Arrays.asList(
                SUB_PROTOCOL_TEXT);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OperationMessage operationMessage = objectMapper.readValue(message.getPayload(), OperationMessage.class);
        logger.info("ProxyWebSocketHandler handleTextMessage session    :" + session);
        logger.info("ProxyWebSocketHandler handleTextMessage session ID :" + session.getId());
        logger.info("ProxyWebSocketHandler handleTextMessage this       :" + this);
        logger.info("ProxyWebSocketHandler handleTextMessage Thread     :" + Thread.currentThread());
        // TODO müssen wir stop-Befehl handlen, um Daten zu löschen?
        if (operationMessage.getType() == OperationMessage.Type.GQL_CONNECTION_INIT) {
            OperationMessage ack = new OperationMessage(OperationMessage.Type.GQL_CONNECTION_ACK, null, null);
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(ack)));

            outdatedPublisher.subscribe(new Subscriber<String>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(String s) {
                    try {
                        String responseJsonRepresentation = new ObjectMapper().writeValueAsString(s);
                        Map<String, Object> responseData = new LinkedHashMap<>();
                        responseData.put("data", responseJsonRepresentation);
                        OperationMessage responseMsg = new OperationMessage(OperationMessage.Type.GQL_DATA, "1", responseJsonRepresentation);
                        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(responseMsg)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
        else if (operationMessage.getType() == OperationMessage.Type.GQL_START) {
            outdatedPublisher.emit("12345");
        }
    }


}
