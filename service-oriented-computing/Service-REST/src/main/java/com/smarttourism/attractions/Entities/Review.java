package com.smarttourism.attractions.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews",
       indexes = {
           @Index(name = "idx_attraction_rating", columnList = "attraction_id, rating"),
           @Index(name = "idx_tourist_id", columnList = "tourist_id"),
           @Index(name = "idx_review_date", columnList = "review_date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    
    @Column(name = "tourist_id", nullable = false, length = 100)
    private String touristId;
    
    @Column(name = "tourist_name", nullable = false, length = 200)
    private String touristName;
    
    @Column(name = "tourist_country", length = 100)
    private String touristCountry;
    
    @Column(nullable = false)
    private Integer rating; // 1-5
    
    @Column(name = "title", length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
    
    @Column(name = "visit_date")
    private LocalDate visitDate;
    
    @Column(name = "is_verified_visit")
    private Boolean isVerifiedVisit = false;
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
    
    @Column(name = "reply", columnDefinition = "TEXT")
    private String reply;
    
    @Column(name = "replied_at")
    private LocalDateTime repliedAt;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
    @PrePersist
    protected void onCreate() {
        reviewDate = LocalDateTime.now();
        if (helpfulCount == null) helpfulCount = 0;
        if (isVerifiedVisit == null) isVerifiedVisit = false;
        if (isEdited == null) isEdited = false;
    }
    
    public void markAsHelpful() {
        this.helpfulCount++;
    }
}