package com.softnerve.epic.service.Implimentation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.softnerve.epic.constant.EpicConstants;
import com.softnerve.epic.exception.EpicAuthException;
import com.softnerve.epic.exception.EpicClientException;
import com.softnerve.epic.exception.EpicDataMappingException;
import com.softnerve.epic.model.EmailEvent;
import com.softnerve.epic.model.dao.PatientDocument;
import com.softnerve.epic.model.dto.AddressDTO;
import com.softnerve.epic.model.dto.ObservationBundleDTO;
import com.softnerve.epic.model.dto.ClinicalNoteDTO;
import com.softnerve.epic.model.dto.ClinicalNoteBundleDTO;
import com.softnerve.epic.model.dto.PatientSummaryDTO;
import com.softnerve.epic.model.dto.RegistrationDTO;
import com.softnerve.epic.repo.EpicPatientRepository;
import com.softnerve.epic.service.CounterService;
import com.softnerve.epic.service.EpicPatientService;
import com.softnerve.epic.utils.ObservationMapper;
import com.softnerve.epic.utils.PatientMapper;
import com.softnerve.epic.utils.DocumentReferenceMapper;
import com.softnerve.epic.exception.PatientNotFoundException;
import com.softnerve.epic.model.dto.ObservationSummaryDTO;
import com.softnerve.epic.model.dao.ObservationDocument;
import com.softnerve.epic.repo.ObservationRepository;
import dev.softnerve.annotation.IntentDefinition;
import dev.softnerve.annotation.IntentService;
import dev.softnerve.exception.IntentErrorType;
import dev.softnerve.exception.IntentHandlingException;
import dev.softnerve.model.Audience;
import lombok.extern.slf4j.Slf4j;
import org.attachment.softnerve.service.KafkaService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.thymeleaf.context.Context;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Service
@IntentService
//@RequiredArgsConstructor
public class EpicPatientServiceIMPL implements EpicPatientService {
    private final WebClient webClient;
    private final TemplateEngine templateEngine;
    private final KafkaService kafkaService;

    private final String clientId;
    private final String tokenUrl;
    private final String fhirBase;
    private final CounterService counterService;
    private String privateJwkJson;
    private final FhirContext fhirContext = FhirContext.forR4();
    private final EpicPatientRepository patientRepository;
//    private final Logger log = (Logger) LoggerFactory.getLogger(EpicPatientService.class);
//    private final WebClient webClient;
//    private final String clientId;
//    private final String tokenUrl;
//    private final String fhirBase;
//    private String privateJwkJson;
//    private final FhirContext fhirContext = FhirContext.forR4();
private static final String FRONTEND_BASE_URL =
        "https://healthbuddy.softnerve.com/";

    private final PasswordEncoder passwordEncoder;

    public EpicPatientServiceIMPL(WebClient webClient, TemplateEngine templateEngine, KafkaService kafkaService,
                              @Value("${epic.client-id}") String clientId,
                              @Value("${epic.token-url}") String tokenUrl,
                              @Value("${epic.fhir-base}") String fhirBase, CounterService counterService,
                              @Value("${epic.private-jwk}") String privateJwkJson, EpicPatientRepository patientRepository, PasswordEncoder passwordEncoder, EpicAuthService authService, ObservationRepository observationRepository) {
        this.webClient = webClient;
        this.templateEngine = templateEngine;
        this.kafkaService = kafkaService;
        this.clientId = clientId;
        this.tokenUrl = tokenUrl;
        this.fhirBase = fhirBase;
        this.counterService = counterService;
        this.privateJwkJson = privateJwkJson.trim();
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.observationRepository = observationRepository;
    }

    private final EpicAuthService authService;
    private final ObservationRepository observationRepository;


