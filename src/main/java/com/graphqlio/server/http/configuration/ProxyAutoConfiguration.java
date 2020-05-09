package com.graphqlio.server.http.configuration;

import com.graphqlio.server.http.proxy.reactive.OutdatedPublisher;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.graphqlio.server.http.proxy.reactive.ProxyPublisherWithSinkFactory;
import com.graphqlio.server.http.proxy.websocket.ProxyWebSocketHandler;

@Configuration
@EnableWebSocket
@Controller
public class ProxyAutoConfiguration implements WebSocketConfigurer{

	private static final String SUBSCRIPTION_ENDPOINT = "/subscriptions";

	@Bean
	public ProxyPublisherWithSinkFactory createProxyPublisherFactory() {
		return new ProxyPublisherWithSinkFactory();
	}
	
	@Autowired
	private ProxyWebSocketHandler proxyWebSocketHandler;

	@Bean
	@ConditionalOnMissingBean
	public ProxyWebSocketHandler proxyWebSocketHandler() {
		return new ProxyWebSocketHandler();
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(proxyWebSocketHandler, SUBSCRIPTION_ENDPOINT);
	}
	
}
