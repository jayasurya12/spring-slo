package com.slo.controller;

import com.slo.service.RequestCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * External routes — mirrors routes/external/external-call.js
 *
 * GET /outgoing/httpbin → calls httpbin.org/get
 * GET /outgoing/httpbin?fail=true → calls httpbin.org/status/500 (simulates
 * failure)
 * GET /outgoing/httpbin?method=POST → calls httpbin.org/post
 */
@RestController
@RequestMapping("/outgoing")
public class ExternalController {

    private static final Logger log = LoggerFactory.getLogger(ExternalController.class);
    private final HttpClient httpClient;
    private final RequestCounterService counterService;

    public ExternalController(RequestCounterService counterService) {
        this.counterService = counterService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /** GET /outgoing/httpbin */
    @GetMapping("/httpbin")
    public ResponseEntity<Map<String, Object>> httpbin(
            @RequestParam(defaultValue = "get") String method,
            @RequestParam(defaultValue = "false") String fail) {

        String httpMethod = method.toUpperCase();
        boolean simulateFail = "true".equalsIgnoreCase(fail);

        String url = simulateFail
                ? "https://httpbin.org/status/500"
                : "https://httpbin.org/" + method.toLowerCase();

        log.info("🌐 External {} call to: {}", httpMethod, url);
        counterService.incrementOutgoing();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ResponseEntity.ok(Map.of(
                        "message", "✅ HTTPBIN " + httpMethod + " request successful",
                        "status", response.statusCode()));
            } else {
                return ResponseEntity.status(500).body(Map.of(
                        "message", "❌ HTTPBIN " + httpMethod + " request failed",
                        "status", response.statusCode()));
            }
        } catch (Exception ex) {
            log.error("❌ External call failed: {}", ex.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "message", "❌ HTTPBIN " + httpMethod + " request failed",
                    "error", ex.getMessage()));
        }
    }
}
