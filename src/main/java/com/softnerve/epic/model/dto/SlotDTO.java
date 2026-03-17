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
    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}