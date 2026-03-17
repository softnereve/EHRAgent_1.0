package com.softnerve.epic.utils;

import com.softnerve.epic.model.dto.SlotDTO;
import org.hl7.fhir.r4.model.Slot;

public class SlotMapper {

    public static SlotDTO toDto(Slot fhirSlot) {
        String scheduleId = null;
        if (fhirSlot.getSchedule() != null && fhirSlot.getSchedule().getReferenceElement() != null) {
            scheduleId = fhirSlot.getSchedule().getReferenceElement().getIdPart();
        }
        return SlotDTO.builder()
                .id(fhirSlot.getIdElement().getIdPart())
                .start(fhirSlot.getStartElement().asStringValue())
                .end(fhirSlot.getEndElement().asStringValue())
                .status(fhirSlot.getStatus().toCode())
                .scheduleId(scheduleId)
                .build();
    }
}