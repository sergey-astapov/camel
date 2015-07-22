package com.eventhandler.core.aggregate.hz;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.context.ContextFsm;
import com.eventhandler.common.context.ContextState;
import com.eventhandler.common.protocol.Event;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.core.aggregate.AbstractAggregationStrategy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.fsm.annotations.AfterTransit;
import ru.yandex.qatools.fsm.impl.YatomataImpl;

import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class HzAggregationStrategy extends AbstractAggregationStrategy {
    HazelcastInstance hz;
    long timeout;

    public class ContextFsm2 extends ContextFsm {
        public ContextFsm2(Context state) {
            super(state);
        }
        @AfterTransit
        public void afterTransit(ContextState.Running state, StartContext event) {
            log.debug("afterTransit: {}, {}", state, event);
        }
    }

    @Override
    protected Context transformContext(Event e) {
        IMap<String, Context> map = hz.getMap("contexts");
        String runId = e.getRunId();

        Context old = null;
        Context context = null;
        try {
            map.tryLock(runId, timeout, TimeUnit.SECONDS);
            if (!map.containsKey(runId)) {
                map.put(runId, Context.builder()
                        .runId(runId)
                        .received(0L)
                        .state(new ContextState.Idle())
                        .build());
            }
            old = map.get(runId);
            YatomataImpl<ContextFsm2> engine = new YatomataImpl<>(ContextFsm2.class, new ContextFsm2(old), old.getState());
            engine.fire(e);
            context = engine.getFSM().getContext();
            map.put(runId, context);
        } catch (Exception err) {
            log.error("Failed to add new exchange", err);
        } finally {
            map.unlock(runId);
        }

        log.info("Context, runId: {}, old: {}, new: {}", new Object[] {runId, old, context});
        return context;
    }
}