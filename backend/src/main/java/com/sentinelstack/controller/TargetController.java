package com.sentinelstack.controller;

import java.util.List;

import com.sentinelstack.dto.TargetRequest;
import com.sentinelstack.dto.TargetResponse;
import com.sentinelstack.service.TargetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/targets")
public class TargetController {

    private final TargetService targetService;

    public TargetController(TargetService targetService) {
        this.targetService = targetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TargetResponse create(@Valid @RequestBody TargetRequest request) {
        return targetService.create(request);
    }

    @GetMapping
    public List<TargetResponse> list() {
        return targetService.list();
    }

    @GetMapping("/{id}")
    public TargetResponse get(@PathVariable Long id) {
        return targetService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        targetService.delete(id);
    }
}
