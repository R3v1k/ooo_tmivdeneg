package com.sentinelstack.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "check_results")
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private MonitoredTarget target;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(nullable = false)
    private boolean available;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    protected CheckResult() {
    }

    public CheckResult(MonitoredTarget target, Integer statusCode, Integer responseTimeMs, boolean available) {
        this.target = target;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.available = available;
    }

    @PrePersist
    void onCreate() {
        if (checkedAt == null) {
            checkedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public MonitoredTarget getTarget() {
        return target;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public boolean isAvailable() {
        return available;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }
}
