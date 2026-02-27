//package com.softnerve.epic.service.implimentation;
//
//import com.softnerve.epic.model.dto.EpicPatientResponse;
//import com.softnerve.epic.model.dto.RegistrationDTO;
//import com.softnerve.epic.service.EpicService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//@Service
//@RequiredArgsConstructor
//public class EpicFhirServiceIMPL implements EpicService {
//
//    private final WebClient webClient;
//    private final EpicAuthService epicAuthService;
//
//    // 🔥 DIRECT VALUE (NO @Value)
//    private static final String FHIR_BASE_URL =
//            "https://fhir.epic.com/interconnect-fhir-oauth/api/FHIR/R4";
//
//    @Override
//    public EpicPatientResponse createPatient(RegistrationDTO dto) {
//
//        String accessToken = epicAuthService.getAccessToken("system/Patient.write");
//
//        String patientJson = buildEpicPatientPayload(dto);
//
//        return webClient.post()
//                .uri(FHIR_BASE_URL + "/Patient")
//                .headers(h -> {
//                    h.setBearerAuth(accessToken);
//                    h.setContentType(MediaType.parseMediaType("application/fhir+json"));
//                    h.setAccept(List.of(MediaType.parseMediaType("application/fhir+json")));
//                })
//                .bodyValue(patientJson)
//                .exchangeToMono(response -> {
//                    if (!response.statusCode().is2xxSuccessful()) {
//                        return response.bodyToMono(String.class)
//                                .flatMap(body -> Mono.error(
//                                        new RuntimeException("Epic error: " + body)
//                                ));
//                    }
//
//                    String epicId = response.headers()
//                            .header("Location")
//                            .stream()
//                            .findFirst()
//                            .orElseThrow(() -> new RuntimeException("Epic ID missing"));
//
//                    return Mono.just(new EpicPatientResponse(extractId(epicId)));
//                })
//                .block();
//    }
//
//    private String extractId(String locationHeader) {
//        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
//    }
//
//    private String buildEpicPatientPayload(RegistrationDTO dto) {
//        return """
//        {
//          "resourceType": "Patient",
//          "active": true,
//          "name": [
//            {
//              "use": "official",
//              "family": "%s",
//              "given": ["%s"]
//            }
//          ],
//          "gender": "%s",
//          "birthDate": "%s",
//          "telecom": [
//            {
//              "system": "phone",
//              "value": "%s",
//              "use": "mobile"
//            }
//          ]
//        }
//        """.formatted(
//                dto.getPatientLastName(),
//                dto.getPatientFirstName(),
//                dto.getGender(),
//                dto.getDob(),
//                dto.getPhoneNumber()
//        );
//    }
//}
