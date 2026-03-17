package com.softnerve.epic.service.Implimentation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.mongodb.client.MongoIterable;
import com.softnerve.epic.constant.EpicConstants;
import com.softnerve.epic.exception.EpicClientException;
import com.softnerve.epic.exception.EpicDataMappingException;
import com.softnerve.epic.model.dao.PractitionerDocument;
import com.softnerve.epic.model.dto.*;
import com.softnerve.epic.repo.PractitionerRepository;
import com.softnerve.epic.service.CounterService;
import com.softnerve.epic.service.EpicPractitionerService;
import com.softnerve.epic.service.EpicSandboxDiscoveryService;
import com.softnerve.epic.utils.PractitionerMapper;
import dev.softnerve.annotation.IntentParam;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Slot;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import dev.softnerve.annotation.IntentDefinition;
import dev.softnerve.annotation.IntentService;
import dev.softnerve.exception.IntentErrorType;
import dev.softnerve.exception.IntentHandlingException;
import dev.softnerve.model.Audience;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@IntentService
public class EpicPractitionerServiceIMPL implements EpicPractitionerService {

    private final WebClient webClient;
    private final EpicAuthService authService;
    private final PractitionerRepository practitionerRepository;
    private final CounterService counterService;
    private final FhirContext fhirContext = FhirContext.forR4();
    private final EpicSandboxDiscoveryService epicSandboxDiscoveryService;
    private final EpicSandboxDiscoveryService sandboxDiscoveryService;


    public EpicPractitionerServiceIMPL(WebClient webClient, EpicAuthService authService,
                                       PractitionerRepository practitionerRepository,
                                       CounterService counterService, EpicSandboxDiscoveryService epicSandboxDiscoveryService, EpicSandboxDiscoveryService sandboxDiscoveryService) {
        this.webClient = webClient;
        this.authService = authService;
        this.practitionerRepository = practitionerRepository;
        this.counterService = counterService;
        this.epicSandboxDiscoveryService = epicSandboxDiscoveryService;
        this.sandboxDiscoveryService = sandboxDiscoveryService;
    }
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final List<String> QUALIFICATIONS = Arrays.asList("MD", "MBBS");
    private final List<String> SPECIALTIES = Arrays.asList("Cardiology", "Dermatology", "Pediatrics", "Neurology", "Orthopedics", "General Medicine");

    private JsonNode parseJson(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

//    @IntentDefinition(
//            audience = Audience.PATIENT,
//            name = "Get_Doctor",
//            description = "Fetch doctor details from epic"
//    )
    @Override
    public Mono<DoctorDTO> getDoctorById(String id) {
        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(token -> fetchPractitionerByRead(token, id)
                        .onErrorResume(EpicClientException.class, e -> e.getStatusCode() == 404
                                ? fetchPractitionerBySearch(token, id)
                                : Mono.error(e)))
                .map(this::parsePractitioner)
                .map(practitionerDto -> {
                    String qualification = extractQualificationFromDto(practitionerDto);
                    String email = findTelecomValue(practitionerDto, "email");
                    String phone = findTelecomValue(practitionerDto, "phone");
                    String fullName = buildFullName(practitionerDto);

                    if (qualification == null || qualification.isBlank()) qualification = getRandomQualification();
                    if (email == null || email.isBlank()) email = getRandomEmail(fullName);
                    if (phone == null || phone.isBlank()) phone = getRandomPhone();

                    return DoctorDTO.builder()
                        .id(practitionerDto.getId())
                        .fullName(fullName)
                        .qualification(qualification)
                        .email(email)
                        .phone(phone)
                        .specialization(getRandomSpecialty())
                        .isFromEpic(true)
                        .build();
                });
    }

    private String buildFullName(PractitionerDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().isEmpty() || dto.getName().get(0) == null) {
            return "Unknown";
        }
        String given = dto.getName().get(0).getGiven() != null
                ? dto.getName().get(0).getGiven().stream().collect(Collectors.joining(" "))
                : "";
        String family = dto.getName().get(0).getFamily() != null ? dto.getName().get(0).getFamily() : "";
        String fullName = (given + " " + family).trim();
        return fullName.isEmpty() ? "Unknown" : fullName;
    }

