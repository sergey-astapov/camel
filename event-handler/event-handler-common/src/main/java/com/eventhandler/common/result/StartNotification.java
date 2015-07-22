package com.eventhandler.common.result;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("startNotification")
public class StartNotification extends Notification {
    String runId;
}
