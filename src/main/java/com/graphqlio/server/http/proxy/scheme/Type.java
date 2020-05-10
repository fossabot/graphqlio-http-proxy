package com.graphqlio.server.http.proxy.scheme;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Type {
    public String kind;
    public String name;
    public String description;
    public  List<Field> fields = new ArrayList<>();
    public Object inputFields;
    public  List<Object> interfaces = new ArrayList<>();
    public  Object enumValues;
    public  Object possibleTypes;
}
