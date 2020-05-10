package com.graphqlio.server.http.proxy.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.socket.TextMessage;

import org.springframework.web.socket.WebSocketSession;

public class PlaygroundSubscriber implements Subscriber<OperationMessage> {
    WebSocketSession session;

    PlaygroundSubscriber(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
    }

    @Override
    public void onNext(OperationMessage responsePayload) {
        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(responsePayload)));
            } else {
                // FIXME: Unsubscribe
            }
        } catch (Exception e) {
            // FIXME: Unsubscribe
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof PlaygroundSubscriber) {
            return session.getId().equals(((PlaygroundSubscriber) obj).session.getId());
        } else  {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return session.getId().hashCode();
    }
}
