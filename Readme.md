# 🚀 Spring SLO Testing Application

Enterprise-grade **Java Spring Boot** application for testing Service Level Objectives (SLOs) and Service Level Indicators (SLIs) with **Datadog**, **New Relic**, and **Atatus** APM tools.

> Migrated from `node-slo` (Node.js/Express) — feature-for-feature port.

## ✨ Features

- **All node-slo routes ported**: success, error, external, slow, health, metrics
- **Dynamic status codes**: `/error/status/{code}` supports all 4xx/5xx codes
- **Thread-safe counters**: incoming/outgoing request tracking
- **Spring Actuator**: built-in `/actuator/health` and more
- **Datadog-ready**: configure with `DD_SERVICE`, `DD_AGENT_HOST` env vars + javaagent

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+ (or use `./mvnw` wrapper)

### Run

```bash
cd spring-slo

# Option 1: simple
./mvnw spring-boot:run

# Option 2: source .env then run
./setup-and-run.sh

# With custom port
PORT=9090 ./mvnw spring-boot:run
```

App available at `http://localhost:8080`

## 🔧 Environment Variables

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8080` | HTTP port |
| `DD_SERVICE` | `spring-slo` | Datadog service name |
| `DD_ENV` | — | Datadog environment |
| `DD_VERSION` | — | Datadog version tag |
| `DD_TRACE_AGENT_HOSTNAME` | `localhost` | Datadog agent host |
| `DD_TRACE_AGENT_PORT` | `8126` | Datadog agent APM port |

## 📡 API Endpoints

### ✅ Success Routes

| Endpoint | Method | Description |
|---|---|---|
| `/success/200` | GET | 200 OK with JSON |
| `/success/accepted` | GET | 202 Accepted |
| `/success/delete` | GET | 200 DELETE success |
| `/success/post` | POST | 201 Created |
| `/success/update` | PUT | 200 Updated |

### ❌ Error Routes

| Endpoint | Method | Description |
|---|---|---|
| `/error/unhandled` | GET | Throws RuntimeException → 500 |
| `/error/handled` | GET | Explicit 500 |
| `/error/async` | GET | Async deferred 500 |
| `/error/custom-span` | GET | Custom span exception → 500 |
| `/error/deleteFail` | GET | 500 DELETE failed |
| `/error/updateFail` | GET | 500 PUT failed |
| `/error/status/{code}` | GET | Dynamic 4xx/5xx status |
| `/error/json` | POST | Echo JSON (400 for malformed) |

### 🌐 External & Slow Routes

| Endpoint | Method | Description |
|---|---|---|
| `/outgoing/httpbin` | GET | External call to httpbin.org |
| `/outgoing/httpbin?fail=true` | GET | Simulated external failure → 500 |
| `/slow/timeout` | GET | 2-minute delay |

### 📊 Monitoring Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/health` | GET | Liveness probe |
| `/ready` | GET | Readiness probe |
| `/metrics` | GET | Request counters |
| `/actuator/health` | GET | Spring Actuator health |

## 🧪 Load Testing

```bash
# Test all endpoints
./load-simulator.sh all

# Test only success routes
./load-simulator.sh success

# Run load test (10 RPS for 60 seconds)
./load-simulator.sh load 60 10

# Against a different host
SLO_HOST=http://staging:8080 ./load-simulator.sh all
```

## 🔍 Datadog APM (Java Agent)

```bash
# Download dd-java-agent
wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'

# Run with agent
DD_SERVICE=spring-slo DD_ENV=dev DD_VERSION=1.0.0 \
  java -javaagent:dd-java-agent.jar \
  -jar target/spring-slo-1.0.0.jar
```

## 📁 Project Structure

```
spring-slo/
├── pom.xml
├── .env
├── setup-and-run.sh
├── load-simulator.sh
└── src/main/java/com/slo/
    ├── SloApplication.java
    ├── config/
    │   └── WebConfig.java
    ├── controller/
    │   ├── HomeController.java
    │   ├── SuccessController.java
    │   ├── ErrorController.java
    │   ├── ExternalController.java
    │   ├── SlowController.java
    │   ├── MetricsController.java
    │   └── HealthController.java
    ├── exception/
    │   └── GlobalExceptionHandler.java
    ├── interceptor/
    │   └── RequestCounterInterceptor.java
    └── service/
        └── RequestCounterService.java
```