    public Mono<ResponseEntity<String>> createPatient(String patientJson, String password,String email, List<AddressDTO> addressList, long startTime) {

        log.info("➡️ Received request to create Patient");

        return authService.getAccessToken("system/Patient.write")
                .flatMap(token ->
                                webClient.post()
                                        .uri(EpicConstants.FHIR_BASE + "/Patient")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .contentType(MediaType.parseMediaType("application/fhir+json"))
                                        .accept(MediaType.parseMediaType("application/fhir+json"))
                                        .bodyValue(patientJson)
                                        .exchangeToMono(resp ->
                                                resp.bodyToMono(String.class)
                                                        .defaultIfEmpty("")
                                                        .flatMap(body -> {
                                                            if (resp.statusCode().isError()) {
                                                                return Mono.error(new EpicClientException(resp.statusCode().value(), body));
                                                            }
                                                            HttpHeaders epicHeaders = resp.headers().asHttpHeaders();
                                                            String location = epicHeaders.getFirst(HttpHeaders.LOCATION);
                                                            if (location == null) {
                                                                return Mono.error(new EpicClientException(resp.statusCode().value(), "Epic did not return Patient ID"));
                                                            }
                                                            String epicPatientId = location.substring(location.lastIndexOf("/") + 1);
                                                            return fetchPatientFromEpic(epicPatientId)
                                                                    .map(this::mapEpicPatientToMongo)
                                                                    .flatMap(doc -> {
                                                                        String patientId = counterService.getNextPatientId();
                                                                        doc.setId(patientId);
                                                                        doc.setIsVerified(false);
                                                                        doc.setAddressDTO(addressList);
                                                                        doc.setEmail(email);
                                                                        String hashedPassword = passwordEncoder.encode(password);
                                                                        doc.setRegistrationToken(generateRegistrationToken());
                                                                        doc.setPasswordHash(hashedPassword);
                                                                        return savePatientInMongo(doc);
                                                                    })
                                                                    .doOnSuccess(this::sendVerificationEmail)
                                                                    .map(saved -> {
                                                                        String timeTaken = (System.currentTimeMillis() - startTime) + "ms";
                                                                        String responseBody = """
            {
              "message": "Patient created in Epic, saved locally, login using same email",
              "patientId": "%s",
              "epicPatientId": "%s",
              "timeTaken": "%s"
            }
            """.formatted(saved.getId(), saved.getEpicPatientId(), timeTaken);
                                                                        log.info("Request createPatient processed in {}", timeTaken);
                                                                        HttpHeaders headers = new HttpHeaders();
                                                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                                                        return new ResponseEntity<>(responseBody, headers, resp.statusCode());
                                                                    });
                                                        })
                                        )
                );
    }


    public Mono<String> fetchPatientFromEpic(String epicPatientId) {

        return authService.getAccessToken("system/Patient.read")
                .flatMap(token ->
                        webClient.get()
                                .uri(EpicConstants.FHIR_BASE + "/Patient/{id}", epicPatientId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .retrieve()
                                .bodyToMono(String.class)
                );
    }

    @IntentDefinition(
            audience = Audience.PATIENT,
            name = "Get_Patient_details",
            description = "Fetch patient details from Ehir system"
    )
    public Mono<PatientSummaryDTO> getPatientByEmail(String email) {
        log.info("📧 Tracking data: Fetching patient by email: {}", email);
        return Mono.fromCallable(() -> patientRepository.findFirstByEmailOrderByCreatedAtDesc(email).orElse(null))
                .flatMap(doc -> {
                    if (doc == null) {
                        log.warn("❌ Tracking data: Patient not found for email: {}", email);
                        return Mono.error(new PatientNotFoundException("Patient with email not found"));
                    }
                    String epicId = doc.getEpicPatientId();
                    log.info("✅ Tracking data: Found local document. epicId: {}, email: {}", epicId, email);
                    if (epicId == null || epicId.isEmpty()) {
                        return Mono.error(new EpicDataMappingException("Epic patient id missing for email"));
                    }
                    return fetchPatientFromEpic(epicId)
                            .map(this::parseAndMapPatient)
                            .doOnSuccess(dto -> log.info("🏁 Tracking data: Successfully fetched and mapped patient details for epicId: {}", epicId));
                })
                .onErrorMap(e -> !(e instanceof PatientNotFoundException || e instanceof EpicDataMappingException) 
                        ? new IntentHandlingException(IntentErrorType.EXECUTION_FAILED, "Error in getPatientByEmail intent", e) : e);
    }

    public PatientDocument mapEpicPatientToMongo(String epicJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Patient patient = parser.parseResource(Patient.class, epicJson);
            PatientDocument doc = new PatientDocument();
            doc.setEpicPatientId(patient.getIdElement().getIdPart());
            if (!patient.getName().isEmpty()) {
                doc.setFirstName(patient.getNameFirstRep().getGivenAsSingleString());
                doc.setLastName(patient.getNameFirstRep().getFamily());
            }
            doc.setGender(patient.getGender() != null ? patient.getGender().toCode() : null);
            if (patient.getBirthDate() != null) {
                doc.setDob(patient.getBirthDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate());
            }
            patient.getTelecom().forEach(t -> {
                if ("phone".equalsIgnoreCase(t.getSystem().toCode())) {
                    doc.setPhone(t.getValue());
                }
                if ("email".equalsIgnoreCase(t.getSystem().toCode())) {
                    doc.setEmail(t.getValue());
                }
            });
            doc.setCreatedAt(Instant.now());
            return doc;
        } catch (Exception e) {
            throw new EpicDataMappingException("Failed to map Epic patient data", e);
        }
    }

