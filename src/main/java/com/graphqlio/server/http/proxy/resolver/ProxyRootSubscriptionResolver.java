package com.graphqlio.server.http.proxy.resolver;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.graphqlio.server.http.proxy.reactive.ProxyPublisherWithSinkFactory;
import com.graphqlio.server.http.proxy.reactive.ProxyPublisherWithSinkFactory.PublisherWithSink;


public class ProxyRootSubscriptionResolver implements GraphQLSubscriptionResolver {

	@Autowired
	private ProxyPublisherWithSinkFactory proxyPublisherWithSinkFactory;
	
	public Publisher<String> outdated() {
		PublisherWithSink publisherWithSink = proxyPublisherWithSinkFactory.create();
		// ToDo: Publisher im Repository ablegen ... 		
		return publisherWithSink.getPublisher();
	} 

	public Publisher<String> notifications(String scope) {
		// TODO: Wie wird der Paramter "scope" genutzt?
		PublisherWithSink publisherWithSink = proxyPublisherWithSinkFactory.create();
		// ToDo: Publisher im Repository ablegen ... 		
		return publisherWithSink.getPublisher();
	}
	
}
