package com.eventhandler.common.protocol;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProcessEvent extends Event {
    String runId;
    String reference;
    String status;

    public String getIdempotentId() {
        return String.valueOf(runId) + String.valueOf(reference) + getClass().getName();
    }
}
