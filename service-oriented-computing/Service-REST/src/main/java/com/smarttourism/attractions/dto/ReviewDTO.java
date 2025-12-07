package com.smarttourism.attractions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long attractionId;
    private String attractionName;
    private Long reservationId;
    private String touristId;
    private String touristName;
    private String touristCountry;
    private Integer rating;
    private String title;
    private String comment;
    private LocalDateTime reviewDate;
    private LocalDate visitDate;
    private Boolean isVerifiedVisit;
    private Integer helpfulCount;
    private String reply;
    private LocalDateTime repliedAt;
    private Boolean isEdited;
    private LocalDateTime editedAt;
}