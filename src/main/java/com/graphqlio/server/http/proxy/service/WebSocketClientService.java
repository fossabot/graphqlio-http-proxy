package com.graphqlio.server.http.proxy.service;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import com.graphqlio.server.http.proxy.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.AbstractWebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrame;
import com.graphqlio.wsf.domain.WsfFrameType;

@Service
public class WebSocketClientService {

	private final Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);
	private static int sFrameId = 0;

  /// increase Standard Buffer size for socket - introspection message maybe lager than standard buffer size
  /// exception: The decoded text message was too big for the output buffer and the endpoint does not support partial messages
		
	private static final int MAX_TEXT_MESSAGE_SIZE = 2048000; // 2 Megabytes.
	private static final int BUFFER_SIZE = MAX_TEXT_MESSAGE_SIZE * 5;

	

	/// ToDo --  read graphql server ws endpoint
    private final String graphqlio_server_ws_endpoint = "ws://127.0.0.1:8080/api/data/graph";
	
	WebSocketSession webSocketSession = null;
	
	@SuppressWarnings("unchecked")
	private static BlockingMap<String, String> notificationMap = new BlockingMap();
	
	private void initSession() {
		if ( webSocketSession == null) {
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
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeSession() {
		if (webSocketSession != null) {
			try {
			     webSocketSession.close();
			     webSocketSession = null;
			}
			catch (Exception e){
				e.printStackTrace();				
			}	
		}	
	}

	
	
	public String graphQlQuery(String jsonRequest) {

		logger.info("WebSocketClientService: Incoming GraphQL query: " + jsonRequest);
			
		initSession();
		
		String myFrameId = "0";
		if (webSocketSession != null) {	
			try {
				ObjectMapper mapper = new ObjectMapper();
				
				@SuppressWarnings("unchecked")
				Map<String, Object> operationMap = mapper.readValue( jsonRequest, Map.class );
				
				String sanitisedQuery = (String) operationMap.get("query");
				
				if (sanitisedQuery != null && sanitisedQuery.length() > 0) {
					sanitisedQuery = sanitisedQuery.replaceAll("\n","");
					sanitisedQuery = sanitisedQuery.replaceAll("\"","\\\\\"");
					
				}
								
				myFrameId= String.valueOf(sFrameId++);
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

				logger.info("WebSocketClientService: Outgoing GraphQL response: " + jsonResponse);
				Map<String, Map<String, Object>> responseMap = new ObjectMapper().readValue(jsonResponse, HashMap.class);
				appendSubscriptionsTo(responseMap);
				return new ObjectMapper().writeValueAsString(responseMap);
			}
			catch (Exception e) {
				String error = "{\"Exception\": \"" + e.toString() + "\"}";
				logger.info("WebSocketClientService: Outgoing GraphQL response: " + error);				
				notificationMap.getOrWait(myFrameId);
				return error;
			}			
		}
		else {
			String error = "{\"Error\": \"Connecting to WebSocket failed\"}";
			logger.info("WebSocketClientService: Outgoing GraphQL response: " + error);				
			notificationMap.getOrWait(myFrameId);
			return error;
		}
	}

	private void appendSubscriptionsTo(Map<String, Map<String, Object>> responseMap) {
		Map<String, Object> schema = (Map<String, Object>) responseMap.get("data").get("__schema");
		Map<String, String> subscriptionMap = new LinkedHashMap<>();
		subscriptionMap.put("name", "Subscription");
		schema.put("subscriptionType", subscriptionMap);
		System.out.print("");
		Type type = new Type();
		type.kind = "OBJECT";
		type.name = "Subscription";
		Field field = new Field();
		field.name = "outdated";
		FieldType fieldType = new FieldType();
		fieldType.kind = "SCALAR";
		fieldType.name = "String";
		field.type = fieldType;
		type.fields.add(field);
		List<Object> types = (List<Object>) schema.get("types");
		types.add(type);
	}

	public void notify(String frameId, String message) {
		notificationMap.putAndSignal(frameId, message);
	}
}
