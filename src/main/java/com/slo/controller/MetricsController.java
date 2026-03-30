package com.slo.controller;

import com.slo.service.RequestCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * GET /metrics — returns incoming/outgoing request counters
 * Mirrors the /metrics route in app.js
 */
@RestController
public class MetricsController {

    private final RequestCounterService counterService;

    public MetricsController(RequestCounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> metrics() {
        return ResponseEntity.ok(counterService.getCounts());
    }
}
