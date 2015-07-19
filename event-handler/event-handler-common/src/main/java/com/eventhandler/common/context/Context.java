package com.eventhandler.common.context;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

import java.io.Serializable;

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
        return !isStopped() && (state instanceof ContextState.Running || state instanceof ContextState.Stopping);
    }

    public boolean isStopped() {
        return current != null && current.equals(total);
    }
}
