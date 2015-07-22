package com.eventhandler.core.routes;

import com.eventhandler.common.protocol.InterContext;
import com.eventhandler.common.protocol.StartContext;
import com.eventhandler.common.protocol.StopContext;
import com.eventhandler.common.result.EndNotification;
import com.eventhandler.common.result.ResultNotification;
import com.eventhandler.common.result.StartNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RoutesTest extends CamelSpringTestSupport {

    public static final String RUN_ID = "RUN_ID";
    public static final String DATA = "DATA";
    public static final String REF_1 = "REF_1";
    public static final String REF_2 = "REF_2";

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[] {
                "META-INF/spring/eh-test.xml",
                "META-INF/spring/eh-camel.xml"
        });
    }

    @Test
    public void test() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(3);
        mockOut.message(0).body().isInstanceOf(StartNotification.class);
        mockOut.message(1).body().isInstanceOf(ResultNotification.class);
        mockOut.message(2).body().isInstanceOf(EndNotification.class);

        ObjectMapper mapper = new ObjectMapper();

        StartContext startContext = StartContext.builder()
                .runId(RUN_ID)
                .data(DATA)
                .build();
        template.sendBody("direct:in", mapper.writeValueAsString(startContext));

        InterContext interContext1 = InterContext.builder()
                .runId(RUN_ID)
                .reference(REF_1)
                .data(DATA)
                .build();
        template.sendBody("direct:in", mapper.writeValueAsString(interContext1));

        InterContext interContext2 = InterContext.builder()
                .runId(RUN_ID)
                .reference(REF_2)
                .data(DATA)
                .build();
        template.sendBody("direct:in", mapper.writeValueAsString(interContext2));

        StopContext stopContext = StopContext.builder()
                .runId(RUN_ID)
                .data(DATA)
                .total(2L)
                .build();
        template.sendBody("direct:in", mapper.writeValueAsString(stopContext));
        assertMockEndpointsSatisfied();
    }
}
