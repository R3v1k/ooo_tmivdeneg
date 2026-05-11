package com.sentinelstack.dto;

import java.time.Instant;

public record TargetResponse(
        Long id,
        String name,
        String url,
        Instant createdAt) {
}
