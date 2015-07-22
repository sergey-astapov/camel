package com.eventhandler.common.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class NotificationTest {
    @Test
    public void testStartNotification() throws IOException {
        check(StartNotification.builder().runId("UID").build());
    }

    @Test
    public void testResultNotification() throws IOException {
        Map<String, String> results = new HashMap<>();
        results.put("REF_1", "OK");
        results.put("REF_2", "OK");
        results.put("REF_3", "OK");
        check(ResultNotification.builder().runId("UID").results(results).build());
    }

    @Test
    public void testStopContext() throws IOException {
        check(EndNotification.builder().runId("UID").status("OK").build());
    }

    private void check(Notification pattern) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pattern);
        log.info(json);
        Notification n = mapper.readValue(json, Notification.class);
        assertThat(n, is(pattern));
    }
}
