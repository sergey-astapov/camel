package com.eventhandler.node;

import org.apache.camel.spring.Main;

public class StartNode {
    private static Main app;

    public static void main(String[] args) throws Exception {
        app = new Main();
        app.setApplicationContextUri("META-INF/spring/eh-context.xml");
        app.enableHangupSupport();
        app.run();
    }
}
