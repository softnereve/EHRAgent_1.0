package com.softnerve.epic.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlotDTO {
    private String id;
    private String start;
    private String end;
    private String status;
    private String scheduleId;
}
