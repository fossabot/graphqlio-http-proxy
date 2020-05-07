package com.graphqlio.server.http.proxy.reactive;

import java.util.Map;

import org.reactivestreams.Publisher;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class ProxyPublisherWithSinkFactory {
	
	private FluxSink<String> fluxSink;
	private Flux<String> publisher;
	private ConnectableFlux<String> connectablePublisher;
		
	public PublisherWithSink create() {
		publisher = Flux.create(emitter -> fluxSink=emitter );	
		connectablePublisher = publisher.publish();
		return new PublisherWithSink(fluxSink,connectablePublisher) ;
	}
	
	public class PublisherWithSink {
		
		private FluxSink<String> fluxSink;
		private Flux<String> publisher;
		
		
		public PublisherWithSink(FluxSink<String> fluxSink, Flux<String> publisher) {
			super();
			this.fluxSink = fluxSink;
			this.publisher = publisher;
		}

		public FluxSink<String> getFluxSink() {
			return fluxSink;
		}
	
		public Flux<String> getPublisher() {
			return publisher;
		}
		
	}

}
