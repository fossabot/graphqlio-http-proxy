package com.graphqlio.server.http.proxy.reactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OutdatedPublisher {

    private static final String SUBSCRIPTION_NAME = "outdated";
    private List<Subscriber<Object>> subscribers = new ArrayList<>();

    public void subscribe(Subscriber<Object> subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * @param data E.g. a String "xyz", a POJO or Map. Everything will be converted to a json-String.
     */
    public void emit(Object data) {
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
            payloadDataSubscription.put(SUBSCRIPTION_NAME, payloadDataSubscriptionValue);

            OperationMessage payload = new OperationMessage(OperationMessage.Type.GQL_DATA, "1", payloadData);

            subscribers.forEach(e -> e.onNext(payload));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
