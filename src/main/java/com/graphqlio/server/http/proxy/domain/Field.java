package com.graphqlio.server.http.proxy.domain;

import java.util.ArrayList;
import java.util.List;

public class Field {

    public String name;
    public String description;
    public List<Object> args = new ArrayList<>();
    public FieldType type;
    public boolean isDeprecated;
    public Object deprecationReason;
}
