package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bundle of clinical notes (DocumentReference)")
public class ClinicalNoteBundleDTO {
    @Schema(description = "Type of bundle", example = "searchset")
    private String type;
    @Schema(description = "Total notes", example = "3")
    private int total;
    @Schema(description = "List of clinical notes")
    private List<ClinicalNoteDTO> notes;
    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
