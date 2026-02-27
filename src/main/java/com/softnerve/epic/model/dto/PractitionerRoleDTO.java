package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PractitionerRoleDTO {
    private String resourceType;
    private String id;
    private Boolean active;
    private PractitionerReference practitioner;
    private List<CodeableConceptDTO> specialty;
    private List<TelecomDTO> telecom;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PractitionerReference {
        private String reference;
        private String display;
    }
}
