package com.graphqlio.server.http.proxy.resolver;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.graphqlio.server.http.proxy.reactive.ProxyPublisherFactory;


public class ProxyRootSubscriptionResolver implements GraphQLSubscriptionResolver {

	@Autowired
	private ProxyPublisherFactory proxyPublisherFactory;
	
	public Publisher<String> outdated() {
		Publisher<String> publisher = proxyPublisherFactory.create();
		// ToDo: Publisher im Repository ablegen ... 		
		return publisher;
	} 
	
}
