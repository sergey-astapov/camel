package com.eventhandler.core.aggregate.test;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.context.ContextFsm;
import com.eventhandler.common.protocol.Event;
import com.eventhandler.common.protocol.ProcessEvent;
import com.eventhandler.core.aggregate.AbstractAggregationStrategy2;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.fsm.FSMException;
import ru.yandex.qatools.fsm.impl.YatomataImpl;

import java.util.Map;

@Slf4j
public class TestAggregationStrategy2 extends AbstractAggregationStrategy2 {
    private final Map<String, Context> contexts;

    public TestAggregationStrategy2(Map<String, Context> contexts) {
        this.contexts = contexts;
    }

    @Override
    protected Context transformContext(Event e) {
        String runId = e.getRunId();
        Context old = contexts.get(runId);
        YatomataImpl<ContextFsm> engine;
        try {
            engine = new YatomataImpl<>(ContextFsm.class, new ContextFsm(old), old.getState());
        } catch (FSMException err) {
            throw new IllegalStateException(err);
        }
        engine.fire(e);
        Context context = engine.getFSM().getContext();
        contexts.put(runId, context);

        log.info("Context, runId: {},\nold: {},\nnew: {}", new Object[]{runId, old, context});
        return context;
    }
}
