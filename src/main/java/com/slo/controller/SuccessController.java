package com.slo.controller;

import com.slo.service.RequestCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * Success routes — mirrors routes/success/
 *
 * GET /success/200 → 200 JSON
 * GET /success/accepted → 202 JSON
 * GET /success/delete → 200 JSON (DELETE-success simulation)
 * POST /success/post → 201 Created
 * PUT /success/update → 200 Updated
 */
@RestController
@RequestMapping("/success")
public class SuccessController {

    private final RequestCounterService counterService;

    public SuccessController(RequestCounterService counterService) {
        this.counterService = counterService;
    }

    /** GET /success/200 */
    @GetMapping("/200")
    public ResponseEntity<Map<String, Object>> get200() {
        counterService.incrementOutgoing();
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "✅ GET success",
                "timestamp", Instant.now().toString(),
                "data", Map.of("id", 1, "name", "SLO Test Resource")));
    }

    /** GET /success/accepted → 202 */
    @GetMapping("/accepted")
    public ResponseEntity<Map<String, Object>> accepted() {
        counterService.incrementOutgoing();
        return ResponseEntity.accepted().body(Map.of(
                "status", 202,
                "message", "✅ Request accepted for processing",
                "timestamp", Instant.now().toString()));
    }

    /** GET /success/delete → 200 */
    @GetMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete() {
        counterService.incrementOutgoing();
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "✅ Resource deleted successfully",
                "timestamp", Instant.now().toString()));
    }

    /** POST /success/post → 201 Created */
    @PostMapping("/post")
    public ResponseEntity<Map<String, Object>> post(@RequestBody(required = false) Map<String, Object> body) {
        counterService.incrementOutgoing();
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "✅ Resource created",
                "timestamp", Instant.now().toString(),
                "received", body != null ? body : Map.of()));
    }

    /** PUT /success/update → 200 */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody(required = false) Map<String, Object> body) {
        counterService.incrementOutgoing();
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "✅ Resource updated",
                "timestamp", Instant.now().toString(),
                "received", body != null ? body : Map.of()));
    }
}
