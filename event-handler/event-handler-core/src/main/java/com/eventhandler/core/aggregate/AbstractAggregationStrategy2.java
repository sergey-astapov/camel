package com.eventhandler.core.aggregate;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.protocol.Event;
import com.eventhandler.common.protocol.ProcessEvent;
import com.eventhandler.common.result.EndNotification;
import com.eventhandler.common.result.Notification;
import com.eventhandler.common.result.ResultNotification;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAggregationStrategy2 implements AggregationStrategy {
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Event e = newExchange.getIn().getBody(Event.class);
        Context context = transformContext(e);
        if (oldExchange == null) {
            newExchange.getIn().setHeader("context", context);
            newExchange.getIn().setBody(addNotifications(new LinkedList<>(), context));
            return newExchange;
        }
        oldExchange.getIn().setHeader("context", context);
        oldExchange.getIn().setBody(addNotifications(oldExchange.getIn().getBody(List.class),
                context));
        return oldExchange;
    }

    private List<Notification> addNotifications(List<Notification> list, Context context) {
        if (context.isAllProcessed()) {
            list.add(ResultNotification.builder()
                    .runId(context.getRunId())
                    .results(context.getResults())
                    .build());
            list.add(EndNotification.builder()
                    .runId(context.getRunId())
                    .status("OK")
                    .build());
        }
        return list;
    }

    protected abstract Context transformContext(Event e);
}
