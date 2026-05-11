package com.sentinelstack.metrics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class CheckMetrics {

    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer responseTimer;
    private final AtomicInteger latestAvailability = new AtomicInteger(0);
    private final AtomicLong latestResponseTimeMs = new AtomicLong(0);

    public CheckMetrics(MeterRegistry meterRegistry) {
        this.successCounter = Counter.builder("sentinel_checks_success_total")
                .description("Total successful target checks")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("sentinel_checks_failure_total")
                .description("Total failed target checks")
                .register(meterRegistry);
        this.responseTimer = Timer.builder("sentinel_check_response_time")
                .description("Target check response time")
                .publishPercentileHistogram()
                .register(meterRegistry);
        Gauge.builder("sentinel_latest_availability", latestAvailability, AtomicInteger::get)
                .description("Latest target availability, 1 for available and 0 for unavailable")
                .register(meterRegistry);
        Gauge.builder("sentinel_latest_response_time_ms", latestResponseTimeMs, AtomicLong::get)
                .description("Latest target response time in milliseconds")
                .register(meterRegistry);
    }

    public void record(boolean available, long responseTimeMs) {
        if (available) {
            successCounter.increment();
        } else {
            failureCounter.increment();
        }
        latestAvailability.set(available ? 1 : 0);
        latestResponseTimeMs.set(responseTimeMs);
        responseTimer.record(responseTimeMs, TimeUnit.MILLISECONDS);
    }
}