    public Mono<PatientDocument> savePatientInMongo(PatientDocument doc) {
        return Mono.fromCallable(() -> patientRepository.save(doc));
    }

//    public Mono<ResponseEntity<String>> createPatientFromDto(RegistrationDTO dto) {
//
//        String patientJson = buildPatientJson(dto);
//
//        log.info("🧾 Generated Epic Patient JSON from DTO");
//        log.debug("FHIR Patient JSON: {}", patientJson);
//
//        return createPatient(patientJson);
//    }
    private String buildPatientJson(RegistrationDTO dto) {

        return """
        {
          "resourceType": "Patient",
          "name": [
            {
              "use": "official",
              "family": "%s",
              "given": ["%s"]
            }
          ],
          "telecom": [
            {
              "system": "phone",
              "value": "%s%s",
              "use": "mobile"
            },
            {
              "system": "email",
              "value": "%s"
            }
          ],
          "gender": "%s",
          "birthDate": "%s"
        }
        """.formatted(
                dto.getPatientLastName(),
                dto.getPatientFirstName(),
                dto.getCountryCode(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                dto.getGender().name().toLowerCase(),
                dto.getDob()
        );
    }
    private Mono<String> fetchPatient(String token, String patientId) {

        log.debug("➡️ Fetching patient from Epic. patientId={}", patientId);

        return webClient.get()
                .uri(fhirBase + "/Patient/{id}", patientId)
                .headers(h -> h.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body ->
                        log.debug("📦 Raw Epic response received. patientId={}", patientId))
                .doOnError(e ->
                        log.error("❌ Epic API call failed. patientId={}, error={}",
                                patientId, e.getMessage(), e));
    }


//    @IntentDefinition(
//            audience = Audience.PATIENT,
//            name = "Fetching patient using ID",
//            description = "Use it to fetch patient using patientId"
//    )
    public Mono<PatientSummaryDTO> getPatient(String patientId) {

        log.info("➡️ Starting Epic patient fetch flow. patientId={}", patientId);

        return getAccessToken("system/Patient.read")
                .doOnSubscribe(s ->
                        log.info("🔐 Requesting access token for scope=system/Patient.read"))
                .doOnSuccess(token ->
                        log.info("🔑 Access token received successfully"))
                .flatMap(token -> fetchPatient(token, patientId))
                .doOnSubscribe(s ->
                        log.info("🌐 Calling Epic FHIR Patient API. patientId={}", patientId))
                .doOnSuccess(response ->
                        log.info("📨 Epic FHIR response received. patientId={}", patientId))
                .map(this::parseAndMapPatient)
                .doOnSuccess(dto ->
                        log.info("🧩 Patient mapping completed. patientId={}", patientId))
                .doOnError(e ->
                        log.error("🔥 Error in Epic patient fetch flow. patientId={}, error={}",
                                patientId, e.getMessage(), e));
    }
    private PatientSummaryDTO parseAndMapPatient(String fhirJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Patient patient = parser.parseResource(Patient.class, fhirJson);
            return PatientMapper.toDto(patient);
        } catch (Exception e) {
            throw new EpicDataMappingException("Failed to parse Patient resource", e);
        }
    }
    private Mono<String> getAccessToken(String scope) {
        try {
            RSAKey rsaKey = RSAKey.parse(privateJwkJson);

            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(clientId)
                    .subject(clientId)
                    .audience(tokenUrl)
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(java.util.Date.from(now))
                    .expirationTime(java.util.Date.from(now.plusSeconds(300)))
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS384)
                    .keyID(rsaKey.getKeyID())
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);
            RSASSASigner signer = new RSASSASigner(rsaKey.toPrivateKey());
            signedJWT.sign(signer);
            String clientAssertion = signedJWT.serialize();

            return webClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                            .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                            .with("client_assertion", clientAssertion)
                            .with("scope", scope))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(m -> (String) m.get("access_token"));

        } catch (Exception e) {
            return Mono.error(new EpicAuthException("Failed to obtain access token", e));
        }
    }
