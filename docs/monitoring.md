# Monitoring

Prometheus is configured in `prometheus/prometheus.yml`.

## Scrape Targets

| Job | Target | Purpose |
| --- | --- | --- |
| `sentinelstack-apps` | `app1:8080/metrics`, `app2:8080/metrics` | Spring Boot, JVM, HTTP, and custom check metrics |
| `nginx` | `nginx-exporter:9113` | NGINX request and connection metrics |
| `postgres` | `postgres-exporter:9187` | PostgreSQL availability and database metrics |

## Useful PromQL

Backend replicas:

```promql
up{job="sentinelstack-apps"}
```

Request rate:

```promql
sum by (instance) (rate(http_server_requests_seconds_count{application="sentinelstack"}[1m]))
```

HTTP latency p95:

```promql
histogram_quantile(0.95, sum by (le, instance) (rate(http_server_requests_seconds_bucket{application="sentinelstack"}[5m])))
```

Check results:

```promql
sentinel_checks_success_total
sentinel_checks_failure_total
sentinel_latest_availability
sentinel_latest_response_time_ms
```

Database availability:

```promql
pg_up{job="postgres"}
```

NGINX traffic:

```promql
nginx_connections_active
rate(nginx_http_requests_total[1m])
```

## Grafana

Grafana is provisioned automatically:

- Datasource: `grafana/provisioning/datasources/datasource.yml`
- Dashboard provider: `grafana/provisioning/dashboards/dashboard.yml`
- Dashboard JSON: `grafana/dashboards/sentinelstack.json`

Open `http://localhost:3000`, log in with `admin` / `admin`, then open the `SentinelStack Overview` dashboard.
