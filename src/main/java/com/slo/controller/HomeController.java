package com.slo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home route — mirrors the root GET / handler in app.js
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
                <html>
                <head><title>🚀 Spring SLO Testing Application</title></head>
                <body style="font-family:sans-serif;max-width:800px;margin:40px auto;padding:0 20px">
                  <h1>🚀 SLO Testing Application</h1>
                  <p>Enterprise-grade Spring Boot app for testing SLO/SLI with Datadog, New Relic, and Atatus</p>

                  <h2>✅ Success Routes</h2>
                  <ul>
                    <li><a href="/success/200">/success/200</a> – GET success with JSON response</li>
                    <li><a href="/success/accepted">/success/accepted</a> – 202 Accepted</li>
                    <li><a href="/success/delete">/success/delete</a> – DELETE success</li>
                    <li>POST <code>/success/post</code> – Create resource (201)</li>
                    <li>PUT <code>/success/update</code> – Update resource (200)</li>
                  </ul>

                  <h2>❌ Error Routes</h2>
                  <ul>
                    <li><a href="/error/unhandled">/error/unhandled</a> – Throws unhandled exception</li>
                    <li><a href="/error/handled">/error/handled</a> – Returns 500 error</li>
                    <li><a href="/error/async">/error/async</a> – Async deferred error</li>
                    <li><a href="/error/custom-span">/error/custom-span</a> – Custom span error</li>
                    <li><a href="/error/deleteFail">/error/deleteFail</a> – DELETE failure</li>
                    <li><a href="/error/updateFail">/error/updateFail</a> – PUT failure</li>
                    <li>POST <code>/error/json</code> with invalid JSON – JSON parse error (→ 400)</li>
                    <li><a href="/error/status/500">/error/status/:code</a> – Dynamic error by status code (400, 401, 403, 404, 429, 500, 501, 502, 503, 504, ...)</li>
                  </ul>

                  <h2>🌐 External &amp; Slow Routes</h2>
                  <ul>
                    <li><a href="/outgoing/httpbin">/outgoing/httpbin</a> – External HTTP call to httpbin.org</li>
                    <li><a href="/outgoing/httpbin?fail=true">/outgoing/httpbin?fail=true</a> – Simulated external failure</li>
                    <li><a href="/slow/timeout">/slow/timeout</a> – 2-minute delay (timeout test)</li>
                  </ul>

                  <h2>📊 Monitoring Endpoints</h2>
                  <ul>
                    <li><a href="/health">/health</a> – Health check (liveness probe)</li>
                    <li><a href="/ready">/ready</a> – Readiness check</li>
                    <li><a href="/metrics">/metrics</a> – Request counters</li>
                    <li><a href="/actuator/health">/actuator/health</a> – Spring Actuator health</li>
                  </ul>
                </body>
                </html>
                """;
    }
}
