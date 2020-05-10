package com.graphqlio.server.http.proxy.scheme;

public class FieldType {
    public String kind;
    public String name;
    public FieldType ofType;

    public static FieldType createNonNull(String kind, String name) {
        return FieldType.create("NON_NULL", null, FieldType.create(kind, name, null));
    }

    public static FieldType create(String kind, String name, FieldType ofType) {
        FieldType newKind = new FieldType();
        newKind.kind = kind;
        newKind.name = name;
        newKind.ofType = ofType;
        return newKind;
    }
}
