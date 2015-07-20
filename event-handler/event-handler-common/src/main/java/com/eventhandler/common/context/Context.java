package com.eventhandler.common.context;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Value
@Builder
public class Context implements Serializable {
    @NotNull
    String runId;
    @NonNull
    @Wither
    Long current;
    @Wither
    Long total;
    @Wither
    Long waiting;
    @NonNull
    @Wither
    ContextState state;

    public boolean isRunning() {
        boolean run = !isStopped() && (state instanceof ContextState.Running || state instanceof ContextState.Stopping);
        log.debug("Check context is running, runId: {}, running: {}", runId, run);
        return run;
    }

    public boolean isStopped() {
        boolean stop = current != null && current.equals(total);
        log.debug("Check context stopped, runId: {}, stopped: {}", runId, stop);
        return stop;
    }

    public boolean isReadyForProcess() {
        return isRunning() || isStopped();
    }
}
