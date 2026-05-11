package com.sentinelstack.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    private final String instanceName;

    public InstanceController(@Value("${sentinel.instance-name:local}") String instanceName) {
        this.instanceName = instanceName;
    }

    @GetMapping("/instance")
    public Map<String, String> instance() {
        return Map.of("instance", instanceName);
    }
}
