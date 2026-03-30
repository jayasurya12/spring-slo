package com.slo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe request counter — mirrors utils/counter.js
 */
@Service
public class RequestCounterService {

    private final AtomicLong incomingCount = new AtomicLong(0);
    private final AtomicLong outgoingCount = new AtomicLong(0);

    public long incrementIncoming() {
        return incomingCount.incrementAndGet();
    }

    public long incrementOutgoing() {
        return outgoingCount.incrementAndGet();
    }

    public Map<String, Long> getCounts() {
        return Map.of(
                "incomingCount", incomingCount.get(),
                "outgoingCount", outgoingCount.get());
    }
}
