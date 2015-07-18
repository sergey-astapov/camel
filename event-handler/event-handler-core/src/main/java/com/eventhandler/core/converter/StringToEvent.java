package com.eventhandler.core.converter;

import com.eventhandler.common.protocol.Event;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import java.io.IOException;

@Converter
public class StringToEvent {
    @Converter
    public Event convert(String data, Exchange exchange) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper.readValue(data, Event.class);
    }
}
