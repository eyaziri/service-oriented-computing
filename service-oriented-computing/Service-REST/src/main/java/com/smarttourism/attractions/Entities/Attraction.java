package com.smarttourism.attractions.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attractions", 
       indexes = {
           @Index(name = "idx_city", columnList = "city"),
           @Index(name = "idx_category", columnList = "category"),
           @Index(name = "idx_active", columnList = "is_active")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;
    
    @Embedded
    private Location location;
    
    @Column(name = "entry_price")
    private Double entryPrice;
    
    @Column(name = "opening_time")
    private LocalTime openingTime;
    
    @Column(name = "closing_time")
    private LocalTime closingTime;
    
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    
    @Column(name = "current_visitors")
    private Integer currentVisitors = 0;
    
    private Double rating = 0.0;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "website_url", length = 500)
    private String websiteUrl;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "average_visit_duration")
    private Integer averageVisitDuration; // en minutes
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentVisitors == null) currentVisitors = 0;
        if (rating == null) rating = 0.0;
        if (totalReviews == null) totalReviews = 0;
        if (isActive == null) isActive = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isOpen() {
        if (openingTime == null || closingTime == null) return true;
        LocalTime now = LocalTime.now();
        return !now.isBefore(openingTime) && !now.isAfter(closingTime);
    }
    
    public Integer getAvailableSpots() {
        if (maxCapacity == null) return null;
        return Math.max(0, maxCapacity - currentVisitors);
    }
    
    public Double getOccupancyRate() {
        if (maxCapacity == null || maxCapacity == 0) return 0.0;
        return (currentVisitors.doubleValue() / maxCapacity) * 100;
    }
}