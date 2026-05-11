# Logging

SentinelStack uses structured JSON logs for application events.

## Backend Logs

The Spring Boot backend uses `logback-spring.xml` with `logstash-logback-encoder`.

Each log line includes:

- `timestamp`
- `level`
- `service`
- `instance`
- `message`
- `logger`
- `thread`
- `stack_trace` when an exception occurs

Example:

```json
{"timestamp":"2026-05-11T15:22:00.000Z","level":"INFO","service":"sentinelstack","instance":"app1","message":"Target check completed targetId=1 statusCode=200 responseTimeMs=145 available=true"}
```

Logged events include:

- application startup
- database connection verification
- target creation
- target deletion
- check execution
- failed check requests
- API errors

Show logs:

```bash
docker compose logs app1
docker compose logs app2
```

## NGINX Logs

NGINX access logs use a JSON log format in `nginx/nginx.conf`.

Show logs:

```bash
docker compose logs nginx
```
