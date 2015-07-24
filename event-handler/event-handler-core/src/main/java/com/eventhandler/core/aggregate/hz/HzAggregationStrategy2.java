package com.eventhandler.core.aggregate.hz;

import com.eventhandler.common.context.Context;
import com.eventhandler.common.context.ContextFsm;
import com.eventhandler.common.protocol.Event;
import com.eventhandler.core.aggregate.AbstractAggregationStrategy2;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.fsm.impl.YatomataImpl;

import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class HzAggregationStrategy2 extends AbstractAggregationStrategy2 {
    HazelcastInstance hz;
    long timeout;

    @Override
    protected Context transformContext(Event e) {
        IMap<String, Context> map = hz.getMap("contexts");
        String runId = e.getRunId();

        Context old = null;
        Context context = null;
        try {
            map.tryLock(runId, timeout, TimeUnit.SECONDS);
            old = map.get(runId);
            YatomataImpl<ContextFsm> engine = new YatomataImpl<>(ContextFsm.class, new ContextFsm(old), old.getState());
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
