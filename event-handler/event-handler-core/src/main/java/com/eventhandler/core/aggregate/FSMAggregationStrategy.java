package com.eventhandler.core.aggregate;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.context.ContextFsm;
import com.eventhandler.common.context.ContextState;
import com.eventhandler.common.protocol.Event;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import ru.yandex.qatools.fsm.impl.YatomataImpl;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Data
public class FsmAggregationStrategy implements AggregationStrategy {
    HazelcastInstance hz;
    long timeout;

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Event e = newExchange.getIn().getBody(Event.class);
        if (oldExchange == null) {
            List<Event> events = new LinkedList<>();
            events.add(e);
            newExchange.getIn().setHeader("contextState", transformContext(e));
            newExchange.getIn().setBody(events);
            return newExchange;
        }
        List<Event> events = oldExchange.getIn().getBody(List.class);
        events.add(e);
        oldExchange.getIn().setHeader("contextState", transformContext(e));
        return oldExchange;
    }

    private Context transformContext(Event e) {
        IMap<String, Context> map = hz.getMap("contexts");
        String runId = e.getRunId();

        Context old = null;
        Context context = null;
        try {
            //map.tryLock(runId, timeout, TimeUnit.SECONDS);
            if (!map.containsKey(runId)) {
                map.put(runId, Context.builder()
                        .runId(runId)
                        .current(0L)
                        .state(new ContextState.Idle())
                        .build());
            }
            old = map.get(runId);
            YatomataImpl<ContextFsm> engine = new YatomataImpl<>(ContextFsm.class, new ContextFsm(old),
                    old.getState());
            engine.fire(e);
            context = engine.getFSM().getContext();
            map.put(runId, context);
        } catch (Exception err) {
            log.error("Failed to add new exchange", err);
        } finally {
            //map.unlock(runId);
        }

        log.info("Context, runId: {}, old: {}, new: {}", new Object[] {runId, old, context});
        return context;
    }
}