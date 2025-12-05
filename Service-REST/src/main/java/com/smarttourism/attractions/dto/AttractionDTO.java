package com.smarttourism.attractions.dto;

import com.smarttourism.attractions.Entities.Category;
import com.smarttourism.attractions.Entities.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDTO {
    private Long id;
    private String name;
    private String city;
    private String description;
    private Category category;
    private Location location;
    private Double entryPrice;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Integer maxCapacity;
    private Integer currentVisitors;
    private Integer availableSpots;
    private Double occupancyRate;
    private Double rating;
    private Integer totalReviews;
    private String imageUrl;
    private String websiteUrl;
    private String phoneNumber;
    private String email;
    private Integer averageVisitDuration;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isOpen;
    private Integer ratingDistribution1;
    private Integer ratingDistribution2;
    private Integer ratingDistribution3;
    private Integer ratingDistribution4;
    private Integer ratingDistribution5;
}