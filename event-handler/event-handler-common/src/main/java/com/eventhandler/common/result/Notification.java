package com.eventhandler.common.result;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResultNotification.class, name = "resultNotification"),
        @JsonSubTypes.Type(value = StartNotification.class, name = "startNotification"),
        @JsonSubTypes.Type(value = EndNotification.class, name = "endNotification")
})
public abstract class Notification implements Serializable {}
