package com.eventhandler.core.aggregate;

import com.eventhandler.common.protocol.Event;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EventsAggregationStrategy implements AggregationStrategy {
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Event e = newExchange.getIn().getBody(Event.class);
        if (oldExchange == null) {
            List<Event> events = new LinkedList<>();
            events.add(e);
            newExchange.getIn().setBody(events);
            return newExchange;
        }
        List<Event> events = oldExchange.getIn().getBody(List.class);
        events.add(e);
        return oldExchange;
    }
}
