package com.softnerve.epic.service.Implimentation;

import com.softnerve.epic.model.dao.ObservationDocument;
import com.softnerve.epic.model.dto.CreateObservationRequest;
import com.softnerve.epic.model.dto.ObservationBundleDTO;
import com.softnerve.epic.model.dto.ObservationSummaryDTO;
import com.softnerve.epic.repo.ObservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.softnerve.epic.service.ObservationService;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObservationServiceIMPL implements ObservationService {

    private final ObservationRepository repository;

    public Mono<ObservationBundleDTO> saveObservation(CreateObservationRequest request) {

        ObservationDocument doc = new ObservationDocument();
        doc.setObservationId(request.getObservationId());
        doc.setTestName(request.getTestName());
        doc.setCategory(request.getCategory());
        doc.setStatus(request.getStatus());
        doc.setPatientId(request.getPatientId());
        doc.setEncounterId(request.getEncounterId());
        doc.setEffectiveDate(request.getEffectiveDate());
        doc.setIssuedDate(request.getIssuedDate());
        doc.setValue(request.getValue());
        doc.setUnit(request.getUnit());
        doc.setReferenceRange(request.getReferenceRange());
        doc.setInterpretation(request.getInterpretation());
        doc.setCreatedAt(Instant.now());

        return Mono.fromCallable(() -> repository.save(doc))
                .map(saved -> {
                    log.info("Observation saved in MongoDB for patientId={}", saved.getPatientId());

                    ObservationSummaryDTO summary = new ObservationSummaryDTO(
                            saved.getObservationId(),
                            saved.getTestName(),
                            saved.getCategory(),
                            saved.getStatus(),
                            saved.getPatientId(),
                            saved.getEncounterId(),
                            saved.getEffectiveDate(),
                            saved.getIssuedDate(),
                            saved.getValue(),
                            saved.getUnit(),
                            saved.getReferenceRange(),
                            saved.getInterpretation()
                    );

                    return new ObservationBundleDTO(
                            "collection",
                            1,
                            List.of(summary),
                            null
                    );
                });
    }
    public Mono<ObservationBundleDTO> getObservationsFromMongo(String patientId) {

        return Mono.fromCallable(() -> repository.findByPatientId(patientId))
                .map(list -> {

                    if (list.isEmpty()) {
                        log.info("No observations found for patientId={}", patientId);
                    }

                    List<ObservationSummaryDTO> summaries =
                            list.stream()
                                    .map(this::toSummaryDto)
                                    .toList();

                    return new ObservationBundleDTO(
                            "collection",
                            summaries.size(),
                            summaries,
                            null
                    );
                });
    }

    private ObservationSummaryDTO toSummaryDto(ObservationDocument doc) {

        return new ObservationSummaryDTO(
                doc.getObservationId(),
                doc.getTestName(),
                doc.getCategory(),
                doc.getStatus(),
                doc.getPatientId(),
                doc.getEncounterId(),
                doc.getEffectiveDate(),
                doc.getIssuedDate(),
                doc.getValue(),
                doc.getUnit(),
                doc.getReferenceRange(),
                doc.getInterpretation()
        );
    }

}
