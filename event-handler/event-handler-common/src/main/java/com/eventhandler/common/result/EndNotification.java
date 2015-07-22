package com.eventhandler.common.result;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("endNotification")
public class EndNotification extends Notification {
    String runId;
    String status;
}
