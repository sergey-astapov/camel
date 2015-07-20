package com.eventhandler.cache.node;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CacheNode {
    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext("META-INF/spring/eh-hz.xml");
    }
}
