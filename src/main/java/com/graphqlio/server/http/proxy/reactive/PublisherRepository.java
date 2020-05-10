package com.graphqlio.server.http.proxy.reactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphqlio.wsf.domain.WsfFrame;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PublisherRepository {
    private List<Subscriber<OperationMessage>> outdatedSubscribers = new ArrayList<>();
    private Map<String, List<Subscriber<OperationMessage>>> notificationsSubscribers = new HashMap<>();

    public void subscribeOnNotifications(String scopeId, Subscriber<OperationMessage> subscriber) {
        notificationsSubscribers.putIfAbsent(scopeId, new ArrayList<>());
        List<Subscriber<OperationMessage>> subscribers = notificationsSubscribers.get(scopeId);
        subscribers.add(subscriber);
    }

    public void subscribeOnOutdated(Subscriber<OperationMessage> subscriber) {
        outdatedSubscribers.add(subscriber);
    }

    public void emit(WsfFrame wsfFrame) {
        String scopeId = wsfFrame.getData().substring(10, wsfFrame.getData().length()-3);
        emitOnOutdated(scopeId);
        emitOnNotifications(scopeId, scopeId);
    }

    public void unsubscribe(Subscriber<OperationMessage> subscriber) {
        outdatedSubscribers.remove(subscriber);
        List<String> removeEmptyElements = new ArrayList<>();
        notificationsSubscribers.forEach((key, value) -> {
            value.remove(subscriber);
            if(removeEmptyElements.isEmpty()) {
                removeEmptyElements.add(key);
            }
        });
        removeEmptyElements.forEach(key -> {
            notificationsSubscribers.remove(key);
        });
    }

    /**
     * @param scopeId scopeId
     * @param data E.g. a String "xyz", a POJO or Map. Everything will be converted to a json-String.
     */
    private void emitOnNotifications(String scopeId, Object data) {
        List<Subscriber<OperationMessage>> subscribers = notificationsSubscribers.get(scopeId);
        if(subscribers != null) {
            emitData(subscribers,"notifications", data);
        }
    }

    /**
     * @param data E.g. a String "xyz", a POJO or Map. Everything will be converted to a json-String.
     */
    private void emitOnOutdated(Object data) {
        emitData(outdatedSubscribers,"outdated", data);
    }

    private void emitData(List<Subscriber<OperationMessage>> subscribers, String subscriptionName, Object data) {
        try {
            Map<String, Object> payloadData = new LinkedHashMap<>();
            Map<String, Object> payloadDataSubscription = new LinkedHashMap<>();
            String payloadDataSubscriptionValue = null;

            if(data instanceof String) {
                payloadDataSubscriptionValue = (String) data;
            } else  {
                payloadDataSubscriptionValue = new ObjectMapper().writeValueAsString(data);
            }

            payloadData.put("data", payloadDataSubscription);
            payloadDataSubscription.put(subscriptionName, payloadDataSubscriptionValue);

            OperationMessage payload = new OperationMessage(OperationMessage.Type.GQL_DATA, "1", payloadData);

            subscribers.forEach(e -> e.onNext(payload));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
