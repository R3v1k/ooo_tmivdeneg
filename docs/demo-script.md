# SentinelStack Demo Script

## 1. Show Repository Structure

```bash
tree /F
```

Or show the key folders manually:

```bash
dir
```

## 2. Start Infrastructure

```bash
docker compose up -d --build
```

## 3. Show Running Containers

```bash
docker compose ps
```

Expected services:

- `nginx`
- `app1`
- `app2`
- `postgres`
- `prometheus`
- `grafana`
- `nginx-exporter`
- `postgres-exporter`

## 4. Show Health Endpoint

```bash
curl http://localhost:8080/health
```

Expected:

```json
{"status":"UP"}
```

## 5. Create a Monitoring Target

```bash
curl -X POST http://localhost:8080/targets ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Example\",\"url\":\"https://example.com\"}"
```

PowerShell:

```powershell
curl.exe -X POST http://localhost:8080/targets `
  -H "Content-Type: application/json" `
  -d "{\"name\":\"Example\",\"url\":\"https://example.com\"}"
```

## 6. Run Manual Check

```bash
curl -X POST http://localhost:8080/checks/run/1
```

## 7. Show Latest Results

```bash
curl http://localhost:8080/checks/latest
```

## 8. Show DB Persistence

```bash
docker compose exec postgres psql -U sentinel -d sentinelstack -c "select * from monitored_targets;"
docker compose exec postgres psql -U sentinel -d sentinelstack -c "select * from check_results;"
```

Restart the stack and show records are still present:

```bash
docker compose restart
curl http://localhost:8080/targets
```

## 9. Demonstrate Load Balancing

PowerShell:

```powershell
1..10 | ForEach-Object { curl.exe http://localhost:8080/instance }
```

Windows CMD:

```bat
for /L %i in (1,1,10) do curl http://localhost:8080/instance
```

Expected output alternates between:

```json
{"instance":"app1"}
{"instance":"app2"}
```

## 10. Stop One Backend

```bash
docker compose stop app1
```

## 11. Show Service Still Works

```bash
curl http://localhost:8080/health
curl http://localhost:8080/instance
```

Expected:

```json
{"status":"UP"}
{"instance":"app2"}
```

## 12. Open Prometheus

Open:

```text
http://localhost:9090
```

Useful queries:

```promql
up
up{job="sentinelstack-apps"}
sentinel_checks_success_total
sentinel_checks_failure_total
```

## 13. Open Grafana

Open:

```text
http://localhost:3000
```

Login:

```text
admin / admin
```

Open the `SentinelStack Overview` dashboard.

## 14. Show Logs

```bash
docker compose logs app1
docker compose logs app2
docker compose logs nginx
```

Backend logs are JSON. NGINX access logs are also JSON.

## 15. Run Tests

```bash
mvn -f backend/pom.xml test
```

## 16. Show CI

Open GitHub Actions and show the workflow from `.github/workflows/ci.yml` passing.
