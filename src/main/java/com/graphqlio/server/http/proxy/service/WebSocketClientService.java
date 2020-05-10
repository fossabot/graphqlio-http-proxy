package com.graphqlio.server.http.proxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphqlio.server.http.proxy.domain.*;
import com.graphqlio.server.http.proxy.reactive.PublisherRepository;
import com.graphqlio.server.http.proxy.scheme.ExtendScheme;
import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrame;
import com.graphqlio.wsf.domain.WsfFrameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Map;

@Service
public class WebSocketClientService {

    private final Logger logger = LoggerFactory.getLogger(WebSocketClientService.class);
    private static int sFrameId = 0;

    /// increase Standard Buffer size for socket - introspection message maybe lager than standard buffer size
    /// exception: The decoded text message was too big for the output buffer and the endpoint does not support partial messages

    private static final int MAX_TEXT_MESSAGE_SIZE = 2048000; // 2 Megabytes.
    private static final int BUFFER_SIZE = MAX_TEXT_MESSAGE_SIZE * 5;
    private final PublisherRepository publisherRepository;

    /// ToDo --  read graphql server ws endpoint
    private final String graphqlio_server_ws_endpoint = "ws://127.0.0.1:8080/api/data/graph";

    WebSocketSession webSocketSession = null;

    @SuppressWarnings("unchecked")
    private static BlockingMap<String, String> notificationMap = new BlockingMap();

    @Autowired
    public WebSocketClientService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    private void initSession() {
        if (webSocketSession == null) {
            // TODO: Wir müssen verhindern, dass der GQL-IO Client versucht eine Verbindung aufzubauen, bevor Server korrekt gestartet und GQL-Schema eingelesen (sonst Exception im Server)
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {


                final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.setDefaultMaxBinaryMessageBufferSize(BUFFER_SIZE);
                container.setDefaultMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);

                final WebSocketClient webSocketClient = new StandardWebSocketClient(container);
                final WebSocketHandler webSocketHandler = new WebSocketMessageHandler(this);
                final WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
                final URI uri = URI.create(graphqlio_server_ws_endpoint);

                webSocketSession =
                        webSocketClient.doHandshake(webSocketHandler, webSocketHttpHeaders, uri).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeSession() {
        if (webSocketSession != null) {
            try {
                webSocketSession.close();
                webSocketSession = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String graphQlQuery(String jsonRequest) {

        logger.debug("WebSocketClientService: Incoming GraphQL query: " + jsonRequest);

        initSession();

        String myFrameId = "0";
        if (webSocketSession != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                @SuppressWarnings("unchecked")
                Map<String, Object> operationMap = mapper.readValue(jsonRequest, Map.class);

                String sanitisedQuery = (String) operationMap.get("query");

                if (sanitisedQuery != null && sanitisedQuery.length() > 0) {
                    sanitisedQuery = sanitisedQuery.replaceAll("\n", "");
                    sanitisedQuery = sanitisedQuery.replaceAll("\"", "\\\\\"");

                }

                myFrameId = String.valueOf(sFrameId++);
                WsfFrame requestFrame =
                        WsfFrame.builder()
                                .fid(myFrameId)
                                .rid("0")
                                .type(WsfFrameType.GRAPHQLREQUEST)
                                .data(sanitisedQuery)
                                .build();

                WsfConverter converter = new WsfRequestConverter();
                String requestAsString = converter.convert(requestFrame);

                AbstractWebSocketMessage message = new TextMessage(requestAsString);
                webSocketSession.sendMessage(message);

                String jsonResponse = notificationMap.getOrWait(myFrameId);

                logger.debug("WebSocketClientService: Outgoing GraphQL response: " + jsonResponse);

                return new ExtendScheme().response(jsonResponse);
            } catch (Exception e) {
                String error = "{\"Exception\": \"" + e.toString() + "\"}";
                logger.debug("WebSocketClientService: Outgoing GraphQL response: " + error);
                notificationMap.getOrWait(myFrameId);
                return error;
            }
        } else {
            String error = "{\"Error\": \"Connecting to WebSocket failed\"}";
            logger.debug("WebSocketClientService: Outgoing GraphQL response: " + error);
            notificationMap.getOrWait(myFrameId);
            return error;
        }
    }

    public void notify(WsfFrame wsfFrame) {
        if (wsfFrame.getType() == WsfFrameType.GRAPHQLRESPONSE) {
            notificationMap.putAndSignal(wsfFrame.getFid(), wsfFrame.getData());
        } else if (wsfFrame.getType() == WsfFrameType.GRAPHQLNOTIFIER) {
            publisherRepository.emit(wsfFrame);
        }
    }
}
