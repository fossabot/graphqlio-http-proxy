package com.graphqlio.server.http.proxy.scheme;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExtendScheme {
    public String response(String jsonResponse) throws Exception {
        if (isSchemaRequest(jsonResponse)) {
            Map<String, Map<String, Object>> responseMap = new ObjectMapper().readValue(jsonResponse, HashMap.class);
            appendSubscriptionsTo(responseMap);
            return new ObjectMapper().writeValueAsString(responseMap);
        } else {
            return jsonResponse;
        }
    }

    private boolean isSchemaRequest(String jsonResponse) {
        return jsonResponse.startsWith("{\"data\":{\"__schema\":{\"types\":");
    }

    private void appendSubscriptionsTo(Map<String, Map<String, Object>> responseMap) {
        Map<String, Object> schema = (Map<String, Object>) responseMap.get("data").get("__schema");
        Map<String, String> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put("name", "Subscription");
        schema.put("subscriptionType", subscriptionMap);
        Type type = new Type();
        type.kind = "OBJECT";
        type.name = "Subscription";
        // outdated: String!
        Field field = new Field();
        field.name = "outdated";
        field.type = FieldType.createNonNull("SCALAR", "String");
        type.fields.add(field);
        // notifications(scope: String!): String!
        Arg arg = new Arg();
        arg.name = "scope";
        arg.type = FieldType.createNonNull("SCALAR", "String");

        Field field2 = new Field();
        field2.name = "notifications";
        field2.args.add(arg);
        field2.type = FieldType.createNonNull("SCALAR", "String");
        type.fields.add(field2);

        List<Object> types = (List<Object>) schema.get("types");
        types.add(type);
    }
}
