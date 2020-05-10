package com.graphqlio.server.http.proxy.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphqlio.server.http.proxy.reactive.OutdatedPublisher;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        if (operationMessage.getType() == OperationMessage.Type.GQL_CONNECTION_INIT) {
            OperationMessage ack = new OperationMessage(OperationMessage.Type.GQL_CONNECTION_ACK, null, null);
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(ack)));

            outdatedPublisher.subscribe(new Subscriber<Object>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                }

                @Override
                public void onNext(Object responsePayload) {
                    try {
                        if(session.isOpen()) {
                            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(responsePayload)));
                        } else {
                            // FIXME: Unsubscribe
                        }
                    } catch (Exception e) {
                        // FIXME: Unsubscribe
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public boolean equals(Object obj) {
                    if(obj == null) return false;
                    if(obj instanceof Session) {
                        return session.getId().equals(((Session) obj).getId());
                    } else  {
                        return false;
                    }
                }

                @Override
                public int hashCode() {
                    return session.hashCode();
                }
            });
        } else if(operationMessage.getType() == OperationMessage.Type.GQL_STOP) {
            // TODO: Unsubscribe
        }
    }


}
