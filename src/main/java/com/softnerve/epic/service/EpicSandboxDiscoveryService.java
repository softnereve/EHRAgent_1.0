package com.softnerve.epic.service;

import com.softnerve.epic.model.dto.EpicSandboxIdsDTO;
import reactor.core.publisher.Mono;

public interface EpicSandboxDiscoveryService {
    /** Try multiple Epic FHIR searches and return discovered Patient/Practitioner/Slot IDs. */
    Mono<EpicSandboxIdsDTO> discoverIds();
}
