package com.softnerve.epic.service.Implimentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnerve.epic.constant.EpicConstants;
import com.softnerve.epic.model.dto.EpicPractitionerSummaryItem;
import com.softnerve.epic.model.dto.EpicSandboxIdsDTO;
import com.softnerve.epic.service.EpicSandboxDiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EpicSandboxDiscoveryServiceImpl implements EpicSandboxDiscoveryService {

    private static final String COUNT_PARAM = "&_count=20";
    private static final long CACHE_TTL_MS = 10 * 60 * 1000;
    private static final int MIN_PRACTITIONERS_TARGET = 13;
    private static final String CACHE_KEY = "practitioner-discovery";
    private static final List<String> PRACTITIONER_FALLBACK_NAMES = List.of("Smith", "Johnson", "Williams", "Doctor", "Test", "Jones", "Brown", "Davis");

    private final WebClient webClient;
    private final EpicAuthService authService;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CacheEntry> discoveryCache = new ConcurrentHashMap<>();

    public EpicSandboxDiscoveryServiceImpl(WebClient webClient, EpicAuthService authService, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<EpicSandboxIdsDTO> discoverIds() {
        long now = System.currentTimeMillis();
        CacheEntry cached = discoveryCache.get(CACHE_KEY);
        if (cached != null && now < cached.expiresAtMs() && cached.dto() != null && cached.dto().getPractitionerIds() != null
                && cached.dto().getPractitionerIds().size() >= MIN_PRACTITIONERS_TARGET) {
            return Mono.just(cached.dto());
        }
        Set<String> patientIds = new LinkedHashSet<>();
        Set<String> practitionerIds = new LinkedHashSet<>();
        Map<String, EpicPractitionerSummaryItem> practitionerSummariesById = new LinkedHashMap<>();
        Set<String> slotIds = new LinkedHashSet<>();
        List<String> messages = new ArrayList<>();

        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(practitionerToken ->
                        tryPractitionerSearches(practitionerToken, practitionerIds, practitionerSummariesById, messages)
                                .then(Mono.defer(() -> practitionerIds.size() < MIN_PRACTITIONERS_TARGET
                                        ? tryPractitionerFallbackSearches(practitionerToken, practitionerIds, practitionerSummariesById, messages)
                                        : Mono.empty()))
                                .then(Mono.defer(() -> practitionerIds.isEmpty()
                                        ? tryKnownPractitionerIds(practitionerToken, practitionerIds, messages).thenReturn(1)
                                        : Mono.just(1)))
                                .map(ignored -> EpicSandboxIdsDTO.builder()
                                        .patientIds(new ArrayList<>(patientIds))
                                        .practitionerIds(new ArrayList<>(practitionerIds))
                                        .practitionerSummaries(new ArrayList<>(practitionerSummariesById.values()))
                                        .slotIds(new ArrayList<>(slotIds))
                                        .messages(new ArrayList<>(messages))
                                        .build())
                                .onErrorResume(e -> {
                                    messages.add("Auth or search error: " + e.getMessage());
                                    return Mono.just(EpicSandboxIdsDTO.builder()
                                            .patientIds(new ArrayList<>(patientIds))
                                            .practitionerIds(new ArrayList<>(practitionerIds))
                                            .practitionerSummaries(new ArrayList<>(practitionerSummariesById.values()))
                                            .slotIds(new ArrayList<>(slotIds))
                                            .messages(new ArrayList<>(messages))
                                            .build());
                                })
                )
                .doOnSuccess(dto -> {
                    log.info("Discovered from Epic: {} patients, {} practitioners, {} slots",
                            dto.getPatientIds().size(), dto.getPractitionerIds().size(), dto.getSlotIds().size());
                    long ttl = dto.getPractitionerIds().size() >= MIN_PRACTITIONERS_TARGET ? CACHE_TTL_MS : 30_000;
                    discoveryCache.put(CACHE_KEY, new CacheEntry(dto, System.currentTimeMillis() + ttl));
                });
    }

    private record CacheEntry(EpicSandboxIdsDTO dto, long expiresAtMs) {}

    private Mono<Void> tryPractitionerSearches(String token, Set<String> practitionerIds,
                                                Map<String, EpicPractitionerSummaryItem> practitionerSummariesById, List<String> messages) {
        String uri = EpicConstants.FHIR_BASE + "/Practitioner?active=true" + COUNT_PARAM + "&_elements=id,name";
        return callEpicAndCollectPractitioners(token, uri, practitionerIds, practitionerSummariesById, messages);
    }

    private Mono<Void> tryPractitionerFallbackSearches(String token, Set<String> practitionerIds,
                                                       Map<String, EpicPractitionerSummaryItem> practitionerSummariesById, List<String> messages) {
        log.info("Practitioner discovery below target ({}). Running fallback searches...", practitionerIds.size());
        List<Mono<Void>> monos = new ArrayList<>();
        for (String name : PRACTITIONER_FALLBACK_NAMES) {
            String uri = EpicConstants.FHIR_BASE + "/Practitioner?name=" + name + "&_count=50&_elements=id,name";
            monos.add(callEpicAndCollectPractitioners(token, uri, practitionerIds, practitionerSummariesById, messages));
        }
        monos.add(callEpicAndCollectPractitioners(token, EpicConstants.FHIR_BASE + "/Practitioner?family=Smith&_count=50&_elements=id,name", practitionerIds, practitionerSummariesById, messages));
        monos.add(callEpicAndCollectPractitioners(token, EpicConstants.FHIR_BASE + "/Practitioner?given=John&_count=50&_elements=id,name", practitionerIds, practitionerSummariesById, messages));
        return Mono.when(monos).then();
    }

    /** Try reading by ID when search returns nothing (e.g. known sandbox test IDs). */
    private Mono<Void> tryKnownPractitionerIds(String token, Set<String> practitionerIds, List<String> messages) {
        List<String> knownIds = List.of("eYWgiyCoSpNV3B8fN6bqhBw3", "eXzZgWndjVuKaore80K8bLA3");
        List<Mono<Void>> monos = new ArrayList<>();
        for (String id : knownIds) {
            String uri = EpicConstants.FHIR_BASE + "/Practitioner/" + id;
            monos.add(webClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(MediaType.parseMediaType("application/fhir+json"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(body -> {
                        try {
                            JsonNode r = objectMapper.readTree(body);
                            if ("Practitioner".equals(r.path("resourceType").asText(null))) {
                                String rid = r.path("id").asText(null);
                                if (rid != null && !rid.isEmpty()) practitionerIds.add(rid);
                            }
                        } catch (Exception ignored) {}
                    })
                    .then()
                    .onErrorResume(e -> { messages.add("Practitioner/" + id + ": " + e.getMessage()); return Mono.empty(); }));
        }
        return Mono.when(monos).then();
    }

    private Mono<Void> callEpicAndCollectPractitioners(String token, String uri, Set<String> practitionerIds,
                                                        Map<String, EpicPractitionerSummaryItem> practitionerSummariesById, List<String> messages) {
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> {
                    extractIdsFromBundle(body, "Practitioner", practitionerIds);
                    extractPractitionerSummariesFromBundle(body, practitionerSummariesById);
                })
                .then()
                .onErrorResume(e -> {
                    messages.add(uri + " -> " + e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> callEpicAndCollectIds(String token, String uri, String expectedResourceType,
                                             Set<String> ids, List<String> messages) {
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> extractIdsFromBundle(body, expectedResourceType, ids))
                .then()
                .onErrorResume(e -> {
                    messages.add(uri + " -> " + e.getMessage());
                    return Mono.empty();
                });
    }

    /** Extract id + display name from Practitioner entries; use when GET /Practitioner/{id} returns 404. */
    private void extractPractitionerSummariesFromBundle(String fhirJson, Map<String, EpicPractitionerSummaryItem> out) {
        try {
            JsonNode root = objectMapper.readTree(fhirJson);
            JsonNode entries = root.path("entry");
            if (!entries.isArray()) return;
            for (JsonNode entry : entries) {
                JsonNode resource = entry.path("resource");
                if (resource.isMissingNode() || !"Practitioner".equals(resource.path("resourceType").asText(""))) continue;
                String id = resource.path("id").asText(null);
                if (id == null || id.isEmpty()) continue;
                String displayName = null;
                JsonNode nameArr = resource.path("name");
                if (nameArr.isArray() && nameArr.size() > 0) {
                    JsonNode name = nameArr.get(0);
                    displayName = name.path("text").asText(null);
                    if (displayName == null || displayName.isEmpty()) {
                        String family = name.path("family").asText("");
                        JsonNode givenArr = name.path("given");
                        String given = (givenArr.isArray() && givenArr.size() > 0) ? givenArr.get(0).asText("") : "";
                        displayName = (given + " " + family).trim();
                    }
                }
                out.putIfAbsent(id, EpicPractitionerSummaryItem.builder().id(id).displayName(displayName != null && !displayName.isEmpty() ? displayName : id).build());
            }
        } catch (Exception e) {
            log.warn("Failed to parse Practitioner summaries: {}", e.getMessage());
        }
    }

    private void extractIdsFromBundle(String fhirJson, String expectedResourceType, Set<String> ids) {
        try {
            JsonNode root = objectMapper.readTree(fhirJson);
            JsonNode entries = root.path("entry");
            if (!entries.isArray()) return;
            for (JsonNode entry : entries) {
                JsonNode resource = entry.path("resource");
                if (resource.isMissingNode()) continue;
                String type = resource.path("resourceType").asText("");
                String id = resource.path("id").asText(null);
                if (id != null && !id.isEmpty() && expectedResourceType.equals(type)) {
                    ids.add(id);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse Epic bundle: {}", e.getMessage());
        }
    }

    private Mono<EpicSandboxIdsDTO> fetchSlotIds(String token, String practitionerId, EpicSandboxIdsDTO dto) {
        Set<String> slotIds = new LinkedHashSet<>();
        String uri = EpicConstants.FHIR_BASE + "/Slot?practitioner=" + practitionerId + "&status=free";
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> extractIdsFromBundle(body, "Slot", slotIds))
                .then(Mono.fromCallable(() -> {
                    dto.getSlotIds().addAll(slotIds);
                    return dto;
                }))
                .onErrorResume(e -> {
                    dto.getMessages().add("Slots for " + practitionerId + ": " + e.getMessage());
                    return Mono.just(dto);
                });
    }
}
