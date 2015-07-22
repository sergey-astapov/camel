package com.eventhandler.common.context;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@Value
@Builder
public class Context implements Serializable {
    @NotNull
    String runId;
    @NonNull
    @Wither
    Long received;
    @Wither
    Long total;
    @Wither
    Long waiting;
    @Wither
    Map<String, String> results;
    @NonNull
    @Wither
    ContextState state;

    public boolean isRunning() {
        boolean status = state instanceof ContextState.Running;
        log.debug("Check context is running, runId: {}, status: {}", runId, status);
        return status;
    }

    public boolean isAllReceived() {
        boolean status = isRunning() && received.equals(total);
        log.debug("Check context received all events, runId: {}, status: {}", runId, status);
        return status;
    }

    public boolean isAllProcessed() {
        boolean status = isAllReceived() && results != null && results.size() == total;
        log.debug("Check context processed all events, runId: {}, status: {}", runId, status);
        return status;
    }
}
