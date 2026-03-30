package com.slo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Slow routes — mirrors routes/slow/timeout.js
 *
 * GET /slow/timeout → sleeps 2 minutes, then returns 200
 */
@RestController
@RequestMapping("/slow")
public class SlowController {

    private static final Logger log = LoggerFactory.getLogger(SlowController.class);

    /** GET /slow/timeout — 2-minute delay */
    @GetMapping("/timeout")
    public ResponseEntity<String> timeout() throws InterruptedException {
        log.info("⏳ Slow route triggered. Waiting 2 minutes...");
        Thread.sleep(120_000L);
        return ResponseEntity.ok("Response after 2 minutes delay!");
    }
}
