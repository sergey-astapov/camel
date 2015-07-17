package com.eventhandler.web.node;

import com.eventhandler.common.protocol.Event;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.security.UsernamePasswordCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import static spark.Spark.*;

@Slf4j
public class WebNode {
    public static void main(String[] args) throws Exception {
        setPort(Integer.valueOf(Optional.ofNullable(System.getProperty("http.port")).orElse("8080")));

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
        clientConfig.setCredentials(new UsernamePasswordCredentials("london", "london-pass"));
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        final IMap eventsMap = client.getMap("events");
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        get("/", (req, res) -> {
            res.status(404);
            return "Not found";
        });
        get("/events", (req, res) -> {
            res.status(404);
            return "Not found";
        });
        get("/events/:uid", "application/json",
                (req, res) -> eventsMap.get(req.params(":uid")),
                mapper::writeValueAsString);
        put("/events", (req, res) -> {
            String body = req.body();
            log.info("body: {}", body);
            Event event = null;
            try {
                event = mapper.readValue(body, Event.class);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            eventsMap.put(event.getUid(), event);
            res.status(201);
            return "OK";
        });
    }
}
