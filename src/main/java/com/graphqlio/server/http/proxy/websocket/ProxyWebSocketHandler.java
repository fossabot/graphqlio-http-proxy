package com.graphqlio.server.http.proxy.websocket;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class ProxyWebSocketHandler extends AbstractWebSocketHandler implements SubProtocolCapable {

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
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("ProxyWebSocketHandler handleTextMessage session    :" + session);
        logger.info("ProxyWebSocketHandler handleTextMessage session ID :" + session.getId());
        logger.info("ProxyWebSocketHandler handleTextMessage this       :" + this);
        logger.info("ProxyWebSocketHandler handleTextMessage Thread     :" + Thread.currentThread());
    }


}
