package com.hoangtien2k3.commonlib.kafka.cdc.message;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {

    READ("r"),
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String name;

    Operation(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
