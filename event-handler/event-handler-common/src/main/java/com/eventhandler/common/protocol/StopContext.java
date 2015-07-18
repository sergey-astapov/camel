package com.eventhandler.common.protocol;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("stopContext")
public class StopContext extends Event {
    String runId;
    String data;
    Long total;
}
