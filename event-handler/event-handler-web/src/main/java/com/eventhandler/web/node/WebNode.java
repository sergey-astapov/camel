package com.eventhandler.web.node;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.security.UsernamePasswordCredentials;
import lombok.extern.slf4j.Slf4j;

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
        IQueue<String> eventsQueue = client.getQueue("events-queue");
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
        get("/events/:runId", "application/json",
                (req, res) -> eventsMap.get(req.params(":runId")),
                mapper::writeValueAsString);
        put("/events", (req, res) -> {
            String body = req.body();
            log.info("body: {}", body);
            try {
                eventsQueue.put(body);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(e);
            }
            res.status(201);
            return "OK";
        });
    }
}
