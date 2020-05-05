package com.graphqlio.server.http.configuration;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graphqlio.server.http.proxy.reactive.ProxyPublisherFactory;

@Configuration
public class ProxyAutoConfiguration {

	@Bean
	public ProxyPublisherFactory createProxyPublisherFactory() {
		return new ProxyPublisherFactory();
	}
	
	
}
