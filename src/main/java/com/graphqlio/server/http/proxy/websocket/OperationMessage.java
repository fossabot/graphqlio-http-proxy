//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.graphqlio.server.http.proxy.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class OperationMessage {
    private OperationMessage.Type type;
    private String id;
    private Object payload;

    public static OperationMessage newKeepAliveMessage() {
        return new OperationMessage(OperationMessage.Type.GQL_CONNECTION_KEEP_ALIVE, (String)null, (Object)null);
    }

    public OperationMessage.Type getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public Object getPayload() {
        return this.payload;
    }

    public OperationMessage() {
    }

    public OperationMessage(OperationMessage.Type type, String id, Object payload) {
        this.type = type;
        this.id = id;
        this.payload = payload;
    }

    public static enum Type {
        GQL_CONNECTION_ACK("connection_ack"),
        GQL_CONNECTION_ERROR("connection_error"),
        GQL_CONNECTION_KEEP_ALIVE("ka"),
        GQL_DATA("data"),
        GQL_ERROR("error"),
        GQL_COMPLETE("complete"),
        GQL_CONNECTION_INIT("connection_init"),
        GQL_CONNECTION_TERMINATE("connection_terminate"),
        GQL_START("start"),
        GQL_STOP("stop");

        private static final Map<String, OperationMessage.Type> reverseLookup = new HashMap();
        private final String type;

        private Type(String type) {
            this.type = type;
        }

        @JsonCreator
        public static OperationMessage.Type findType(String type) {
            return (OperationMessage.Type)reverseLookup.get(type);
        }

        @JsonValue
        public String getType() {
            return this.type;
        }

        static {
            OperationMessage.Type[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                OperationMessage.Type type = var0[var2];
                reverseLookup.put(type.getType(), type);
            }

        }
    }
}
