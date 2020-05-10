package com.graphqlio.server.http.proxy.service;


import com.graphqlio.server.http.proxy.domain.WsfResponseConverter;
import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrame;
import com.graphqlio.wsf.domain.WsfFrameType;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class WebSocketMessageHandler extends AbstractWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    WebSocketClientService webSocketClient = null;

    protected WebSocketMessageHandler(WebSocketClientService webSocketClient) {
        this.webSocketClient = webSocketClient;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        logger.info("message received: " + message.getPayload());

        String messageText = message.getPayload();

        // [1,1,"GRAPHQL-RESPONSE",{"data":{"updateRoute":{"flightNumber":"LH2084","departure":"HAM","destination":"MUC"}}}]
        WsfConverter wsfConverter = new WsfResponseConverter();
        WsfFrame responseFrame = null;
        if (messageText.contains("GRAPHQL-RESPONSE")) {
            responseFrame = wsfConverter.convert(messageText);
        } else if (messageText.contains("GRAPHQL-NOTIFY")) {
            responseFrame = createNotifierFrameFrom(messageText);
        }
        webSocketClient.notify(responseFrame);
    }

    private WsfFrame createNotifierFrameFrom(String messageText) {
        JSONArray arr = new JSONArray(messageText);
        String fid = String.valueOf(arr.get(0));
        String rid = String.valueOf(arr.get(1));
        String data = String.valueOf(arr.get(3));
        return WsfFrame.builder().fid(fid).rid(rid).type(WsfFrameType.GRAPHQLNOTIFIER).data(data).build();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("connection established: " + session.getId());
    }


}
