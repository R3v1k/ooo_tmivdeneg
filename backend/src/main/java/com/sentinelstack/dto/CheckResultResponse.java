package com.sentinelstack.dto;

import java.time.Instant;

public record CheckResultResponse(
        Long id,
        Long targetId,
        String targetName,
        String targetUrl,
        Integer statusCode,
        Integer responseTimeMs,
        boolean available,
        Instant checkedAt) {
}
