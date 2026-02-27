package com.softnerve.epic.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softnerve.epic.model.dto.PatientAppointmentDTO;
import com.softnerve.epic.model.enums.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "PatientAppointment")
public class AppointmentEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 3287238728238L;

    @Id
    @Field("_id")
    private ObjectId id;
    private String appointEventId;
    @NotEmpty(message = "AppointmentId requires")
    private String appointmentId;
    @NotBlank
    private String doctorId;
    @NotBlank
    private String doctorName;
    @NotBlank
    private String patientId;
    @NotBlank
    private String patientName;
    @NotBlank
    private String addressId;
    @NotBlank
    private String venueDetails;
    @Field(targetType = FieldType.STRING)
    private AppointmentStatus appointmentStatus;
    @NotBlank
    private AppointmentSlot appointmentSlot;
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Invalid date format (DD/MM/YYYY)")
    private String appointmentBookingDate;

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


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AppointmentSlot {
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:mm)")
        private String startTime;

        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:mm)")
        private String endTime;
    }
}
