package com.eventhandler.common.protocol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InterContext.class, name = "interContext"),
        @JsonSubTypes.Type(value = StartContext.class, name = "startContext"),
        @JsonSubTypes.Type(value = StopContext.class, name = "stopContext")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Event implements Serializable {
    public abstract String getRunId();
    public abstract String getIdempotentId();
}
