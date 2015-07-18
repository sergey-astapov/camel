package com.eventhandler.core.aggregate;

import com.eventhandler.common.protocol.Event;
import com.eventhandler.core.fsm.ContextFsm;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import ru.yandex.qatools.fsm.Yatomata;
import ru.yandex.qatools.fsm.impl.FSMBuilder;

@Slf4j
public class FSMAggregationStrategy implements AggregationStrategy {
    private final Yatomata<ContextFsm> fsmEngine;

    public FSMAggregationStrategy(Class fsmClass) {
        this.fsmEngine = new FSMBuilder(ContextFsm.class).build();
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//        Event e = newExchange.getIn().getBody(Event.class);
//        if (oldExchange == null) {
//            List<Event> events = new LinkedList<>();
//            events.add(e);
//            newExchange.getIn().setBody(events);
//            return newExchange;
//        }
//        List<Event> events = oldExchange.getIn().getBody(List.class);
//        events.add(e);
//        return oldExchange;

        Event result = oldExchange == null ? null : oldExchange.getIn().getBody(Event.class);
        try {
            Event event = newExchange.getIn().getBody(Event.class);
            result = fsmEngine.fire(event);
        } catch (Exception e) {
            log.error("fsm error", e);
        }

        if (result != null) {
            newExchange.getIn().setBody(result);
        }
        return newExchange;
    }

    public boolean isCompleted() {
        return fsmEngine.isCompleted();
    }
}