package com.eventhandler.common.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class EventTest {
    @Test
    public void testStartContext() throws IOException {
        check(StartContext.builder().runId("UID").data("DATA").build());
    }

    @Test
    public void testInterContext() throws IOException {
        check(InterContext.builder().runId("UID").data("DATA").build());
    }

    @Test
    public void testStopContext() throws IOException {
        check(StopContext.builder().runId("UID").data("DATA").total(100L).build());
    }

    private void check(Event pattern) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pattern);
        log.info(json);
        Event event = mapper.readValue(json, Event.class);
        assertThat(event, is(pattern));
    }
}
