package com.eventhandler.core.converter;

import com.eventhandler.common.protocol.Event;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import java.io.IOException;

@Slf4j
@Converter
public class StringToEvent {
    @Converter
    public Event convert(String data, Exchange exchange) throws IOException {
        log.debug("Convert data: {}", data);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper.readValue(data, Event.class);
    }
}
