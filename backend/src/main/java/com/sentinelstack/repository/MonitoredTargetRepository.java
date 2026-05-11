package com.sentinelstack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sentinelstack.entity.MonitoredTarget;

public interface MonitoredTargetRepository extends JpaRepository<MonitoredTarget, Long> {
}
