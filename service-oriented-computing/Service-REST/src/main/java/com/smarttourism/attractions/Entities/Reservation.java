package com.smarttourism.attractions.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations",
       indexes = {
           @Index(name = "idx_tourist_id", columnList = "tourist_id"),
           @Index(name = "idx_visit_date", columnList = "visit_date"),
           @Index(name = "idx_status", columnList = "status")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reservation_code", unique = true, nullable = false, length = 50)
    private String reservationCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;
    
    @Column(name = "tourist_id", nullable = false, length = 100)
    private String touristId;
    
    @Column(name = "tourist_name", nullable = false, length = 200)
    private String touristName;
    
    @Column(name = "tourist_email", nullable = false, length = 200)
    private String touristEmail;
    
    @Column(name = "tourist_phone", length = 20)
    private String touristPhone;
    
    @Column(name = "tourist_country", length = 100)
    private String touristCountry;
    
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;
    
    @Column(name = "visit_time")
    private String visitTime; // "MORNING", "AFTERNOON", "FULL_DAY" ou horaire spécifique
    
    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;
    
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReservationStatus status;
    
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;
    
    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @PrePersist
    protected void onCreate() {
        reservationTime = LocalDateTime.now();
        if (reservationCode == null) {
            reservationCode = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (status == null) {
            status = ReservationStatus.CONFIRMED;
        }
    }
    
    public enum ReservationStatus {
        CONFIRMED("Confirmée"),
        PENDING("En attente"),
        CANCELLED("Annulée"),
        COMPLETED("Terminée"),
        NO_SHOW("Non présent");
        
        private final String displayName;
        
        ReservationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}