package com.slo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Instant;
import java.util.Map;

/**
 * Health and readiness endpoints — mirrors /health and /ready in app.js
 */
@RestController
public class HealthController {

    @Value("${spring.application.name:spring-slo}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final long startTime = System.currentTimeMillis();

    /** GET /health — liveness probe */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long heapUsed = mem.getHeapMemoryUsage().getUsed();
        long heapMax = mem.getHeapMemoryUsage().getMax();

        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "timestamp", Instant.now().toString(),
                "uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000,
                "memory", Map.of(
                        "heapUsedBytes", heapUsed,
                        "heapMaxBytes", heapMax),
                "version", appVersion,
                "service", appName));
    }

    /** GET /ready — readiness probe */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long heapUsed = mem.getHeapMemoryUsage().getUsed();
        boolean memoryOk = heapUsed < 500L * 1024 * 1024; // 500 MB threshold

        return ResponseEntity.ok(Map.of(
                "status", "ready",
                "timestamp", Instant.now().toString(),
                "checks", Map.of(
                        "server", "up",
                        "memory", memoryOk ? "ok" : "critical")));
    }
}
