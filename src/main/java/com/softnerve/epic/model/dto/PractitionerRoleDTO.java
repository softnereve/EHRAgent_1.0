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

    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PractitionerReference {
        private String reference;
        private String display;
    }
}
