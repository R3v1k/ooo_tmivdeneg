package com.sentinelstack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TargetRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 2048) String url) {
}
