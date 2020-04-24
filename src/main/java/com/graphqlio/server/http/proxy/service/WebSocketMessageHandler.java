package com.graphqlio.server.http.proxy.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.graphqlio.server.http.proxy.domain.WsfResponseConverter;
import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrame;

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
      
      int pos = messageText.indexOf("GRAPHQL-RESPONSE");
      if (pos > 0) {

          WsfConverter wsfConverter = new WsfResponseConverter();
          WsfFrame responseFrame = wsfConverter.convert(messageText);

          if (responseFrame != null) {
        	  String messageId = responseFrame.getFid();   //FrameID
              webSocketClient.notify(messageId, responseFrame.getData());
        	          	  
          }
       }   
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      logger.info("connection established: " + session.getId());
    }
	
	
}
