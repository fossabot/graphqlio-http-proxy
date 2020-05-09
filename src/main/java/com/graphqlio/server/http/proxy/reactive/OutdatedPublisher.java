package com.graphqlio.server.http.proxy.reactive;

import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class OutdatedPublisher {

    private static final String SUBSCRIPTION_NAME = "outdated";
    private final ProxyPublisherWithSinkFactory.PublisherWithSink publisherWithSink;

    public OutdatedPublisher() {
        ProxyPublisherWithSinkFactory proxyPublisherWithSinkFactory = new ProxyPublisherWithSinkFactory();
        publisherWithSink = proxyPublisherWithSinkFactory.create();
    }

    public void subscribe(Subscriber<String> subscriber) {
        publisherWithSink.getPublisher().subscribe(subscriber);
    }

    public void emit(String data) {
        publisherWithSink.getFluxSink().next(data);
    }

}
