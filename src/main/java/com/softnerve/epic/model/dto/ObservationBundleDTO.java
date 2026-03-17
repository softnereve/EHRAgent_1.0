package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bundle of patient observations/test results")
public class ObservationBundleDTO {

    @Schema(description = "Type of bundle", example = "searchset")
    private String type;

    @Schema(description = "Total number of observations", example = "5")
    private int total;

    @Schema(description = "List of individual observation summaries")
    private List<ObservationSummaryDTO> observations;

    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
