package com.smarttourism.attractions.service;

import com.smarttourism.attractions.dto.ReservationDTO;
import com.smarttourism.attractions.dto.requests.CreateReservationRequest;
import com.smarttourism.attractions.Entities.Attraction;
import com.smarttourism.attractions.Entities.Reservation;
import com.smarttourism.attractions.exception.BusinessException;
import com.smarttourism.attractions.exception.ErrorCode;
import com.smarttourism.attractions.exception.ResourceNotFoundException;
import com.smarttourism.attractions.exception.ValidationException;
import com.smarttourism.attractions.repository.AttractionRepository;
import com.smarttourism.attractions.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final AttractionRepository attractionRepository;
    
    @Transactional
    public ReservationDTO createReservation(CreateReservationRequest request) {
        log.info("Création d'une réservation pour l'attraction ID: {}", request.getAttractionId());
        
        validateReservationRequest(request);
        
        Attraction attraction = attractionRepository.findById(request.getAttractionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    request.getAttractionId()
                ));
        
        if (!attraction.getIsActive()) {
            throw new BusinessException(ErrorCode.ATTRACTION_INACTIVE,
                "Impossible de réserver: l'attraction est inactive");
        }
        
        if (attraction.getMaxCapacity() != null) {
            Integer totalVisitors = reservationRepository.sumConfirmedVisitorsForDate(
                attraction.getId(), request.getVisitDate());
            totalVisitors = totalVisitors != null ? totalVisitors : 0;
            
            if (totalVisitors + request.getNumberOfPeople() > attraction.getMaxCapacity()) {
                throw new BusinessException(ErrorCode.NO_AVAILABILITY,
                    String.format("Pas assez de places disponibles. Places restantes: %d",
                        attraction.getMaxCapacity() - totalVisitors));
            }
        }
        
        // Vérifier si l'utilisateur a déjà une réservation pour cette date
        boolean existingReservation = reservationRepository.existsByTouristIdAndAttractionIdAndVisitDate(
            request.getTouristId(), attraction.getId(), request.getVisitDate());
        
        if (existingReservation) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS,
                "Vous avez déjà une réservation pour cette attraction à cette date");
        }
        
        Reservation reservation = new Reservation();
        mapRequestToReservation(request, attraction, reservation);
        
        try {
            Reservation savedReservation = reservationRepository.save(reservation);
            log.info("Réservation créée avec code: {}", savedReservation.getReservationCode());
            return convertToDTO(savedReservation);
        } catch (Exception e) {
            log.error("Erreur lors de la création de la réservation", e);
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                "Erreur lors de la création de la réservation: " + e.getMessage());
        }
    }
    
    public ReservationDTO getReservationById(Long id) {
        log.debug("Récupération de la réservation avec ID: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        return convertToDTO(reservation);
    }
    
    public ReservationDTO getReservationByCode(String reservationCode) {
        log.debug("Récupération de la réservation avec code: {}", reservationCode);
        
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "code",
                    reservationCode
                ));
        
        return convertToDTO(reservation);
    }
    
    public List<ReservationDTO> getReservationsByTouristId(String touristId) {
        log.debug("Récupération des réservations pour le touriste ID: {}", touristId);
        return reservationRepository.findByTouristIdOrderByReservationTimeDesc(touristId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReservationDTO> getReservationsByTouristId(String touristId, Pageable pageable) {
        log.debug("Récupération des réservations pour le touriste ID {} avec pagination", touristId);
        return reservationRepository.findByTouristId(touristId, pageable)
                .map(this::convertToDTO);
    }
    
    public List<ReservationDTO> getReservationsByAttractionId(Long attractionId) {
        log.debug("Récupération des réservations pour l'attraction ID: {}", attractionId);
        return reservationRepository.findByAttractionId(attractionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReservationDTO> getReservationsByAttractionId(Long attractionId, Pageable pageable) {
        log.debug("Récupération des réservations pour l'attraction ID {} avec pagination", attractionId);
        return reservationRepository.findByAttractionId(attractionId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public ReservationDTO updateReservationStatus(Long id, Reservation.ReservationStatus newStatus) {
        log.info("Mise à jour du statut de la réservation ID {}: {}", id, newStatus);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        validateStatusTransition(reservation.getStatus(), newStatus);
        
        if (newStatus == Reservation.ReservationStatus.CANCELLED) {
            reservation.setCancelledAt(LocalDateTime.now());
            reservation.setCancellationReason("Annulée par l'utilisateur");
        }
        
        reservation.setStatus(newStatus);
        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Statut de la réservation ID {} mis à jour à: {}", id, newStatus);
        return convertToDTO(updatedReservation);
    }
    
    @Transactional
    public ReservationDTO updateReservation(Long id, CreateReservationRequest request) {
        log.info("Mise à jour de la réservation ID: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        // Vérifier que le touriste est le propriétaire
        if (!reservation.getTouristId().equals(request.getTouristId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED,
                "Vous n'êtes pas autorisé à modifier cette réservation");
        }
        
        // Vérifier que la réservation n'est pas déjà annulée ou terminée
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED ||
            reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                "Impossible de modifier une réservation annulée ou terminée");
        }
        
        // Vérifier la nouvelle date
        if (request.getVisitDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.INVALID_DATE,
                "La nouvelle date de visite doit être dans le futur");
        }
        
        // Si la date change, vérifier la disponibilité
        if (!reservation.getVisitDate().equals(request.getVisitDate())) {
            Map<String, Object> availability = checkAvailability(reservation.getAttraction().getId(), request.getVisitDate());
            if (!(Boolean) availability.get("available")) {
                throw new BusinessException(ErrorCode.NO_AVAILABILITY,
                    "Pas de disponibilité pour la nouvelle date");
            }
        }
        
        // Mettre à jour les informations
        reservation.setTouristName(request.getTouristName());
        reservation.setTouristEmail(request.getTouristEmail());
        reservation.setTouristPhone(request.getTouristPhone());
        reservation.setTouristCountry(request.getTouristCountry());
        reservation.setVisitDate(request.getVisitDate());
        reservation.setVisitTime(request.getVisitTime());
        reservation.setNumberOfPeople(request.getNumberOfPeople());
        reservation.setSpecialRequirements(request.getSpecialRequirements());
        
        // Recalculer le prix
        Double totalPrice = reservation.getAttraction().getEntryPrice() != null ? 
            reservation.getAttraction().getEntryPrice() * request.getNumberOfPeople() : 0.0;
        reservation.setTotalPrice(totalPrice);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Réservation ID {} mise à jour", id);
        return convertToDTO(updatedReservation);
    }
    
    @Transactional
    public ReservationDTO cancelReservation(Long id, String reason) {
        log.info("Annulation de la réservation ID {} avec raison: {}", id, reason);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.RESERVATION_CANCELLED,
                "La réservation est déjà annulée");
        }
        
        if (reservation.getVisitDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.RESERVATION_DATE_PASSED,
                "Impossible d'annuler une réservation dont la date est passée");
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(reason);
        
        Reservation cancelledReservation = reservationRepository.save(reservation);
        log.info("Réservation ID {} annulée", id);
        return convertToDTO(cancelledReservation);
    }
    
    @Transactional
    public ReservationDTO checkInReservation(Long id) {
        log.info("Check-in de la réservation ID: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        if (reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                "Seules les réservations confirmées peuvent être check-in");
        }
        
        if (!reservation.getVisitDate().isEqual(LocalDate.now())) {
            throw new BusinessException(ErrorCode.INVALID_DATE,
                "Le check-in n'est possible que le jour de la visite");
        }
        
        reservation.setCheckInTime(LocalDateTime.now());
        Reservation checkedInReservation = reservationRepository.save(reservation);
        log.info("Check-in effectué pour la réservation ID: {}", id);
        return convertToDTO(checkedInReservation);
    }
    
    @Transactional
    public ReservationDTO checkOutReservation(Long id) {
        log.info("Check-out de la réservation ID: {}", id);
        
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "Réservation",
                    "id",
                    id
                ));
        
        if (reservation.getCheckInTime() == null) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                "Impossible de check-out sans check-in préalable");
        }
        
        reservation.setCheckOutTime(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        Reservation checkedOutReservation = reservationRepository.save(reservation);
        log.info("Check-out effectué pour la réservation ID: {}", id);
        return convertToDTO(checkedOutReservation);
    }
    
    public Map<String, Object> getReservationStatistics(Long attractionId) {
        log.debug("Récupération des statistiques de réservation pour l'attraction ID: {}", attractionId);
        
        Long totalReservations = reservationRepository.countByAttractionId(attractionId);
        Long completedReservations = reservationRepository.countCompletedReservations(attractionId);
        
        return Map.of(
            "totalReservations", totalReservations != null ? totalReservations : 0,
            "completedReservations", completedReservations != null ? completedReservations : 0,
            "completionRate", totalReservations != null && totalReservations > 0 ? 
                (completedReservations != null ? (completedReservations.doubleValue() / totalReservations) * 100 : 0) : 0
        );
    }
    
    public Map<String, Object> checkAvailability(Long attractionId, LocalDate date) {
        log.debug("Vérification de la disponibilité pour l'attraction ID {} à la date {}", attractionId, date);
        
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    attractionId
                ));
        
        if (!attraction.getIsActive()) {
            return Map.of(
                "available", false,
                "reason", "Attraction inactive",
                "availableSpots", 0
            );
        }
        
        if (attraction.getMaxCapacity() == null) {
            return Map.of(
                "available", true,
                "reason", "Capacité illimitée",
                "availableSpots", -1 // Indique capacité illimitée
            );
        }
        
        Integer totalVisitors = reservationRepository.sumConfirmedVisitorsForDate(attractionId, date);
        totalVisitors = totalVisitors != null ? totalVisitors : 0;
        Integer availableSpots = Math.max(0, attraction.getMaxCapacity() - totalVisitors);
        
        return Map.of(
            "available", availableSpots > 0,
            "reason", availableSpots > 0 ? "Places disponibles" : "Complet",
            "availableSpots", availableSpots,
            "totalCapacity", attraction.getMaxCapacity(),
            "reservedSpots", totalVisitors
        );
    }
    
    private void validateReservationRequest(CreateReservationRequest request) {
        if (request.getVisitDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.INVALID_DATE,
                "La date de visite doit être aujourd'hui ou dans le futur");
        }
        
        if (request.getNumberOfPeople() == null || request.getNumberOfPeople() <= 0) {
            throw new ValidationException(ErrorCode.INVALID_CAPACITY,
                "numberOfPeople", "Le nombre de personnes doit être positif");
        }
        
        if (request.getNumberOfPeople() > 50) {
            throw new ValidationException(ErrorCode.RESERVATION_LIMIT_EXCEEDED,
                "numberOfPeople", "Le nombre de personnes ne peut pas dépasser 50");
        }
    }
    
    private void validateStatusTransition(Reservation.ReservationStatus currentStatus, 
                                         Reservation.ReservationStatus newStatus) {
        if (currentStatus == Reservation.ReservationStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.RESERVATION_CANCELLED,
                "Impossible de modifier une réservation annulée");
        }
        
        if (currentStatus == Reservation.ReservationStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                "Impossible de modifier une réservation terminée");
        }
    }
    
    private void mapRequestToReservation(CreateReservationRequest request, Attraction attraction, 
                                        Reservation reservation) {
        reservation.setAttraction(attraction);
        reservation.setTouristId(request.getTouristId());
        reservation.setTouristName(request.getTouristName());
        reservation.setTouristEmail(request.getTouristEmail());
        reservation.setTouristPhone(request.getTouristPhone());
        reservation.setTouristCountry(request.getTouristCountry());
        reservation.setVisitDate(request.getVisitDate());
        reservation.setVisitTime(request.getVisitTime());
        reservation.setNumberOfPeople(request.getNumberOfPeople());
        reservation.setSpecialRequirements(request.getSpecialRequirements());
        
        // Calculer le prix total
        Double totalPrice = attraction.getEntryPrice() != null ? 
            attraction.getEntryPrice() * request.getNumberOfPeople() : 0.0;
        reservation.setTotalPrice(totalPrice);
    }
    
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationCode(reservation.getReservationCode());
        dto.setAttractionId(reservation.getAttraction().getId());
        dto.setAttractionName(reservation.getAttraction().getName());
        dto.setTouristId(reservation.getTouristId());
        dto.setTouristName(reservation.getTouristName());
        dto.setTouristEmail(reservation.getTouristEmail());
        dto.setTouristPhone(reservation.getTouristPhone());
        dto.setTouristCountry(reservation.getTouristCountry());
        dto.setVisitDate(reservation.getVisitDate());
        dto.setVisitTime(reservation.getVisitTime());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setNumberOfPeople(reservation.getNumberOfPeople());
        dto.setStatus(reservation.getStatus());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setSpecialRequirements(reservation.getSpecialRequirements());
        dto.setQrCodeUrl(reservation.getQrCodeUrl());
        dto.setCheckInTime(reservation.getCheckInTime());
        dto.setCheckOutTime(reservation.getCheckOutTime());
        dto.setNotes(reservation.getNotes());
        dto.setCancellationReason(reservation.getCancellationReason());
        dto.setCancelledAt(reservation.getCancelledAt());
        return dto;
    }
}