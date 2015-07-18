package com.eventhandler.common.protocol;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("startContext")
public class StartContext extends Event {
    String runId;
    String data;
}
