package com.eventhandler.common.context;

import com.eventhandler.common.protocol.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.fsm.StopConditionAware;
import ru.yandex.qatools.fsm.annotations.FSM;
import ru.yandex.qatools.fsm.annotations.OnTransit;
import ru.yandex.qatools.fsm.annotations.Transit;
import ru.yandex.qatools.fsm.annotations.Transitions;

import java.util.HashMap;
import java.util.Map;

import static com.eventhandler.common.context.ContextState.*;

@Slf4j
@FSM(start = Idle.class)
@Transitions({
        @Transit(from = {Idle.class, Starting.class}, on = StartContext.class, to = Running.class),
        @Transit(from = {Idle.class, Starting.class}, on = {InterContext.class, StopContext.class}, to = Starting.class),
        @Transit(from = Running.class, on = {InterContext.class, StopContext.class, ProcessEvent.class}, to = Running.class),
})
public class ContextFsm implements StopConditionAware<ContextState, Event> {
    private Context context;

    public ContextFsm(Context state) {
        this.context = state;
    }

    public Context getContext() {
        return context;
    }

    @OnTransit
    public void onStartContext(Idle from, Running to, StartContext event) {
        validate(event.getRunId());
        updateContext(Context.builder()
                .runId(event.getRunId())
                .received(0L)
                .state(to)
                .build());
    }

    @OnTransit
    public void onInterContext(Idle from, Starting to, InterContext event) {
        validate(event.getRunId());
        updateContext(Context.builder()
                .runId(event.getRunId())
                .received(0L)
                .waiting(1L)
                .state(to)
                .build());
    }

    @OnTransit
    public void onStopContext(Idle from, Starting to, StopContext event) {
        validate(event.getRunId());
        updateContext(Context.builder()
                .runId(event.getRunId())
                .received(0L)
                .waiting(0L)
                .total(event.getTotal())
                .state(to)
                .build());
    }

    @OnTransit
    public void onInterContext(Running from, Running to, InterContext event) {
        validate(event.getRunId());
        updateContext(context.withReceived(context.getReceived() + 1));
    }

    @OnTransit
    public void onStartContext(Starting from, Running to, StartContext event) {
        validate(event.getRunId());
        updateContext(Context.builder()
                .runId(event.getRunId())
                .received(context.getWaiting())
                .waiting(0L)
                .total(context.getTotal())
                .state(to)
                .build());
    }

    @OnTransit
    public void onInterContext(Starting from, Starting to, InterContext event) {
        validate(event.getRunId());
        updateContext(context.withWaiting(context.getWaiting() + 1));
    }

    @OnTransit
    public void onStopContext(Starting from, Starting to, StopContext event) {
        validate(event.getRunId());
        updateContext(context.withTotal(event.getTotal()));
    }

    @OnTransit
    public void onStopContext(Running from, Running to, StopContext event) {
        validate(event.getRunId());
        updateContext(context.withTotal(event.getTotal()));
    }

    @OnTransit
    public void onProcessedEvent(Running from, Running to, ProcessEvent event) {
        validate(event.getRunId());
        Map<String, String> res = context.getResults();
        if (res == null) {
            res = new HashMap<>();
        }
        res.put(event.getReference(), event.getStatus());
        updateContext(context.withResults(res));
    }

    @Override
    public boolean isStopRequired(ContextState state, Event event){
        return this.context.isAllProcessed();
    }

    private void updateContext(Context newContext) {
        log.debug("updateContext,\noldContext: {},\nnewContext: {}", context, newContext);
        context = newContext;
    }

    private void validate(String runId) {
        if (!context.getRunId().equals(runId)) {
            throw new IllegalArgumentException("Wrong runId, runId: " + context.getRunId() + ", new runId: " + runId);
        }
    }
}
