package com.softnerve.epic.model.dao;


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

import java.util.Date;
import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "immunization_chart")
public class ImmunizationChart {

    @Id
    @Field("_id")
    private ObjectId id;

    private String immunizationId;

    private String immunizationName;

    private Integer startBirthMonth;

    private Integer endBirthMonth;

    private Integer priorityForSeq;

    private Integer durationAfterInDays;

    private Double immunizationCost;

    private String companyName;

    private ImmunizationType immunizationType;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public enum ImmunizationType {
        BOOSTER, NORMAL
    }


    // Method to generate a random unique immunizationId
    public static String generateImmunizationId() {
        final int length = 16;
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }
}


