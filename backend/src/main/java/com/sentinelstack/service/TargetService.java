package com.sentinelstack.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import com.sentinelstack.dto.TargetRequest;
import com.sentinelstack.dto.TargetResponse;
import com.sentinelstack.entity.MonitoredTarget;
import com.sentinelstack.exception.InvalidTargetException;
import com.sentinelstack.exception.ResourceNotFoundException;
import com.sentinelstack.repository.MonitoredTargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TargetService {

    private static final Logger log = LoggerFactory.getLogger(TargetService.class);

    private final MonitoredTargetRepository targetRepository;

    public TargetService(MonitoredTargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    @Transactional
    public TargetResponse create(TargetRequest request) {
        String normalizedUrl = normalizeUrl(request.url());
        MonitoredTarget target = new MonitoredTarget(request.name().trim(), normalizedUrl);
        MonitoredTarget saved = targetRepository.save(target);
        log.info("Target created id={} name={} url={}", saved.getId(), saved.getName(), saved.getUrl());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TargetResponse> list() {
        return targetRepository.findAll()
                .stream()
                .map(TargetService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TargetResponse get(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        MonitoredTarget target = findById(id);
        targetRepository.delete(target);
        log.info("Target deleted id={} name={}", target.getId(), target.getName());
    }

    @Transactional(readOnly = true)
    public MonitoredTarget findById(Long id) {
        return targetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Target %d was not found".formatted(id)));
    }

    private static String normalizeUrl(String url) {
        String trimmed = url.trim();
        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!List.of("http", "https").contains(scheme) || uri.getHost() == null) {
                throw new InvalidTargetException("URL must be an absolute http or https URL");
            }
            return uri.toString();
        } catch (URISyntaxException ex) {
            throw new InvalidTargetException("URL is not valid");
        }
    }

    private static TargetResponse toResponse(MonitoredTarget target) {
        return new TargetResponse(target.getId(), target.getName(), target.getUrl(), target.getCreatedAt());
    }
}
