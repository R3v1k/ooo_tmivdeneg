package com.sentinelstack.controller;

import java.util.List;

import com.sentinelstack.dto.CheckResultResponse;
import com.sentinelstack.service.CheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checks")
public class CheckController {

    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    @PostMapping("/run/{targetId}")
    public CheckResultResponse run(@PathVariable Long targetId) {
        return checkService.runCheck(targetId);
    }

    @GetMapping("/latest")
    public List<CheckResultResponse> latest() {
        return checkService.latest();
    }
}
