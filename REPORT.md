# SentinelStack Final Report

## Project Summary

SentinelStack is a Spring Boot uptime monitoring platform packaged as a Docker Compose infrastructure. It demonstrates backend replicas, NGINX load balancing, PostgreSQL persistence, Prometheus metrics, Grafana dashboards, JSON logs, health checks, and CI verification.

## Implemented Requirements

| Requirement | Implementation |
| --- | --- |
| Docker Compose startup | `docker compose up -d --build` starts the full stack |
| Spring Boot backend | Java 21 Spring Boot app in `backend/` |
| PostgreSQL persistence | `postgres` service with named `postgres_data` volume |
| NGINX reverse proxy | `nginx` service listens on host port `8080` |
| Two backend replicas | `app1` and `app2` services share one backend image |
| Health endpoint | `/health` and `/actuator/health` |
| Metrics endpoint | `/metrics` forwards Prometheus metrics |
| Prometheus and Grafana | Services and provisioning included |
| Docker health checks | Backend, Postgres, and NGINX health checks |
| Structured JSON logs | Logback JSON encoder for backend; JSON access logs for NGINX |
| CI/CD | GitHub Actions workflow runs tests, Docker build, Compose validation |
| Fault tolerance demo | Stop `app1`; NGINX continues serving through `app2` |

## Backend Design

The backend exposes a small REST API:

- `GET /health`
- `GET /metrics`
- `GET /instance`
- `POST /targets`
- `GET /targets`
- `GET /targets/{id}`
- `DELETE /targets/{id}`
- `POST /checks/run/{targetId}`
- `GET /checks/latest`

The application stores monitored targets and check results using Spring Data JPA. Flyway creates the database schema before Hibernate validates it, which avoids schema creation races when both replicas start together. Manual checks use Java `HttpClient`, record status code, response time, availability, and a timestamp. A scheduled runner also checks all configured targets every 60 seconds by default.

## Database Design

`monitored_targets` stores configured services:

- `id`
- `name`
- `url`
- `created_at`

`check_results` stores uptime checks:

- `id`
- `target_id`
- `status_code`
- `response_time_ms`
- `available`
- `checked_at`

## Monitoring

Prometheus scrapes:

- `app1:8080/metrics`
- `app2:8080/metrics`
- `nginx-exporter:9113`
- `postgres-exporter:9187`

Grafana provisions a dashboard named `SentinelStack Overview` with panels for backend health, request rate, latency, JVM memory, JVM threads, error rate, check success/failure, NGINX traffic, and database availability.

## Logging

Backend logs are emitted as JSON with timestamp, level, service, instance, message, logger, and thread fields. The code logs startup, database connection verification, target creation, target deletion, check execution, failed checks, and API errors.

## CI/CD

The GitHub Actions workflow:

1. Checks out the repository.
2. Sets up Java 21.
3. Caches Maven dependencies.
4. Runs backend tests.
5. Builds the backend Docker image.
6. Validates `docker-compose.yml`.

## Demo Scenario

The demo starts the full stack, creates a target, runs a manual check, shows persisted results, sends repeated `/instance` requests through NGINX, stops `app1`, and confirms the platform still responds through `app2`. Prometheus and Grafana show the infrastructure state during the failure.
