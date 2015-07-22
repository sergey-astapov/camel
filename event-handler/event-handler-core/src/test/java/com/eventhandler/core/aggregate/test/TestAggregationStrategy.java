package com.eventhandler.core.aggregate.test;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.context.ContextFsm;
import com.eventhandler.common.context.ContextState;
import com.eventhandler.common.protocol.Event;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.common.result.StartNotification;
import com.eventhandler.core.aggregate.AbstractAggregationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import ru.yandex.qatools.fsm.annotations.AfterTransit;
import ru.yandex.qatools.fsm.impl.YatomataImpl;

import java.util.Map;

@Slf4j
public class TestAggregationStrategy extends AbstractAggregationStrategy {
    private final Map<String, Context> contexts;
    private final ProducerTemplate template;
    private final String startEndpoint;

    public TestAggregationStrategy(Map<String, Context> contexts, CamelContext camelContext, String startEndpoint) {
        this.contexts = contexts;
        this.template = camelContext.createProducerTemplate();
        this.startEndpoint = startEndpoint;
    }

    public class ContextFsm2 extends ContextFsm {
        public ContextFsm2(Context state) {
            super(state);
        }

        @AfterTransit
        public void afterTransit(ContextState.Running state, StartContext event) {
            log.debug("afterTransit: {}, {}", state, event);
            template.sendBody(startEndpoint, StartNotification.builder()
                    .runId(event.getRunId())
                    .build());
        }
    }

    @Override
    protected Context transformContext(Event e) {
        String runId = e.getRunId();

        Context old = null;
        Context context = null;
        try {
            if (!contexts.containsKey(runId)) {
                contexts.put(runId, Context.builder()
                        .runId(runId)
                        .received(0L)
                        .state(new ContextState.Idle())
                        .build());
            }
            old = contexts.get(runId);
            YatomataImpl<ContextFsm2> engine = new YatomataImpl<>(ContextFsm2.class, new ContextFsm2(old), old.getState());
            engine.fire(e);
            context = engine.getFSM().getContext();
            contexts.put(runId, context);
        } catch (Exception err) {
            throw new IllegalStateException("Failed to add new exchange", err);
        }

        log.info("Context, runId: {},\nold: {},\nnew: {}", new Object[]{runId, old, context});
        return context;
    }
}
