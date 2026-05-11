# SentinelStack

SentinelStack is a containerized uptime monitoring platform for a System and Network Administration final project. It uses Spring Boot, PostgreSQL, NGINX, Prometheus, Grafana, Docker Compose, structured JSON logs, health checks, and GitHub Actions CI.

## Architecture

```text
User / curl
    |
    v
NGINX :8080
    |
    +---- app1 :8080
    |
    +---- app2 :8080
          |
          v
      PostgreSQL

Prometheus scrapes app1, app2, NGINX exporter, and Postgres exporter.
Grafana loads a provisioned SentinelStack dashboard from Prometheus.
```

## Quick Start

```bash
docker compose up -d --build
docker compose ps
```

Main entry points:

- API through NGINX: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` with `admin` / `admin`

## API

```bash
curl http://localhost:8080/health
curl http://localhost:8080/metrics
curl http://localhost:8080/instance
```

Create a target:

```bash
curl -X POST http://localhost:8080/targets \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Example\",\"url\":\"https://example.com\"}"
```

List targets:

```bash
curl http://localhost:8080/targets
```

Run a manual check:

```bash
curl -X POST http://localhost:8080/checks/run/1
```

Show latest check results:

```bash
curl http://localhost:8080/checks/latest
```

Delete a target:

```bash
curl -X DELETE http://localhost:8080/targets/1
```

## Load Balancing Demo

Run repeated requests through NGINX:

```bash
for /L %i in (1,1,10) do curl http://localhost:8080/instance
```

PowerShell version:

```powershell
1..10 | ForEach-Object { curl.exe http://localhost:8080/instance }
```

Stop one backend and repeat:

```bash
docker compose stop app1
curl http://localhost:8080/health
curl http://localhost:8080/instance
```

The service should continue responding through `app2`.

## Local Backend Tests

```bash
mvn -f backend/pom.xml test
```

## Important Files

- `backend/` - Spring Boot application
- `docker-compose.yml` - complete container stack
- `nginx/conf.d/default.conf` - reverse proxy and load balancing
- `prometheus/prometheus.yml` - scrape configuration
- `grafana/dashboards/sentinelstack.json` - provisioned dashboard
- `.github/workflows/ci.yml` - CI pipeline
- `docs/demo-script.md` - presentation script
- `REPORT.md` - final project report
