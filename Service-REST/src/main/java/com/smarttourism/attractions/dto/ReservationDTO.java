package com.smarttourism.attractions.dto;

import com.smarttourism.attractions.Entities.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private String reservationCode;
    private Long attractionId;
    private String attractionName;
    private String touristId;
    private String touristName;
    private String touristEmail;
    private String touristPhone;
    private String touristCountry;
    private LocalDate visitDate;
    private String visitTime;
    private LocalDateTime reservationTime;
    private Integer numberOfPeople;
    private Reservation.ReservationStatus status;
    private Double totalPrice;
    private String specialRequirements;
    private String qrCodeUrl;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String notes;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
}