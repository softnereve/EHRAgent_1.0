package com.softnerve.epic.utils;

import com.softnerve.epic.model.dto.*;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class PractitionerMapper {
    private static final Random random = new Random();
    private static final List<String> QUALIFICATIONS = Arrays.asList("MD", "MBBS");
    private static final List<String> SPECIALTIES = Arrays.asList("Cardiology", "Dermatology", "Pediatrics", "Neurology", "Orthopedics", "General Medicine");

    public static PractitionerDTO toPractitionerDto(Practitioner practitioner) {
        if (practitioner == null) return null;

        return PractitionerDTO.builder()
                .resourceType("Practitioner")
                .id(practitioner.getIdElement().getIdPart())
                .active(practitioner.hasActive() ? practitioner.getActive() : null)
                .name(practitioner.getName().stream()
                        .map(n -> NameDTO.builder()
                                .use(n.hasUse() ? n.getUse().toCode() : null)
                                .family(n.getFamily())
                                .given(n.getGiven().stream()
                                        .map(g -> g.getValue())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .identifier(practitioner.getIdentifier().stream()
                        .map(i -> IdentifierDTO.builder()
                                .use(i.hasUse() ? i.getUse().toCode() : null)
                                .system(i.getSystem())
                                .value(i.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .telecom(practitioner.getTelecom().stream()
                        .map(t -> TelecomDTO.builder()
                                .system(t.hasSystem() && t.getSystem() != null ? t.getSystem().toCode() : null)
                                .value(t.getValue())
                                .use(t.hasUse() && t.getUse() != null ? t.getUse().toCode() : null)
                                .build())
                        .collect(Collectors.toList()))
                .gender(practitioner.hasGender() ? practitioner.getGender().toCode() : null)
                .birthDate(practitioner.hasBirthDate() ? practitioner.getBirthDate().toString() : null)
                .photo(practitioner.getPhoto().stream()
                        .map(p -> AttachmentDTO.builder()
                                .contentType(p.getContentType())
                                .url(p.getUrl())
                                .title(p.getTitle())
                                .build())
                        .collect(Collectors.toList()))
                .qualification(practitioner.getQualification().stream()
                        .map(q -> QualificationDTO.builder()
                                .identifier(q.getIdentifier().stream()
                                        .map(i -> IdentifierDTO.builder()
                                                .use(i.hasUse() ? i.getUse().toCode() : null)
                                                .system(i.getSystem())
                                                .value(i.getValue())
                                                .build())
                                        .collect(Collectors.toList()))
                                .code(CodeableConceptDTO.builder()
                                        .text(q.getCode().getText())
                                        .coding(q.getCode().getCoding().stream()
                                                .map(c -> CodingDTO.builder()
                                                        .system(c.getSystem())
                                                        .code(c.getCode())
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .communication(practitioner.getCommunication().stream()
                        .map(c -> CodeableConceptDTO.builder()
                                .text(c.getText())
                                .coding(c.getCoding().stream()
                                        .map(cod -> CodingDTO.builder()
                                                .system(cod.getSystem())
                                                .code(cod.getCode())
                                                .display(cod.getDisplay())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static PractitionerRoleDTO toPractitionerRoleDto(PractitionerRole role) {
        if (role == null) return null;

        return PractitionerRoleDTO.builder()
                .resourceType("PractitionerRole")
                .id(role.getIdElement().getIdPart())
                .active(role.hasActive() ? role.getActive() : null)
                .practitioner(role.hasPractitioner() ? PractitionerRoleDTO.PractitionerReference.builder()
                        .reference(role.getPractitioner().getReference())
                        .display(role.getPractitioner().getDisplay())
                        .build() : null)
                .specialty(role.getSpecialty().stream()
                        .map(s -> CodeableConceptDTO.builder()
                                .text(s.getText())
                                .coding(s.getCoding().stream()
                                        .map(c -> CodingDTO.builder()
                                                .system(c.getSystem())
                                                .code(c.getCode())
                                                .display(c.getDisplay())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .telecom(role.getTelecom().stream()
                        .map(t -> TelecomDTO.builder()
                                .system(t.hasSystem() && t.getSystem() != null ? t.getSystem().toCode() : null)
                                .value(t.getValue())
                                .use(t.hasUse() && t.getUse() != null ? t.getUse().toCode() : null)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static PractitionerSummaryDTO toSummaryDto(PractitionerDTO dto, List<PractitionerRoleDTO> roles, String doctorId) {
        if (dto == null) return null;

        List<String> specialties = new ArrayList<>();
        if (dto.getQualification() != null) {
            for (QualificationDTO q : dto.getQualification()) {
                if (q.getCode() != null) {
                    if (q.getCode().getText() != null && !q.getCode().getText().isEmpty()) {
                        specialties.add(q.getCode().getText());
                    } else if (q.getCode().getCoding() != null && !q.getCode().getCoding().isEmpty()) {
                        // Fallback to coding display or code if text is missing
                        q.getCode().getCoding().stream()
                                .filter(Objects::nonNull)
                                .map(c -> c.getDisplay() != null ? c.getDisplay() : c.getCode())
                                .filter(Objects::nonNull)
                                .filter(s -> !specialties.contains(s))
                                .forEach(specialties::add);
                    }
                }
            }
        }
        
        if (roles != null) {
            for (PractitionerRoleDTO role : roles) {
                if (role.getSpecialty() != null) {
                    for (CodeableConceptDTO s : role.getSpecialty()) {
                        if (s.getText() != null && !s.getText().isEmpty()) {
                            if (!specialties.contains(s.getText())) specialties.add(s.getText());
                        } else if (s.getCoding() != null && !s.getCoding().isEmpty()) {
                            s.getCoding().stream()
                                    .filter(Objects::nonNull)
                                    .map(c -> c.getDisplay() != null ? c.getDisplay() : c.getCode())
                                    .filter(Objects::nonNull)
                                    .filter(val -> !specialties.contains(val))
                                    .forEach(specialties::add);
                        }
                    }
                }
            }
        }

        if (specialties.isEmpty()) {
            specialties.add(getRandomSpecialty());
        }

        List<String> telecoms = mergeTelecoms(dto, roles);
        String fullName = dto.getName() != null && !dto.getName().isEmpty() ?
                dto.getName().get(0).getGiven().stream().collect(Collectors.joining(" ")) + " " + dto.getName().get(0).getFamily() : "Unknown";

        if (telecoms.isEmpty()) {
            telecoms.add(getRandomEmail(fullName));
            telecoms.add(getRandomPhone());
        }

        return PractitionerSummaryDTO.builder()
                .doctorId(doctorId)
                .epicPractitionerId(dto.getId())
                .fullName(fullName)
                .gender(dto.getGender())
                .telecom(telecoms)
                .specialties(specialties)
                .isFromEpic(true)
                .build();
    }

    private static String getRandomQualification() {
        return QUALIFICATIONS.get(random.nextInt(QUALIFICATIONS.size()));
    }

    private static String getRandomSpecialty() {
        return SPECIALTIES.get(random.nextInt(SPECIALTIES.size()));
    }

    private static String getRandomEmail(String fullName) {
        String base = fullName.toLowerCase().replaceAll("[^a-z]", ".");
        return base + "@example.com";
    }

    private static String getRandomPhone() {
        return "+1-555-" + String.format("%04d", random.nextInt(10000));
    }

    private static List<String> mergeTelecoms(PractitionerDTO dto, List<PractitionerRoleDTO> roles) {
        List<String> telecoms = new ArrayList<>();
        if (dto.getTelecom() != null) {
            dto.getTelecom().stream()
                    .map(TelecomDTO::getValue)
                    .filter(Objects::nonNull)
                    .forEach(telecoms::add);
        }
        if (roles != null) {
            roles.stream()
                    .filter(r -> r.getTelecom() != null)
                    .flatMap(r -> r.getTelecom().stream())
                    .map(TelecomDTO::getValue)
                    .filter(Objects::nonNull)
                    .filter(t -> !telecoms.contains(t))
                    .forEach(telecoms::add);
        }
        return telecoms;
    }

    public static com.softnerve.epic.model.dao.PractitionerDocument toDocument(PractitionerDTO dto, List<PractitionerRoleDTO> roles, String doctorId) {
        if (dto == null) return null;

        List<String> specialties = new ArrayList<>();
        if (dto.getQualification() != null) {
            for (QualificationDTO q : dto.getQualification()) {
                if (q.getCode() != null) {
                    if (q.getCode().getText() != null && !q.getCode().getText().isEmpty()) {
                        specialties.add(q.getCode().getText());
                    } else if (q.getCode().getCoding() != null && !q.getCode().getCoding().isEmpty()) {
                        q.getCode().getCoding().stream()
                                .filter(Objects::nonNull)
                                .map(c -> c.getDisplay() != null ? c.getDisplay() : c.getCode())
                                .filter(Objects::nonNull)
                                .filter(s -> !specialties.contains(s))
                                .forEach(specialties::add);
                    }
                }
            }
        }
        
        if (roles != null) {
            for (PractitionerRoleDTO role : roles) {
                if (role.getSpecialty() != null) {
                    for (CodeableConceptDTO s : role.getSpecialty()) {
                        if (s.getText() != null && !s.getText().isEmpty()) {
                            if (!specialties.contains(s.getText())) specialties.add(s.getText());
                        } else if (s.getCoding() != null && !s.getCoding().isEmpty()) {
                            s.getCoding().stream()
                                    .filter(Objects::nonNull)
                                    .map(c -> c.getDisplay() != null ? c.getDisplay() : c.getCode())
                                    .filter(Objects::nonNull)
                                    .filter(val -> !specialties.contains(val))
                                    .forEach(specialties::add);
                        }
                    }
                }
            }
        }

        return com.softnerve.epic.model.dao.PractitionerDocument.builder()
                .id(doctorId)
                .epicPractitionerId(dto.getId())
                .firstName(dto.getName() != null && !dto.getName().isEmpty() ?
                        dto.getName().get(0).getGiven().stream().collect(Collectors.joining(" ")) : null)
                .lastName(dto.getName() != null && !dto.getName().isEmpty() ?
                        dto.getName().get(0).getFamily() : null)
                .gender(dto.getGender())
                .birthDate(dto.getBirthDate())
                .specialties(specialties)
                .telecom(mergeTelecoms(dto, roles))
                .createdAt(java.time.Instant.now())
                .isFromEpic(true)
                .build();
    }
}
