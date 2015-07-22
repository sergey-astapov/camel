package com.eventhandler.common.result;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("resultNotification")
public class ResultNotification extends Notification {
    String runId;
    Map<String, String> results;
}
