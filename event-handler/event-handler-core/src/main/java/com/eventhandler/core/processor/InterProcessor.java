package com.eventhandler.core.processor;

import com.eventhandler.common.protocol.InterContext;
import com.eventhandler.common.protocol.ProcessEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Slf4j
public class InterProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        InterContext event = exchange.getIn().getBody(InterContext.class);
        ProcessEvent res = ProcessEvent.builder()
                .runId(event.getRunId())
                .reference(event.getReference())
                .status("OK")
                .build();
        log.debug("Process event: {}, res: {}", event, res);
        exchange.getIn().setBody(res);
    }
}
