package com.softnerve.epic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailEvent implements Serializable {
    private static final long serialVersionUID = 28778322362389239L;

    private String subject;
    private String body;
    private TextType textType;
    private String fromName;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String replyTo;
    private String replyToName;
    public enum TextType{
        HTML, TEXT;
    }
}
