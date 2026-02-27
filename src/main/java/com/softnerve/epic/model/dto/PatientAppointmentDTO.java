package com.softnerve.epic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softnerve.epic.model.dao.AppointmentEvent;
import com.softnerve.epic.model.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientAppointmentDTO {
    private String appointmentId;
    @JsonProperty("providerId")
    private String doctorId;
    @JsonProperty("providerName")
    private String doctorName;
    @JsonProperty("clientId")
    private String patientId;
    @JsonProperty("clientName")
    private String patientName;
    private String venueId;
    private String venueDetails;
    private AppointmentStatus appointmentStatus;
    private AppointmentEvent.AppointmentSlot appointmentSlot;
    private String appointmentBookingDate; // [format --> DD/MM/YYYY]
    private long appointmentUpdatedAt;
    private ImmunizationData immunizationData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImmunizationData{
        private String childId;
        private String childName;
        private String immunizationId;
        private String immunizationName;
    }
}
