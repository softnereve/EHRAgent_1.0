package com.softnerve.epic.service.Implimentation;

import ca.uhn.fhir.context.FhirContext;
import com.softnerve.epic.constant.EpicConstants;
import com.softnerve.epic.exception.EpicClientException;
import com.softnerve.epic.exception.EpicDataMappingException;
import com.softnerve.epic.model.dto.AppointmentBookRequestDTO;
import com.softnerve.epic.model.dto.AppointmentFindRequestDTO;
import com.softnerve.epic.model.dto.AppointmentRequestDTO;
import com.softnerve.epic.model.dto.AppointmentResponseDTO;
import com.softnerve.epic.model.dto.SlotDTO;
import com.softnerve.epic.service.EpicAppointmentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnerve.epic.utils.SlotMapper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Slot;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class EpicAppointmentServiceIMPL implements EpicAppointmentService {

   public EpicAppointmentServiceIMPL(WebClient webClient, EpicAuthService authService) {
        this.webClient = webClient;
        this.authService = authService;
    }

    private final WebClient webClient;
    private final EpicAuthService authService;
    private final FhirContext fhirContextR4 = FhirContext.forR4();

    private final String fhirBase = EpicConstants.FHIR_BASE;
    private final String fhirBaseStu3 = EpicConstants.FHIR_BASE1;

    public Mono<AppointmentResponseDTO> bookAppointment(
            AppointmentRequestDTO dto) {

        String json = buildAppointmentJson(dto);

        return authService.getAccessToken("system/Appointment.write")
                .flatMap(token ->
                        webClient.post()
                                .uri(fhirBase + "/Appointment")
                                .headers(h -> h.setBearerAuth(token))
                                .contentType(MediaType.parseMediaType("application/fhir+json"))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .bodyValue(json)
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                )
                .map(this::parseAppointment);
    }

    private String buildAppointmentJson(AppointmentRequestDTO dto) {

        return """
        {
          "resourceType": "Appointment",
          "status": "booked",
          "participant": [
            {
              "actor": { "reference": "Patient/%s" },
              "status": "accepted"
            },
            {
              "actor": { "reference": "Practitioner/%s" },
              "status": "accepted"
            }
          ],
          "slot": [
            { "reference": "Slot/%s" }
          ],
          "description": "%s"
        }
        """.formatted(
                dto.getPatientId(),
                dto.getPractitionerId(),
                dto.getSlotId(),
                dto.getReason()
        );
    }

    private AppointmentResponseDTO parseAppointment(String json) {

        Appointment appointment =
                fhirContextR4.newJsonParser()
                        .parseResource(Appointment.class, json);

        return AppointmentResponseDTO.builder()
                .appointmentId(
                        appointment.getIdElement().getIdPart())
                .status(appointment.getStatus().toCode())
                .build();
    }

    public Mono<Void> cancelAppointment(String id) {

        String json = """
        {
          "resourceType": "Appointment",
          "id": "%s",
          "status": "cancelled"
        }
        """.formatted(id);

        return authService.getAccessToken("system/Appointment.write")
                .flatMap(token ->
                        webClient.put()
                                .uri(fhirBase + "/Appointment/{id}", id)
                                .headers(h -> h.setBearerAuth(token))
                                .contentType(MediaType.parseMediaType("application/fhir+json"))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .bodyValue(json)
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                )
                .then();
    }

    public Mono<List<AppointmentResponseDTO>> getAppointmentsByPatient(
            String patientId) {

        return authService.getAccessToken("system/Appointment.read")
                .flatMap(token ->
                        webClient.get()
                                .uri(fhirBase + "/Appointment?patient=" + patientId)
                                .headers(h -> h.setBearerAuth(token))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                )
                .map(this::parseAppointmentBundle);
    }

   private List<AppointmentResponseDTO> parseAppointmentBundle(String json) {

    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        if (!root.has("entry")) {
            return Collections.emptyList();
        }

        List<AppointmentResponseDTO> appointments = new ArrayList<>();

        for (JsonNode entry : root.get("entry")) {

            JsonNode resource = entry.get("resource");

            AppointmentResponseDTO dto = new AppointmentResponseDTO();
            dto.setAppointmentId(resource.get("id").asText());
            dto.setStatus(resource.get("status").asText());

            if (resource.has("start")) {
                dto.setStart(resource.get("start").asText());
            }

            if (resource.has("end")) {
                dto.setEnd(resource.get("end").asText());
            }

            appointments.add(dto);
        }

        return appointments;

    } catch (Exception e) {
        throw new RuntimeException("Failed to parse FHIR Appointment bundle", e);
    }
}


    @Override
    public Mono<List<SlotDTO>> findSlots(String practitionerId) {
        log.info("Generating pure dummy slots for practitioner {}. (Bypassing Epic)", practitionerId);
        return Mono.just(generateDummySlots(practitionerId));
    }

    private List<SlotDTO> generateDummySlots(String practitionerId) {
        List<SlotDTO> dummySlots = new ArrayList<>();
        OffsetDateTime tomorrow = OffsetDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        
        // Generate slots from 9 AM to 6 PM (18:00) in 30-minute intervals
        OffsetDateTime start = tomorrow.withHour(9).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime endLimit = tomorrow.withHour(18).withMinute(0).withSecond(0).withNano(0);

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

    private List<SlotDTO> parseSlotBundle(String json) {
        try {
            Bundle bundle = fhirContextR4.newJsonParser().parseResource(Bundle.class, json);
            return bundle.getEntry().stream()
                    .filter(e -> e.getResource() instanceof Slot)
                    .map(e -> (Slot) e.getResource())
                    .map(SlotMapper::toDto)
                    .toList();
        } catch (Exception e) {
            log.error("Error parsing Slot bundle: {}", e.getMessage());
            throw new EpicDataMappingException("Failed to parse slot data from Epic: " + e.getMessage());
        }
    }

    @Override
    public Mono<List<AppointmentResponseDTO>> findAppointments(AppointmentFindRequestDTO dto) {

        return authService.getAccessToken("system/Appointment.read")
                .flatMap(token -> callEpic(dto, token));
    }

    private Mono<List<AppointmentResponseDTO>> callEpic(
            AppointmentFindRequestDTO dto,
            String token) {

        String json = buildFindParametersJson(dto);

        log.info("Calling Epic FHIR STU3 $find: {}/Appointment/$find", fhirBaseStu3);

        return webClient.post()
                .uri(fhirBaseStu3 + "/Appointment/$find")
                .headers(headers -> {
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.parseMediaType("application/fhir+json"));
                    headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/fhir+json")));
                })
                .bodyValue(json)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Epic Error Response: {}", body);
                                    return Mono.error(
                                            new EpicClientException(
                                                    response.statusCode().value(),
                                                    body));
                                })
                )
                .bodyToMono(String.class)
                .map(this::parseAppointmentBundle);
    }

    private String buildFindParametersJson(AppointmentFindRequestDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"resourceType\": \"Parameters\",\n  \"parameter\": [\n");
        sb.append(String.format("    { \"name\": \"patient\", \"valueReference\": { \"reference\": \"Patient/%s\" } },\n", dto.getPatientId()));
        sb.append(String.format("    { \"name\": \"startTime\", \"valueDateTime\": \"%s\" },\n", dto.getStart()));
        sb.append(String.format("    { \"name\": \"endTime\", \"valueDateTime\": \"%s\" }", dto.getEnd()));

        if (dto.getPractitionerId() != null) {
            sb.append(String.format(",\n    { \"name\": \"practitioner\", \"valueReference\": { \"reference\": \"Practitioner/%s\" } }", dto.getPractitionerId()));
        }
        sb.append("\n  ]\n}");
        return sb.toString();
    }
    @Override
    public Mono<AppointmentResponseDTO> bookAppointmentSTU3(AppointmentBookRequestDTO dto) {
        String json = buildBookParametersJson(dto);

        return authService.getAccessToken("system/Appointment.write")
                .flatMap(token ->
                        webClient.post()
                                .uri(fhirBaseStu3 + "/Appointment/$book")
                                .headers(h -> h.setBearerAuth(token))
                                .contentType(MediaType.parseMediaType("application/fhir+json"))
                                .accept(MediaType.parseMediaType("application/fhir+json"))
                                .bodyValue(json)
                                .retrieve()
                                .onStatus(status -> status.isError(), resp -> resp.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new EpicClientException(resp.statusCode().value(), body))))
                                .bodyToMono(String.class)
                )
                .map(this::parseAppointment);
    }

   private String buildBookParametersJson(AppointmentBookRequestDTO dto) {

    return """
    {
      "resourceType": "Parameters",
      "parameter": [
        {
          "name": "patient",
          "valueReference": {
            "reference": "Patient/%s"
          }
        }%s
      ]
    }
    """.formatted(
            dto.getPatientId(),
            (dto.getNote() != null && !dto.getNote().isBlank())
                    ? ",\n        { \"name\": \"note\", \"valueString\": \"" + dto.getNote() + "\" }"
                    : ""
    );
}

}
