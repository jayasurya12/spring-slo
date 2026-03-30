package com.slo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Error routes — mirrors routes/errors/
 *
 * GET /error/unhandled → throws RuntimeException (→ GlobalExceptionHandler →
 * 500)
 * GET /error/handled → explicit 500 response
 * GET /error/async → async exception → 500
 * GET /error/custom-span → throws, caught internally, returns 500
 * GET /error/deleteFail → 500 DELETE failed
 * GET /error/updateFail → 500 PUT failed
 * GET /error/status/{code} → dynamic status code (4xx/5xx)
 * POST /error/json → echoes valid JSON / 400 for malformed
 */
@RestController
@RequestMapping("/error")
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    // All supported HTTP error status codes — mirrors statusError.js
    private static final List<Integer> SUPPORTED_CODES = List.of(
            400, 401, 402, 403, 404, 405, 406, 407, 408, 409,
            410, 411, 412, 413, 414, 415, 416, 417, 418, 422,
            423, 424, 425, 426, 428, 429, 431, 451,
            500, 501, 502, 503, 504, 505, 506, 507, 508, 510, 511);

    private static final Map<Integer, String> STATUS_MESSAGES = Map.ofEntries(
            Map.entry(400, "Bad Request"),
            Map.entry(401, "Unauthorized"),
            Map.entry(402, "Payment Required"),
            Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"),
            Map.entry(405, "Method Not Allowed"),
            Map.entry(406, "Not Acceptable"),
            Map.entry(407, "Proxy Authentication Required"),
            Map.entry(408, "Request Timeout"),
            Map.entry(409, "Conflict"),
            Map.entry(410, "Gone"),
            Map.entry(411, "Length Required"),
            Map.entry(412, "Precondition Failed"),
            Map.entry(413, "Payload Too Large"),
            Map.entry(414, "URI Too Long"),
            Map.entry(415, "Unsupported Media Type"),
            Map.entry(416, "Range Not Satisfiable"),
            Map.entry(417, "Expectation Failed"),
            Map.entry(418, "I'm a Teapot"),
            Map.entry(422, "Unprocessable Entity"),
            Map.entry(423, "Locked"),
            Map.entry(424, "Failed Dependency"),
            Map.entry(425, "Too Early"),
            Map.entry(426, "Upgrade Required"),
            Map.entry(428, "Precondition Required"),
            Map.entry(429, "Too Many Requests"),
            Map.entry(431, "Request Header Fields Too Large"),
            Map.entry(451, "Unavailable For Legal Reasons"),
            Map.entry(500, "Internal Server Error"),
            Map.entry(501, "Not Implemented"),
            Map.entry(502, "Bad Gateway"),
            Map.entry(503, "Service Unavailable"),
            Map.entry(504, "Gateway Timeout"),
            Map.entry(505, "HTTP Version Not Supported"),
            Map.entry(506, "Variant Also Negotiates"),
            Map.entry(507, "Insufficient Storage"),
            Map.entry(508, "Loop Detected"),
            Map.entry(510, "Not Extended"),
            Map.entry(511, "Network Authentication Required"));

    /** GET /error/unhandled — throws, caught by GlobalExceptionHandler → 500 */
    @GetMapping("/unhandled")
    public ResponseEntity<?> unhandled() {
        log.warn("⚠️  Triggering unhandled error");
        throw new RuntimeException("This is an unhandled server error!");
    }

    /** GET /error/handled — explicit 500 */
    @GetMapping("/handled")
    public ResponseEntity<String> handled() {
        log.error("❌ Handled error triggered");
        return ResponseEntity.status(500).body("This is a handled error");
    }

    /** GET /error/async — async deferred result with exception */
    @GetMapping("/async")
    public DeferredResult<ResponseEntity<Map<String, Object>>> async() {
        DeferredResult<ResponseEntity<Map<String, Object>>> result = new DeferredResult<>(5000L);
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(100);
                result.setResult(ResponseEntity.status(500).body(Map.of(
                        "error", "Async Promise-like rejection",
                        "message", "Async error simulated",
                        "timestamp", Instant.now().toString())));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                result.setErrorResult(e);
            }
        });
        return result;
    }

    /** GET /error/custom-span — custom span simulation with caught exception */
    @GetMapping("/custom-span")
    public ResponseEntity<String> customSpan() {
        try {
            // Simulate a custom-span error (no dd-trace SDK in Java, just log the concept)
            log.info("Starting custom.error.operation span");
            throw new RuntimeException("Manual span failure!");
        } catch (RuntimeException ex) {
            log.error("custom-span error: {}", ex.getMessage());
            // In production, instrument with dd-trace Java agent (javaagent JAR)
            return ResponseEntity.status(500).body("Error with custom span!");
        }
    }

    /** GET /error/deleteFail → 500 */
    @GetMapping("/deleteFail")
    public ResponseEntity<Map<String, Object>> deleteFail() {
        log.error("❌ DELETE failure triggered");
        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "error", "DELETE operation failed",
                "timestamp", Instant.now().toString()));
    }

    /** GET /error/updateFail → 500 */
    @GetMapping("/updateFail")
    public ResponseEntity<Map<String, Object>> updateFail() {
        log.error("❌ PUT/UPDATE failure triggered");
        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "error", "PUT operation failed",
                "timestamp", Instant.now().toString()));
    }

    /**
     * GET /error/status/{code}
     * Dynamic status code — supports any code in SUPPORTED_CODES
     */
    @GetMapping("/status/{code}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable int code) {
        if (!SUPPORTED_CODES.contains(code)) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Unsupported status code",
                    "supported", SUPPORTED_CODES));
        }
        String message = STATUS_MESSAGES.getOrDefault(code, "Error");
        log.error("❌ Dynamic status {} triggered: {}", code, message);
        return ResponseEntity.status(code).body(Map.of(
                "statusCode", code,
                "error", message,
                "timestamp", Instant.now().toString()));
    }

    /**
     * POST /error/json
     * Returns 200 echo for valid JSON (Spring returns 400 automatically for
     * malformed JSON)
     */
    @PostMapping("/json")
    public ResponseEntity<Map<String, Object>> json(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "message", "Valid JSON received",
                "body", body != null ? body : Map.of()));
    }
}
