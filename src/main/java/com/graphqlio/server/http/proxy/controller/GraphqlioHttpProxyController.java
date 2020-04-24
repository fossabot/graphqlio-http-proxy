package com.graphqlio.server.http.proxy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.graphqlio.server.http.proxy.service.WebSocketClientService;

@RestController
public class GraphqlioHttpProxyController {

	@Autowired
	WebSocketClientService webSocketClient;
		
		
	@PostMapping(value = "/graphql", consumes = {"application/json"} )	
	public String graphQlQuery (@RequestBody String json) {							
	   return webSocketClient.graphQlQuery(json);
	}

}
