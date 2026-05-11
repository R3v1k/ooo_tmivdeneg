package com.sentinelstack.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sentinel.checks", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledCheckRunner {

    private static final Logger log = LoggerFactory.getLogger(ScheduledCheckRunner.class);

    private final CheckService checkService;

    public ScheduledCheckRunner(CheckService checkService) {
        this.checkService = checkService;
    }

    @Scheduled(fixedDelayString = "${sentinel.checks.fixed-delay-ms:60000}")
    public void run() {
        try {
            checkService.runAllChecks();
        } catch (RuntimeException ex) {
            log.error("Scheduled check execution failed", ex);
        }
    }
}
