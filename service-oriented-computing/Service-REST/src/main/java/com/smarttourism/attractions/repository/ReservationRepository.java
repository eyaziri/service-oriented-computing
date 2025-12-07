package com.smarttourism.attractions.repository;

import com.smarttourism.attractions.Entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByTouristId(String touristId);
    List<Reservation> findByTouristIdOrderByReservationTimeDesc(String touristId);
    List<Reservation> findByAttractionIdAndVisitDate(Long attractionId, LocalDate visitDate);
    List<Reservation> findByAttractionId(Long attractionId);
    
    // AJOUTER CETTE MÉTHODE
    Long countByAttractionId(Long attractionId);
    
    // AJOUTER CETTE MÉTHODE AUSSI (pour compter par statut)
    Long countByAttractionIdAndStatus(Long attractionId, Reservation.ReservationStatus status);
    
    Optional<Reservation> findByReservationCode(String reservationCode);
    
    @Query("SELECT r FROM Reservation r WHERE r.attraction.id = :attractionId AND " +
           "r.visitDate = :visitDate AND r.status = 'CONFIRMED'")
    List<Reservation> findConfirmedReservationsForDate(@Param("attractionId") Long attractionId,
                                                      @Param("visitDate") LocalDate visitDate);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.attraction.id = :attractionId AND " +
           "r.visitDate = :visitDate AND r.status = 'CONFIRMED'")
    Long countConfirmedReservationsForDate(@Param("attractionId") Long attractionId,
                                          @Param("visitDate") LocalDate visitDate);
    
    @Query("SELECT SUM(r.numberOfPeople) FROM Reservation r WHERE r.attraction.id = :attractionId AND " +
           "r.visitDate = :visitDate AND r.status = 'CONFIRMED'")
    Integer sumConfirmedVisitorsForDate(@Param("attractionId") Long attractionId,
                                       @Param("visitDate") LocalDate visitDate);
    
    Page<Reservation> findByTouristId(String touristId, Pageable pageable);
    Page<Reservation> findByAttractionId(Long attractionId, Pageable pageable);
    
    @Query("SELECT r FROM Reservation r WHERE r.visitDate >= :startDate AND r.visitDate <= :endDate")
    List<Reservation> findReservationsBetweenDates(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.attraction.id = :attractionId AND " +
           "r.status = 'COMPLETED'")
    Long countCompletedReservations(@Param("attractionId") Long attractionId);
    
    boolean existsByTouristIdAndAttractionIdAndVisitDate(String touristId, Long attractionId, LocalDate visitDate);
}