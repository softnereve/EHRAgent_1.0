package com.softnerve.epic.configuration;

import com.softnerve.epic.service.EpicSandboxDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpicStartupWarmup {

    private final EpicSandboxDiscoveryService epicSandboxDiscoveryService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmupEpicPractitioners() {
        epicSandboxDiscoveryService.discoverIds()
                .doOnSuccess(dto -> log.info("Startup warmup complete. practitioners={}", dto.getPractitionerIds() != null ? dto.getPractitionerIds().size() : 0))
                .doOnError(e -> log.warn("Startup warmup failed: {}", e.getMessage()))
                .subscribe();
    }
}
