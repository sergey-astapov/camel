package com.eventhandler.common.protocol;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("interContext")
public class InterContext extends Event {
    String uid;
    String data;
}
