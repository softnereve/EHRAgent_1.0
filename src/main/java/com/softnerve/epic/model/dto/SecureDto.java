package com.softnerve.epic.model.dto;

import com.softnerve.epic.model.dao.Patient;
import com.softnerve.epic.model.dao.PatientDocument;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecureDto {
    private String email;
    private String password;
    private boolean status;
    private String countryCode;
    private String contact;
    private String ownerId;
    private String resourceType;
    private Boolean is2fa_enabled;

    public SecureDto(PatientDocument patient, boolean status) {
        this.email = patient.getEmail();
        this.password = patient.getPasswordHash();
        this.status = status;
        this.ownerId=patient.getId();
        this.resourceType="PATIENT";
        this.is2fa_enabled=false;
        this.countryCode = patient.getCountryCode();
        this.contact = patient.getPhone();
    }

}
