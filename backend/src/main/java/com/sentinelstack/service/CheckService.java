package com.sentinelstack.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sentinelstack.dto.CheckResultResponse;
import com.sentinelstack.entity.CheckResult;
import com.sentinelstack.entity.MonitoredTarget;
import com.sentinelstack.exception.ResourceNotFoundException;
import com.sentinelstack.metrics.CheckMetrics;
import com.sentinelstack.repository.CheckResultRepository;
import com.sentinelstack.repository.MonitoredTargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckService {

    private static final Logger log = LoggerFactory.getLogger(CheckService.class);

    private final MonitoredTargetRepository targetRepository;
    private final CheckResultRepository checkResultRepository;
    private final CheckMetrics checkMetrics;
    private final HttpClient httpClient;
    private final Duration timeout;

    public CheckService(
            MonitoredTargetRepository targetRepository,
            CheckResultRepository checkResultRepository,
            CheckMetrics checkMetrics,
            HttpClient httpClient,
            @Value("${sentinel.checks.timeout-ms:10000}") long timeoutMs) {
        this.targetRepository = targetRepository;
        this.checkResultRepository = checkResultRepository;
        this.checkMetrics = checkMetrics;
        this.httpClient = httpClient;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Transactional
    public CheckResultResponse runCheck(Long targetId) {
        MonitoredTarget target = targetRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Target %d was not found".formatted(targetId)));

        long started = System.nanoTime();
        Integer statusCode = null;
        boolean available = false;

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(target.getUrl()))
                    .timeout(timeout)
                    .header("User-Agent", "SentinelStack/1.0")
                    .GET()
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            statusCode = response.statusCode();
            available = statusCode >= 200 && statusCode < 400;
        } catch (IOException ex) {
            log.warn("Target check failed id={} url={} error={}", target.getId(), target.getUrl(), ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Target check interrupted id={} url={}", target.getId(), target.getUrl());
        } catch (IllegalArgumentException ex) {
            log.warn("Target check rejected invalid URI id={} url={} error={}", target.getId(), target.getUrl(), ex.getMessage());
        }

        long elapsedMs = Duration.ofNanos(System.nanoTime() - started).toMillis();
        CheckResult result = checkResultRepository.save(new CheckResult(
                target,
                statusCode,
                Math.toIntExact(Math.min(elapsedMs, Integer.MAX_VALUE)),
                available));
        checkMetrics.record(available, elapsedMs);
        log.info(
                "Target check completed targetId={} statusCode={} responseTimeMs={} available={}",
                target.getId(),
                statusCode,
                elapsedMs,
                available);
        return toResponse(result);
    }

    @Transactional
    public void runAllChecks() {
        targetRepository.findAll().forEach(target -> runCheck(target.getId()));
    }

    @Transactional(readOnly = true)
    public List<CheckResultResponse> latest() {
        Map<Long, CheckResult> latestByTarget = new LinkedHashMap<>();
        for (CheckResult result : checkResultRepository.findNewestFirst()) {
            latestByTarget.putIfAbsent(result.getTarget().getId(), result);
        }
        return latestByTarget.values()
                .stream()
                .map(CheckService::toResponse)
                .toList();
    }

    private static CheckResultResponse toResponse(CheckResult result) {
        MonitoredTarget target = result.getTarget();
        return new CheckResultResponse(
                result.getId(),
                target.getId(),
                target.getName(),
                target.getUrl(),
                result.getStatusCode(),
                result.getResponseTimeMs(),
                result.isAvailable(),
                result.getCheckedAt());
    }
}
