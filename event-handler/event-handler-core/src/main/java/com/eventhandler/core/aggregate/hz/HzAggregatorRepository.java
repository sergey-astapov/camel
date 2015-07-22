package com.eventhandler.core.aggregate.hz;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultExchangeHolder;
import org.apache.camel.spi.AggregationRepository;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HzAggregatorRepository implements AggregationRepository {
    private IMap<String, DefaultExchangeHolder> map;
    private long timeout;

    public HzAggregatorRepository(HazelcastInstance hazelcastInstance, String repositoryName, long timeout){
        map = hazelcastInstance.getMap(repositoryName);
        this.timeout = timeout;
    }

    @Override
    public Exchange add(CamelContext camelContext, String key, Exchange exchange) {
        log.debug("Add exchange for key: {}, entry: {}", key, exchange);
        try {
            DefaultExchangeHolder holder = DefaultExchangeHolder.marshal(exchange);
            map.tryPut(key, holder, timeout, TimeUnit.SECONDS);
            return toExchange(camelContext, holder);
        } catch (Exception e) {
            log.error("Failed to add new exchange", e);
        } finally {
            map.unlock(key);
        }
        return null;
    }

    @Override
    public Exchange get(CamelContext camelContext, String key) {
        log.debug("Get exchange for key: {}", key);
        try {
            map.tryLock(key, timeout, TimeUnit.SECONDS);
            return toExchange(camelContext, map.get(key));
        } catch (Exception e) {
            log.error("Failed to get the exchange", e);
        }
        return null;
    }

    @Override
    public void remove(CamelContext camelContext, String key, Exchange exchange) {
        log.debug("Remove exchange for key: {}", key);
        try {
            map.tryRemove(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to remove the exchange", e);
        } finally {
            map.unlock(key);
        }
    }

    @Override
    public void confirm(CamelContext camelContext, String exchangeId) {
        /* Nothing to do */
    }

    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(map.keySet());
    }

    private Exchange toExchange(CamelContext camelContext, DefaultExchangeHolder holder) {
        Exchange exchange = null;
        if (holder != null) {
            exchange = new DefaultExchange(camelContext);
            DefaultExchangeHolder.unmarshal(exchange, holder);
        }
        return exchange;
    }
}