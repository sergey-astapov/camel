package com.eventhandler.core.fsm;

import com.eventhandler.common.protocol.Event;
import com.eventhandler.common.protocol.InterContext;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.common.protocol.StopContext;
import lombok.Data;
import ru.yandex.qatools.fsm.StopConditionAware;
import ru.yandex.qatools.fsm.annotations.FSM;
import ru.yandex.qatools.fsm.annotations.OnTransit;
import ru.yandex.qatools.fsm.annotations.Transit;
import ru.yandex.qatools.fsm.annotations.Transitions;

import java.util.LinkedList;
import java.util.List;

import static com.eventhandler.core.fsm.ContextState.*;

@FSM(start = Idle.class)
@Transitions({
        @Transit(from = {Idle.class, WaitingStart.class}, on = StartContext.class, to = Running.class),
        @Transit(from = Idle.class, on = {InterContext.class, StopContext.class}, to = WaitingStart.class),
        @Transit(from = Running.class, on = InterContext.class, to = Running.class),
        @Transit(from = Running.class, on = StopContext.class, to = WaitingLast.class),
        @Transit(from = WaitingLast.class, on = InterContext.class, to = WaitingLast.class),
})
@Data
public class ContextFsm implements StopConditionAware<ContextState, Event> {
    String runId;
    Long current;
    Long total;
    List<InterContext> events = new LinkedList<>();

    @OnTransit
    public void onStartContext(Idle from, Running to, StartContext event) {
        validate(event.getRunId());
        current = 0L;
    }

    @OnTransit
    public void onStartContext(WaitingStart from, Running to, StartContext event) {
        validate(event.getRunId());
        current = 0L;
        while(events.size() > 0) {
            InterContext ic = events.remove(0);
            current++;
        }
    }

    @OnTransit
    public void onInterContext(Idle from, WaitingStart to, InterContext event) {
        validate(event.getRunId());
        events.add(event);
    }

    @OnTransit
    public void onInterContext(Running from, Running to, InterContext event) {
        validate(event.getRunId());
        current++;
    }

    @OnTransit
    public void onInterContext(WaitingLast from, WaitingLast to, InterContext event) {
        validate(event.getRunId());
        current++;
    }

    @OnTransit
    public void onStopContext(Idle from, WaitingStart to, StopContext event) {
        validate(event.getRunId());
        total = event.getTotal();
    }

    @OnTransit
    public void onStopContext(Running from, WaitingLast to, StopContext event) {
        validate(event.getRunId());
        total = event.getTotal();
    }

    @Override
    public boolean isStopRequired(ContextState state, Event event){
        return current != null && total != null && current.equals(total);
    }

    private void validate(String runId) {
        if (this.runId == null) {
            this.runId = runId;
        }
        if (!this.runId.equals(runId)) {
            throw new IllegalArgumentException("Wrong UID, runId: " + this.runId + ", event runId: " + runId);
        }
    }
}