    private String extractQualificationFromDto(PractitionerDTO dto) {
        if (dto == null || dto.getQualification() == null || dto.getQualification().isEmpty()) {
            return null;
        }

        for (QualificationDTO q : dto.getQualification()) {
            if (q == null || q.getCode() == null) {
                continue;
            }
            if (q.getCode().getText() != null && !q.getCode().getText().isBlank()) {
                return q.getCode().getText();
            }
            if (q.getCode().getCoding() != null) {
                for (CodingDTO c : q.getCode().getCoding()) {
                    if (c == null) {
                        continue;
                    }
                    if (c.getDisplay() != null && !c.getDisplay().isBlank()) {
                        return c.getDisplay();
                    }
                    if (c.getCode() != null && !c.getCode().isBlank()) {
                        return c.getCode();
                    }
                }
            }
        }
        return null;
    }

    private String findTelecomValue(PractitionerDTO dto, String system) {
        if (dto == null || dto.getTelecom() == null || dto.getTelecom().isEmpty()) {
            return null;
        }
        for (TelecomDTO telecom : dto.getTelecom()) {
            if (telecom == null) {
                continue;
            }
            if (telecom.getSystem() != null && telecom.getSystem().equalsIgnoreCase(system)) {
                if (telecom.getValue() != null && !telecom.getValue().isBlank()) {
                    return telecom.getValue();
                }
            }
        }
        return null;
    }

