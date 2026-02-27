package com.softnerve.epic.model.dto;

import com.softnerve.epic.model.dao.Patient;
import com.softnerve.epic.model.dao.PatientDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UiData {

    private String patientId;
    private String resourceType;
    private String patientFirstName;
    private String patientLastName;
    private String email;
    private String countryCode;
    private String phoneNumber;

    // ✅ Flattened address fields
    private String firstLane;
    private String secondLane;
    private String city;
    private Integer pinCode;


    public UiData(PatientDocument patient, String resourceType) {
        this.patientId = patient.getId();
        this.resourceType = resourceType;
        this.patientFirstName = patient.getFirstName();
        this.patientLastName = patient.getLastName();
        this.email = patient.getEmail();
        this.countryCode = patient.getCountryCode();
        this.phoneNumber = patient.getPhone();

        // ✅ Extract address safely
        if (patient.getAddressDTO() != null && !patient.getAddressDTO().isEmpty()) {
            AddressDTO addr = patient.getAddressDTO().get(0); // primary address

            if (addr.getLine() != null && !addr.getLine().isEmpty()) {
                this.firstLane = addr.getLine().get(0);
                if (addr.getLine().size() > 1) {
                    this.secondLane = addr.getLine().get(1);
                }
            }

            this.city = addr.getCity();
//            this.state = addr.getState();
            this.pinCode = Integer.valueOf(addr.getPostalCode());
//            this.country = addr.getCountry();
        }
    }
}