//    @IntentDefinition(
//            audience = Audience.PATIENT,
//            name = "Get_Observations_By_PatientId",
//            description = "Fetch laboratory observations for a patient from Epic"
//    )
    public Mono<ObservationBundleDTO> getObservations(String patientId) {
        return getAccessToken("system/Observation.read")
                .flatMap(token -> fetchObservations(token, patientId))
                .publishOn(Schedulers.boundedElastic())
                .map(this::parseAndMap)
                .doOnNext(bundle -> persistObservationsAsync(bundle));
    }
    @IntentDefinition(
            audience = Audience.PATIENT,
            name = "Get_me_my_Observations",
            description = "Fetch laboratory observation details "
    )
    public Mono<ObservationBundleDTO> getObservationsByEmail(String email) {
        log.info("📧 Tracking data: Fetching observations for email: {}", email);
        return Mono.fromCallable(() -> patientRepository.findFirstByEmailOrderByCreatedAtDesc(email).orElse(null))
                .flatMap(doc -> {
                    if (doc == null) {
                        log.warn("❌ Tracking data: Patient not found for email: {}", email);
                        return Mono.error(new PatientNotFoundException("Patient with email not found"));
                    }
                    String epicId = doc.getEpicPatientId();
                    log.info("✅ Tracking data: Found local document. epicId: {}, email: {}", epicId, email);
                    if (epicId == null || epicId.isEmpty()) {
                        return Mono.error(new EpicDataMappingException("Epic patient id missing for email"));
                    }
                    return getObservations(epicId)
                            .doOnSuccess(bundle -> log.info("🏁 Tracking data: Successfully fetched {} observations for epicId: {}", 
                                    (bundle != null && bundle.getObservations() != null ? bundle.getObservations().size() : 0), epicId));
                })
                .onErrorMap(e -> !(e instanceof PatientNotFoundException || e instanceof EpicDataMappingException) 
                        ? new IntentHandlingException(IntentErrorType.EXECUTION_FAILED, "Error in getObservationsByEmail intent", e) : e);
    }

    private Mono<String> fetchObservations(String accessToken, String patientId) {
        return webClient
                .get()
                .uri(fhirBase + "/Observation?patient={id}&category=laboratory", patientId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .accept(MediaType.parseMediaType("application/fhir+json"))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new EpicClientException(response.statusCode().value(), body)
                                ))
                )
                .bodyToMono(String.class);
    }

    private ObservationBundleDTO parseAndMap(String fhirJson) {
        try {
            IParser parser = fhirContext.newJsonParser();
            Bundle bundle = parser.parseResource(Bundle.class, fhirJson);
            return ObservationMapper.toDto(bundle);
        } catch (Exception e) {
            throw new EpicDataMappingException("Failed to parse Observation bundle", e);
        }
    }
    private void persistObservationsAsync(ObservationBundleDTO bundle) {
        if (bundle == null || bundle.getObservations() == null || bundle.getObservations().isEmpty()) {
            return;
        }
        reactor.core.publisher.Flux.fromIterable(bundle.getObservations())
                .map(this::toObservationDocument)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(doc -> Mono.fromCallable(() -> observationRepository.save(doc)))
                .doOnError(err -> log.error("Failed to persist observations: {}", err.getMessage()))
                .subscribe();
    }
    private ObservationDocument toObservationDocument(ObservationSummaryDTO s) {
        ObservationDocument doc = new ObservationDocument();
        doc.setObservationId(s.getObservationId());
        doc.setTestName(s.getTestName());
        doc.setCategory(s.getCategory());
        doc.setStatus(s.getStatus());
        doc.setPatientId(s.getPatientId());
        doc.setEncounterId(s.getEncounterId());
        doc.setEffectiveDate(s.getEffectiveDate());
        doc.setIssuedDate(s.getIssuedDate());
        doc.setValue(s.getValue());
        doc.setUnit(s.getUnit());
        doc.setReferenceRange(s.getReferenceRange());
        doc.setInterpretation(s.getInterpretation());
        doc.setCreatedAt(java.time.Instant.now());
        return doc;
    }

    public Mono<ResponseEntity<String>> createObservation(String observationJson, long startTime) {

        return authService.getAccessToken("system/Observation.write")
                .flatMap(token ->
                        webClient.post()
                                .uri(fhirBase + "/Observation")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.parseMediaType("application/fhir+json"))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .bodyValue(observationJson)
                                .exchangeToMono(resp ->
                                        resp.bodyToMono(String.class)
                                                .defaultIfEmpty("")
                                                .flatMap(body -> {
                                                    if (resp.statusCode().isError()) {
                                                        return Mono.error(new EpicClientException(resp.statusCode().value(), body));
                                                    }
                                                    String location = resp.headers().asHttpHeaders().getFirst(HttpHeaders.LOCATION);
                                                    String epicObservationId = null;
                                                    if (location != null) {
                                                        epicObservationId = location.substring(location.lastIndexOf("/") + 1);
                                                    }
                                                    String timeTaken = (System.currentTimeMillis() - startTime) + "ms";
                                                    String responseBody = """
                {
                  "message": "Observation create attempted",
                  "epicObservationId": "%s",
                  "timeTaken": "%s"
                }
                """.formatted(epicObservationId, timeTaken);
                                                    log.info("Request createObservation processed in {}", timeTaken);
                                                    return Mono.just(new ResponseEntity<>(responseBody, resp.headers().asHttpHeaders(), resp.statusCode()));
                                                })
                                )

                );
    }

    public Mono<ClinicalNoteDTO> getClinicalNoteById(String documentId) {
        long start = System.currentTimeMillis();
        log.info("Starting Epic DocumentReference fetch. documentId={}", documentId);
        return getAccessToken("system/DocumentReference.read")
                .doOnSubscribe(s -> log.info("Requesting access token for scope=system/DocumentReference.read"))
                .doOnSuccess(t -> log.info("Access token acquired for DocumentReference.read"))
                .flatMap(token ->
                        webClient.get()
                                .uri(fhirBase + "/DocumentReference/{id}", documentId)
                                .headers(h -> h.setBearerAuth(token))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                                .doOnNext(b -> log.info("Epic DocumentReference response received. documentId={}", documentId))
                )
                .publishOn(Schedulers.boundedElastic())
                .map(json -> {
                    IParser parser = fhirContext.newJsonParser();
                    org.hl7.fhir.r4.model.DocumentReference dr =
                            parser.parseResource(org.hl7.fhir.r4.model.DocumentReference.class, json);
                    return DocumentReferenceMapper.mapDocRef(dr);
                })
                .doOnSuccess(dto -> log.info("Clinical note mapped. documentId={}, time={}ms", documentId, (System.currentTimeMillis() - start)))
                .doOnError(e -> log.error("Error fetching clinical note. documentId={}, error={}", documentId, e.getMessage()));
    }

    public Mono<ClinicalNoteBundleDTO> getClinicalNotesByPatient(String patientId) {
        long start = System.currentTimeMillis();
        log.info("Starting Epic DocumentReference search. patientId={}", patientId);
        return getAccessToken("system/DocumentReference.read")
                .doOnSubscribe(s -> log.info("Requesting access token for scope=system/DocumentReference.read"))
                .doOnSuccess(t -> log.info("Access token acquired for DocumentReference.read"))
                .flatMap(token ->
                        webClient.get()
                                .uri(fhirBase + "/DocumentReference?patient={id}", patientId)
                                .headers(h -> h.setBearerAuth(token))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                                .doOnNext(b -> log.info("Epic DocumentReference bundle received. patientId={}", patientId))
                )
                .publishOn(Schedulers.boundedElastic())
                .map(json -> {
                    IParser parser = fhirContext.newJsonParser();
                    Bundle bundle = parser.parseResource(Bundle.class, json);
                    return DocumentReferenceMapper.toDto(bundle);
                })
                .doOnSuccess(b -> log.info("Clinical notes mapped. patientId={}, count={}, time={}ms",
                        patientId,
                        (b != null && b.getNotes() != null ? b.getNotes().size() : 0),
                        (System.currentTimeMillis() - start)))
                .doOnError(e -> log.error("Error fetching clinical notes. patientId={}, error={}", patientId, e.getMessage()));
    }
    private String generateRegistrationToken() {
        return UUID.randomUUID().toString();
    }
    public void sendVerificationEmail(PatientDocument patient) {

        log.info("➡️ Preparing verification email for patientId={}", patient.getId());
        String verificationLink = FRONTEND_BASE_URL
                + "/verify/verifyRegistration?email="
                + patient.getEmail()
                + "&authResource=PATIENT"
                + "&token="
                + patient.getRegistrationToken(); // 🔥 using patientId (Mongo _id)

        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);
        context.setVariable("patientFirstName", patient.getFirstName());

        String emailBody =
                templateEngine.process("verification-email-template", context);

        EmailEvent emailEvent = EmailEvent.builder()
                .textType(EmailEvent.TextType.HTML)
                .replyTo("no-reply-healthbuddy@softenerve.com")
                .replyToName("Health Buddy Automation")
                .fromName("HealthBuddy Automation")
                .to(Collections.singletonList(patient.getEmail()))
                .body(emailBody)
                .subject("Patient verification email")
                .build();

        log.info("📧 Sending verification email for patientId={}", patient.getId());

        kafkaService.publishToKafkaAsync(
                "send-email",
                patient.getId(),
                emailEvent
        );
    }


}
}