    /** Epic read by ID (often 404 in sandbox). */
    private Mono<String> fetchPractitionerByRead(String token, String id) {
        return webClient.get()
                .uri(EpicConstants.FHIR_BASE + "/Practitioner/{id}?_elements=name,telecom,qualification", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .onStatus(status -> status.value() == 404, resp -> resp.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new EpicClientException(404, "Practitioner not found in Epic."))))
                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                .bodyToMono(String.class);
    }

    /** Fallback: Epic search by _id when read returns 404 (sandbox quirk). */
    private Mono<String> fetchPractitionerBySearch(String token, String id) {
        return webClient.get()
                .uri(EpicConstants.FHIR_BASE + "/Practitioner?_id={id}&_elements=name,telecom,qualification", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                .bodyToMono(String.class)
                .flatMap(bundleJson -> extractFirstPractitionerFromBundle(bundleJson))
                .switchIfEmpty(Mono.error(new EpicClientException(404, "Practitioner not found in Epic.")));
    }

    private Mono<String> extractFirstPractitionerFromBundle(String bundleJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, bundleJson);
            return bundle.getEntry().stream()
                    .filter(e -> e.getResource() instanceof Practitioner)
                    .findFirst()
                    .map(e -> parser.encodeResourceToString(e.getResource()))
                    .map(Mono::just)
                    .orElse(Mono.empty());
        } catch (Exception e) {
            return Mono.error(new EpicDataMappingException("Failed to parse Practitioner from search bundle", e));
        }
    }

    // @IntentDefinition(
    //         audience = Audience.PATIENT,
    //         name = "Get_Available_Slots",
    //         description = "Fetch available slots for a doctor on a specific date"
    // )
    @Override
    public Mono<SlotBundleDTO> getAvailableSlots(String doctorId, LocalDate date) {
        log.info("Generating pure dummy slots for doctor {} on {}. (Bypassing Epic)", doctorId, date);
        return Mono.just(SlotBundleDTO.builder().slots(generateDummySlots(doctorId, date)).build());
    }

    private List<SlotDTO> generateDummySlots(String practitionerId, LocalDate date) {
        List<SlotDTO> dummySlots = new ArrayList<>();
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime start = date.atTime(9, 0).atOffset(zoneOffset);
        OffsetDateTime endLimit = date.atTime(18, 0).atOffset(zoneOffset);

        int i = 0;
        while (start.isBefore(endLimit)) {
            OffsetDateTime end = start.plusMinutes(30);

            dummySlots.add(SlotDTO.builder()
                    .id("dummy-slot-" + practitionerId + "-" + i)
                    .start(start.toString())
                    .end(end.toString())
                    .status("free")
                    .scheduleId("dummy-schedule-" + practitionerId)
                    .build());

            start = end;
            i++;
        }
        return dummySlots;
    }

    private SlotBundleDTO parseSlotBundle(String json) {
        IParser parser = fhirContext.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, json);
        return SlotBundleDTO.builder().slots(bundle.getEntry().stream()
                .flatMap(e -> {
                    if (e.getResource() instanceof Slot) {
                        return java.util.stream.Stream.of((Slot) e.getResource());
                    }
                    return java.util.stream.Stream.empty();
                })
                .map(s -> SlotDTO.builder()
                        .id(s.getIdElement().getIdPart())
                        .start(s.getStart().toInstant().atOffset(ZoneOffset.UTC).toString())
                        .end(s.getEnd().toInstant().atOffset(ZoneOffset.UTC).toString())
                        .status(s.getStatus().toCode())
                        .build())
                .collect(Collectors.toList())).build();
    }

    // @IntentDefinition(
    //         audience = Audience.PATIENT,
    //         name = "Get_Practitioner_By_Id",
    //         description = "Fetch practitioner details by ID from Epic"
    // )
    @Override
    public Mono<PractitionerSummaryDTO> getPractitionerById(String id) {
        log.info("➡️ Starting practitioner fetch process. id={}", id);

        Mono<PractitionerDTO> practitionerMono =
                authService.getAccessToken("system/Practitioner.read")
                        .doOnSuccess(token -> log.info("🔑 Successfully obtained access token for Practitioner.read"))
                        .flatMap(token -> {
                            log.info("🌐 Fetching Practitioner resource from Epic...");
                            return fetchPractitionerByRead(token, id)
                                    .onErrorResume(EpicClientException.class, e -> {
                                        if (e.getStatusCode() == 404) {
                                            log.info("🌐 Read 404, falling back to Practitioner?_id={}", id);
                                            return fetchPractitionerBySearch(token, id);
                                        }
                                        return Mono.error(e);
                                    });
                        })
                        .map(this::parsePractitioner)
                        .doOnSuccess(dto -> log.info("✅ Successfully parsed Practitioner resource. id={}", dto.getId()));

        Mono<List<PractitionerRoleDTO>> rolesMono =
                getPractitionerRoles(id)
                        .defaultIfEmpty(java.util.List.of())
                        .doOnSuccess(r -> log.info("🧩 Roles fetch completed for practitionerId={}, count={}", id, (r != null ? r.size() : 0)))
                        .onErrorReturn(java.util.List.of());

        return Mono.zip(practitionerMono, rolesMono)
                .flatMap(tuple -> {
                    PractitionerDTO practitionerDto = tuple.getT1();
                    List<PractitionerRoleDTO> roles = tuple.getT2();
                    List<PractitionerRoleDTO> safeRoles = (roles != null) ? roles : java.util.List.of();
                    return findOrCreateDoctorId(practitionerDto.getId())
                            .map(doctorId -> {
                                persistPractitionerAsync(practitionerDto, safeRoles, doctorId);
                                PractitionerSummaryDTO summary = PractitionerMapper.toSummaryDto(practitionerDto, safeRoles, doctorId);
                                log.info("🎯 Final Practitioner Summary DTO ready. id={}, doctorId={}, roles={}", practitionerDto.getId(), doctorId, safeRoles.size());
                                return summary;
                            });
                })
                .doOnSuccess(finalResult -> log.info("🏁 Fetch process complete for id={}", id))
                .doOnError(err -> log.error("💥 Critical error in getPractitionerById: {}", err.getMessage()));
    }

    // @IntentDefinition(
    //         audience = Audience.PATIENT,
    //         name = "Get_Practitioner_Roles",
    //         description = "Fetch roles for a practitioner"
    // )
    @Override
    public Mono<List<PractitionerRoleDTO>> getPractitionerRoles(String practitionerId) {
        log.info("🌐 Multi-strategy search for PractitionerRoles. Id={}", practitionerId);
        
        return authService.getAccessToken("system/PractitionerRole.read")
                .flatMap(token -> {
                    // Strategy 1: Search by ID (no prefix)
                    Mono<String> search1 = webClient.get()
                            .uri(EpicConstants.FHIR_BASE + "/PractitionerRole?practitioner={id}", practitionerId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .accept(MediaType.parseMediaType("application/fhir+json"))
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(e -> {
                                log.warn("⚠️ Strategy 1 (practitioner={}) failed: {}", practitionerId, e.getMessage());
                                return Mono.empty();
                            });

                    // Strategy 2: Search by Practitioner/ID (prefixed)
                    Mono<String> search2 = webClient.get()
                            .uri(EpicConstants.FHIR_BASE + "/PractitionerRole?practitioner=Practitioner/{id}", practitionerId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .accept(MediaType.parseMediaType("application/fhir+json"))
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(e -> {
                                log.warn("⚠️ Strategy 2 (practitioner=Practitioner/{}) failed: {}", practitionerId, e.getMessage());
                                return Mono.empty();
                            });

                    return Mono.zip(
                            search1.defaultIfEmpty(""),
                            search2.defaultIfEmpty("")
                    ).map(tuple -> {
                        String res1 = tuple.getT1();
                        String res2 = tuple.getT2();
                        
                        List<PractitionerRoleDTO> allRoles = new ArrayList<>();
                        if (!res1.isEmpty()) {
                            log.info("📄 Strategy 1 Raw JSON: {}", res1);
                            allRoles.addAll(parsePractitionerRoleBundle(res1));
                        }
                        if (!res2.isEmpty()) {
                            log.info("📄 Strategy 2 Raw JSON: {}", res2);
                            List<PractitionerRoleDTO> roles2 = parsePractitionerRoleBundle(res2);
                            // Avoid duplicates
                            roles2.stream()
                                    .filter(r2 -> allRoles.stream().noneMatch(r1 -> r1.getId().equals(r2.getId())))
                                    .forEach(allRoles::add);
                        }
                        log.info("✅ Total {} unique roles found across all strategies.", allRoles.size());
                        return allRoles;
                    });
                });
    }

     @IntentDefinition(
             audience = Audience.PATIENT,
             name = "Search_Practitioners",
             description = "Search practitioners by name or specialty"
     )
    @Override
    public Mono<List<PractitionerSummaryDTO>> searchPractitioners(@IntentParam(name = "name") String name, String specialty) {
        log.info("➡️ Searching Practitioners in Epic. name={}, specialty={}", name, specialty);

        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(token -> webClient.get()
                        .uri(EpicConstants.FHIR_BASE + "/Practitioner" + (name != null && !name.isEmpty() ? "?name=" + name : ""))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.parseMediaType("application/fhir+json"))
                        .retrieve()
                        .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                        .bodyToMono(String.class)
                        .map(this::parsePractitionerBundle)
                        .doOnSuccess(list -> log.info("🏁 Tracking data: Found {} practitioners for search name: {}", (list != null ? list.size() : 0), name)))
                .onErrorMap(e -> !(e instanceof EpicClientException) ? new IntentHandlingException(IntentErrorType.EXECUTION_FAILED, "Error in searchPractitioners intent", e) : e);
    }

    private List<PractitionerSummaryDTO> parsePractitionerBundle(String fhirJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, fhirJson);
            return bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof Practitioner)
                    .map(entry -> (Practitioner) entry.getResource())
                    .map(PractitionerMapper::toPractitionerDto)
                    .map(dto -> PractitionerMapper.toSummaryDto(dto, List.of(), "PENDING"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("❌ Failed to parse Practitioner bundle: {}", e.getMessage());
            throw new EpicDataMappingException("Failed to parse Practitioner bundle", e);
        }
    }

    private PractitionerDTO parsePractitioner(String fhirJson) {
        log.info("📄 Raw Practitioner JSON: {}", fhirJson);
        try {
            IParser parser = fhirContext.newJsonParser();
            Practitioner practitioner = parser.parseResource(Practitioner.class, fhirJson);
            PractitionerDTO dto = PractitionerMapper.toPractitionerDto(practitioner);
            log.info("✅ Parsed Practitioner DTO: specialties={}, telecom={}",
                    dto.getQualification() != null ? dto.getQualification().size() : 0,
                    dto.getTelecom() != null ? dto.getTelecom().size() : 0);
            return dto;
        } catch (Exception e) {
            log.error("❌ Failed to parse Practitioner resource: {}", e.getMessage());
            throw new EpicDataMappingException("Failed to parse Practitioner resource", e);
        }
    }

    private List<PractitionerRoleDTO> parsePractitionerRoleBundle(String fhirJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, fhirJson);
            List<PractitionerRoleDTO> roles = bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof PractitionerRole)
                    .map(entry -> (PractitionerRole) entry.getResource())
                    .map(PractitionerMapper::toPractitionerRoleDto)
                    .collect(Collectors.toList());
            log.info("✅ Parsed {} PractitionerRoles from bundle", roles.size());
            return roles;
        } catch (Exception e) {
            log.error("❌ Failed to parse PractitionerRole bundle: {}", e.getMessage());
            throw new EpicDataMappingException("Failed to parse PractitionerRole bundle", e);
        }
    }

    private Mono<String> findOrCreateDoctorId(String epicId) {
        return Mono.fromCallable(() -> practitionerRepository.findByEpicPractitionerId(epicId))
                .flatMap(opt -> opt.map(doc -> Mono.just(doc.getId()))
                        .orElseGet(() -> Mono.just(counterService.getNextDoctorId())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void persistPractitionerAsync(PractitionerDTO dto, List<PractitionerRoleDTO> roles, String doctorId) {
        Mono.fromCallable(() -> {
            PractitionerDocument doc = PractitionerMapper.toDocument(dto, roles, doctorId);
            return practitionerRepository.save(doc);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnSuccess(saved -> log.info("✅ Practitioner persisted in MongoDB. doctorId={}", doctorId))
        .doOnError(err -> log.error("❌ Failed to persist practitioner in MongoDB: {}", err.getMessage()))
        .subscribe();
    }


    @Override
    public Mono<List<DoctorDTO>> getPractitionersByIds(
        @IntentParam(name = "ids") List<String> ids) {
        log.info("📥 Batch fetching {} doctors from Epic", ids.size());

        return reactor.core.publisher.Flux.fromIterable(ids)
                .flatMap(id -> getDoctorById(id)
                        .onErrorResume(e -> {
                            log.warn("⚠️ Failed to fetch doctor {}: {}", id, e.getMessage());
                            return Mono.empty();
                        }),
                        3  // concurrency limit to avoid rate-limiting
                )
                .collectList()
                .doOnSuccess(list -> log.info("✅ Batch fetch complete. Retrieved {}/{} doctors", list.size(), ids.size()));
    }
    @IntentDefinition(
            audience = Audience.PATIENT,
            name = "Get_Practitioners_List",
            description = "Batch fetch details for multiple practitioners"
    )
    @Override
    public Mono<List<DoctorDTO>> getAllDoctorsFromSandbox() {
        log.info("🔍 Tracking data: Discovering all doctors from sandbox");
        java.util.concurrent.atomic.AtomicLong start = new java.util.concurrent.atomic.AtomicLong(System.currentTimeMillis());
        java.util.concurrent.atomic.AtomicLong discoveryMs = new java.util.concurrent.atomic.AtomicLong(0);
        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(token -> sandboxDiscoveryService.discoverIds()
                        .flatMap(dto -> {
                            discoveryMs.set(System.currentTimeMillis() - start.get());
                            log.info("⏱️ Epic discovery took {} ms", discoveryMs.get());
                            List<String> ids = dto.getPractitionerIds();
                            log.info("✅ Tracking data: Discovered {} practitioner IDs, fetching details...", ids.size());
                            return fetchDoctorsByIdsWithToken(ids, token);
                        }))
                .doOnSuccess(list -> {
                    long total = System.currentTimeMillis() - start.get();
                    long fetch = total - discoveryMs.get();
                    log.info("⏱️ Epic batch fetch took {} ms", fetch);
                    log.info("⏱️ Epic total (discover + fetch) took {} ms", total);
                    log.info("🏁 Tracking data: Successfully fetched {} doctor details from sandbox", (list != null ? list.size() : 0));
                })
                .onErrorMap(e -> new IntentHandlingException(IntentErrorType.EXECUTION_FAILED, "Error in getAllDoctorsFromSandbox intent", e));
    }
    private Mono<List<DoctorDTO>> fetchDoctorsByIds(List<String> ids) {
        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(token -> fetchDoctorsByIdsWithToken(ids, token));
    }

    private Mono<List<DoctorDTO>> fetchDoctorsByIdsWithToken(List<String> ids, String token) {
        log.info("📥 Batch fetching {} doctors from Epic", ids.size());
        return Flux.fromIterable(ids)
                .distinct()
                .flatMap(id -> buildDoctorWithRole(token, id)
                                .onErrorResume(e -> {
                                    log.warn("⚠️ Failed to fetch doctor {}: {}", id, e.getMessage());
                                    return Mono.empty();
                                }),
                        6, 8 // bounded concurrency and prefetch
                )
                .collectList()
                .doOnSuccess(list -> log.info("✅ Batch fetch complete. Retrieved {}/{} doctors", list.size(), ids.size()));
    }

    @Override
    public Flux<DoctorDTO> streamAllDoctorsFromSandbox(int concurrency) {
        return authService.getAccessToken("system/Practitioner.read")
                .flatMapMany(token -> sandboxDiscoveryService.discoverIds()
                        .flatMapMany(dto -> Flux.fromIterable(dto.getPractitionerIds()))
                        .flatMap(id -> buildDoctorWithRole(token, id)
                                .onErrorResume(e -> Mono.empty()), concurrency));
    }

    @Override
    public Flux<DoctorDTO> streamDoctorsByIds(List<String> ids, int concurrency) {
        return authService.getAccessToken("system/Practitioner.read")
                .flatMapMany(token -> Flux.fromIterable(ids)
                        .flatMap(id -> buildDoctorWithRole(token, id)
                                .onErrorResume(e -> Mono.empty()), concurrency));
    }

    @Override
    public Mono<List<DoctorDTO>> getDoctorsPage(int page, int size) {
        int p = Math.max(page, 0);
        int s = size <= 0 ? 20 : size;
        return authService.getAccessToken("system/Practitioner.read")
                .flatMap(token -> sandboxDiscoveryService.discoverIds()
                        .flatMap(dto -> {
                            List<String> ids = dto.getPractitionerIds();
                            int from = Math.min(p * s, ids.size());
                            int to = Math.min(from + s, ids.size());
                            List<String> sub = ids.subList(from, to);
                            return Flux.fromIterable(sub)
                                    .flatMap(id -> buildDoctorWithRole(token, id)
                                            .onErrorResume(e -> Mono.empty()), 3)
                                    .collectList();
                        }));
    }


    private Mono<JsonNode> fetchPractitionerRole(String token, String practitionerId) {

        return webClient.get()
                .uri(EpicConstants.FHIR_BASE + "/PractitionerRole?practitioner={id}", practitionerId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
//    @Override
//    public Mono<List<DoctorDTO>> getAllSandboxDoctors() {
//
//        return epicSandboxDiscoveryService.discoverIds()
//                .flatMapMany(dto -> Flux.fromIterable(dto.getPractitionerIds()))
//                .flatMap(this::getDoctorById)
//                .collectList();
//    }

public Mono<List<DoctorDTO>> getAllSandboxDoctors() {

    return authService.getAccessToken("system/Practitioner.read")
            .flatMap(token ->
                    epicSandboxDiscoveryService.discoverIds()
                            .flatMapMany(dto -> Flux.fromIterable(dto.getPractitionerIds()))
                            .flatMap(id -> buildDoctorWithRole(token, id)
                                    .onErrorResume(e -> Mono.empty()))
                            .collectList()
            );
}
    private String extractSpecialization(JsonNode roleBundle) {

        if (roleBundle == null || roleBundle.isNull()) return null;

        JsonNode entries = roleBundle.get("entry");

        if (entries != null && entries.isArray() && entries.size() > 0) {

            JsonNode resource = entries.get(0).get("resource");

            if (resource != null && resource.has("specialty")) {

                JsonNode specialty = resource.get("specialty");

                if (specialty.isArray() && specialty.size() > 0) {

                    JsonNode coding = specialty.get(0).get("coding");

                    if (coding != null && coding.isArray() && coding.size() > 0) {
                        return coding.get(0).get("display").asText();
                    }
                }
            }
        }

        return null;
    }
    private String extractFullName(JsonNode practitioner) {

        JsonNode names = practitioner.get("name");

        if (names != null && names.isArray() && names.size() > 0) {

            JsonNode name = names.get(0);

            String family = name.has("family") ? name.get("family").asText() : "";

            String given = "";
            if (name.has("given")) {
                for (JsonNode g : name.get("given")) {
                    given += g.asText() + " ";
                }
            }

            return (given + family).trim();
        }

        return "Unknown";
    }

    private Mono<DoctorDTO> buildDoctorWithRole(String token, String id) {

        Mono<JsonNode> practitionerMono =
                fetchPractitionerByRead(token, id)
                        .onErrorResume(EpicClientException.class, e -> e.getStatusCode() == 404
                                ? fetchPractitionerBySearch(token, id)
                                : Mono.error(e))
                        .map(this::parseJson);   // needed (returns String)

        Mono<JsonNode> roleMono =
                fetchPractitionerRole(token, id)  // already JsonNode
                        .onErrorResume(e -> Mono.empty())
                        .defaultIfEmpty(NullNode.getInstance());

        return Mono.zip(practitionerMono, roleMono)
                .map(tuple -> {

                    JsonNode practitioner = tuple.getT1();
                    JsonNode roleBundle = tuple.getT2();

                    String fullName = extractFullName(practitioner);
                    String qualification = extractQualification(practitioner);
                    if (qualification == null || qualification.isBlank()) qualification = getRandomQualification();

                    String specialization = extractSpecialization(roleBundle);
                    if (specialization == null || specialization.isBlank()) specialization = getRandomSpecialty();

                    String email = findTelecomValueFromJson(practitioner, "email");
                    if (email == null || email.isBlank()) email = getRandomEmail(fullName);

                    String phone = findTelecomValueFromJson(practitioner, "phone");
                    if (phone == null || phone.isBlank()) phone = getRandomPhone();

                    return DoctorDTO.builder()
                            .id(practitioner.get("id").asText())
                            .fullName(fullName)
                            .qualification(qualification)
                            .specialization(specialization)
                            .email(email)
                            .phone(phone)
                            .isFromEpic(true)
                            .build();
                });
    }
    private String extractQualification(JsonNode practitioner) {

        JsonNode qualifications = practitioner.get("qualification");

        if (qualifications != null && qualifications.isArray() && qualifications.size() > 0) {
            JsonNode code = qualifications.get(0).get("code");

            if (code != null) {
                if (code.has("text")) {
                    return code.get("text").asText();
                }

                JsonNode coding = code.get("coding");
                if (coding != null && coding.isArray() && coding.size() > 0) {
                    return coding.get(0).get("display").asText();
                }
            }
        }
        return null;
    }

    private String getRandomQualification() {
        return QUALIFICATIONS.get(random.nextInt(QUALIFICATIONS.size()));
    }

    private String getRandomSpecialty() {
        return SPECIALTIES.get(random.nextInt(SPECIALTIES.size()));
    }

    private String getRandomEmail(String fullName) {
        String base = fullName.toLowerCase().replaceAll("[^a-z]", ".");
        return base + "@example.com";
    }

    private String getRandomPhone() {
        return "+1-555-" + String.format("%04d", random.nextInt(10000));
    }

    private String findTelecomValueFromJson(JsonNode practitioner, String system) {
        if (practitioner == null || !practitioner.has("telecom")) return null;
        JsonNode telecom = practitioner.get("telecom");
        if (telecom.isArray()) {
            for (JsonNode t : telecom) {
                if (t.has("system") && t.get("system").asText().equalsIgnoreCase(system)) {
                    if (t.has("value")) return t.get("value").asText();
                }
            }
        }
        return null;
    }
}
