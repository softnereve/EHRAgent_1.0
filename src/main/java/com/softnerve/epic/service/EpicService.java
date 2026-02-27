package com.softnerve.epic.service;

import com.softnerve.epic.model.dto.EpicPatientResponse;
import com.softnerve.epic.model.dto.RegistrationDTO;
import org.springframework.stereotype.Service;

public interface EpicService {
    EpicPatientResponse createPatient(RegistrationDTO dto);
}
