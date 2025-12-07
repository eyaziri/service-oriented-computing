package com.smarttourism.attractions.service;

import com.smarttourism.attractions.dto.ReviewDTO;
import com.smarttourism.attractions.dto.requests.CreateReviewRequest;
import com.smarttourism.attractions.Entities.Attraction;
import com.smarttourism.attractions.Entities.Reservation;
import com.smarttourism.attractions.Entities.Review;
import com.smarttourism.attractions.exception.BusinessException;
import com.smarttourism.attractions.exception.ErrorCode;
import com.smarttourism.attractions.exception.ResourceNotFoundException;
import com.smarttourism.attractions.exception.ValidationException;
import com.smarttourism.attractions.repository.AttractionRepository;
import com.smarttourism.attractions.repository.ReservationRepository;
import com.smarttourism.attractions.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final AttractionRepository attractionRepository;
    private final ReservationRepository reservationRepository;
    
    @Transactional
    public ReviewDTO createReview(CreateReviewRequest request) {
        log.info("Création d'un avis pour l'attraction ID: {} par le touriste ID: {}", 
                request.getAttractionId(), request.getTouristId());
        
        validateReviewRequest(request);
        
        Attraction attraction = attractionRepository.findById(request.getAttractionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    request.getAttractionId()
                ));
        
        if (!attraction.getIsActive()) {
            throw new BusinessException(ErrorCode.ATTRACTION_INACTIVE,
                "Impossible de poster un avis pour une attraction inactive");
        }
        
        // Vérifier si l'utilisateur a déjà posté un avis pour cette attraction
        boolean existingReview = reviewRepository.existsByTouristIdAndAttractionId(
            request.getTouristId(), attraction.getId());
        
        if (existingReview) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS,
                "Vous avez déjà posté un avis pour cette attraction");
        }
        
        Review review = new Review();
        mapRequestToReview(request, attraction, review);
        
        // Si une réservation est fournie, vérifier et marquer comme visite vérifiée
        if (request.getReservationId() != null) {
            Reservation reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Réservation",
                        "id",
                        request.getReservationId()
                    ));
            
            if (!reservation.getTouristId().equals(request.getTouristId())) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                    "Cette réservation ne vous appartient pas");
            }
            
            if (reservation.getStatus() != Reservation.ReservationStatus.COMPLETED) {
                throw new BusinessException(ErrorCode.RESERVATION_CONFLICT,
                    "Seules les réservations terminées peuvent être notées");
            }
            
            review.setReservation(reservation);
            review.setIsVerifiedVisit(true);
        }
        
        try {
            Review savedReview = reviewRepository.save(review);
            
            // Mettre à jour la note moyenne de l'attraction
            updateAttractionRating(attraction);
            
            log.info("Avis créé avec ID: {}", savedReview.getId());
            return convertToDTO(savedReview);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'avis", e);
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS,
                "Erreur lors de la création de l'avis: " + e.getMessage());
        }
    }
    
    public ReviewDTO getReviewById(Long id) {
        log.debug("Récupération de l'avis avec ID: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.REVIEW_NOT_FOUND,
                    "Avis",
                    "id",
                    id
                ));
        
        return convertToDTO(review);
    }
    
    public List<ReviewDTO> getReviewsByAttractionId(Long attractionId) {
        log.debug("Récupération des avis pour l'attraction ID: {}", attractionId);
        return reviewRepository.findByAttractionId(attractionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDTO> getReviewsByAttractionId(Long attractionId, Pageable pageable) {
        log.debug("Récupération des avis pour l'attraction ID {} avec pagination", attractionId);
        return reviewRepository.findByAttractionId(attractionId, pageable)
                .map(this::convertToDTO);
    }
    
    public Page<ReviewDTO> getReviewsByAttractionIdSorted(Long attractionId, String sortBy, Pageable pageable) {
        log.debug("Récupération des avis triés pour l'attraction ID {}: {}", attractionId, sortBy);
        return reviewRepository.findByAttractionIdSorted(attractionId, sortBy, pageable)
                .map(this::convertToDTO);
    }
    
    public List<ReviewDTO> getReviewsByTouristId(String touristId) {
        log.debug("Récupération des avis du touriste ID: {}", touristId);
        return reviewRepository.findByTouristId(touristId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDTO> getReviewsByTouristId(String touristId, Pageable pageable) {
        log.debug("Récupération des avis du touriste ID {} avec pagination", touristId);
        return reviewRepository.findByTouristId(touristId, pageable)
                .map(this::convertToDTO);
    }
    
    public List<ReviewDTO> getVerifiedReviewsByAttractionId(Long attractionId) {
        log.debug("Récupération des avis vérifiés pour l'attraction ID: {}", attractionId);
        return reviewRepository.findVerifiedReviewsByAttractionId(attractionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReviewDTO updateReview(Long id, CreateReviewRequest request) {
        log.info("Mise à jour de l'avis avec ID: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.REVIEW_NOT_FOUND,
                    "Avis",
                    "id",
                    id
                ));
        
        if (!review.getTouristId().equals(request.getTouristId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED,
                "Vous n'êtes pas autorisé à modifier cet avis");
        }
        
        // Vérifier si l'avis peut être modifié (délai de 24h par exemple)
        if (review.getReviewDate().plusHours(24).isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.REVIEW_UPDATE_NOT_ALLOWED,
                "Impossible de modifier l'avis après 24 heures");
        }
        
        validateReviewRequest(request);
        
        // Sauvegarder l'ancienne note pour mettre à jour la moyenne
        Integer oldRating = review.getRating();
        
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setIsEdited(true);
        review.setEditedAt(java.time.LocalDateTime.now());
        
        Review updatedReview = reviewRepository.save(review);
        
        // Mettre à jour la note moyenne de l'attraction
        updateAttractionRating(review.getAttraction());
        
        log.info("Avis mis à jour avec ID: {}", updatedReview.getId());
        return convertToDTO(updatedReview);
    }
    
    @Transactional
    public void deleteReview(Long id, String touristId) {
        log.info("Suppression de l'avis avec ID: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.REVIEW_NOT_FOUND,
                    "Avis",
                    "id",
                    id
                ));
        
        if (!review.getTouristId().equals(touristId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED,
                "Vous n'êtes pas autorisé à supprimer cet avis");
        }
        
        Attraction attraction = review.getAttraction();
        reviewRepository.delete(review);
        
        // Mettre à jour la note moyenne de l'attraction
        updateAttractionRating(attraction);
        
        log.info("Avis supprimé avec ID: {}", id);
    }
    
    @Transactional
    public ReviewDTO markReviewAsHelpful(Long id) {
        log.info("Marquage de l'avis ID {} comme utile", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.REVIEW_NOT_FOUND,
                    "Avis",
                    "id",
                    id
                ));
        
        review.markAsHelpful();
        Review updatedReview = reviewRepository.save(review);
        
        log.info("Avis ID {} marqué comme utile. Total: {}", id, updatedReview.getHelpfulCount());
        return convertToDTO(updatedReview);
    }
    
    @Transactional
    public ReviewDTO addReplyToReview(Long id, String reply) {
        log.info("Ajout d'une réponse à l'avis ID: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.REVIEW_NOT_FOUND,
                    "Avis",
                    "id",
                    id
                ));
        
        review.setReply(reply);
        review.setRepliedAt(java.time.LocalDateTime.now());
        Review updatedReview = reviewRepository.save(review);
        
        log.info("Réponse ajoutée à l'avis ID: {}", id);
        return convertToDTO(updatedReview);
    }
    
    public Map<String, Object> getReviewStatistics(Long attractionId) {
        log.debug("Récupération des statistiques d'avis pour l'attraction ID: {}", attractionId);
        
        Double averageRating = reviewRepository.calculateAverageRating(attractionId);
        Long totalReviews = reviewRepository.countByAttractionId(attractionId);
        Long verifiedReviews = (long) reviewRepository.findVerifiedReviewsByAttractionId(attractionId).size();
        
        Map<Integer, Long> ratingDistribution = Map.of(
            1, reviewRepository.countByAttractionIdAndRating(attractionId, 1),
            2, reviewRepository.countByAttractionIdAndRating(attractionId, 2),
            3, reviewRepository.countByAttractionIdAndRating(attractionId, 3),
            4, reviewRepository.countByAttractionIdAndRating(attractionId, 4),
            5, reviewRepository.countByAttractionIdAndRating(attractionId, 5)
        );
        
        return Map.of(
            "averageRating", averageRating != null ? averageRating : 0.0,
            "totalReviews", totalReviews != null ? totalReviews : 0,
            "verifiedReviews", verifiedReviews,
            "ratingDistribution", ratingDistribution
        );
    }
    
    private void validateReviewRequest(CreateReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException(ErrorCode.INVALID_RATING,
                "rating", "La note doit être entre 1 et 5");
        }
        
        if (request.getVisitDate() != null && request.getVisitDate().isAfter(LocalDate.now())) {
            throw new ValidationException(ErrorCode.INVALID_DATE,
                "visitDate", "La date de visite ne peut pas être dans le futur");
        }
    }
    
    private void mapRequestToReview(CreateReviewRequest request, Attraction attraction, Review review) {
        review.setAttraction(attraction);
        review.setTouristId(request.getTouristId());
        review.setTouristName(request.getTouristName());
        review.setTouristCountry(request.getTouristCountry());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setVisitDate(request.getVisitDate());
    }
    
    @Transactional
    protected void updateAttractionRating(Attraction attraction) {
        Double newAverageRating = reviewRepository.calculateAverageRating(attraction.getId());
        Long totalReviews = reviewRepository.countByAttractionId(attraction.getId());
        
        attraction.setRating(newAverageRating != null ? newAverageRating : 0.0);
        attraction.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        attractionRepository.save(attraction);
        
        log.debug("Note moyenne de l'attraction {} mise à jour: {}", attraction.getId(), newAverageRating);
    }
    
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setAttractionId(review.getAttraction().getId());
        dto.setAttractionName(review.getAttraction().getName());
        if (review.getReservation() != null) {
            dto.setReservationId(review.getReservation().getId());
        }
        dto.setTouristId(review.getTouristId());
        dto.setTouristName(review.getTouristName());
        dto.setTouristCountry(review.getTouristCountry());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setReviewDate(review.getReviewDate());
        dto.setVisitDate(review.getVisitDate());
        dto.setIsVerifiedVisit(review.getIsVerifiedVisit());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setReply(review.getReply());
        dto.setRepliedAt(review.getRepliedAt());
        dto.setIsEdited(review.getIsEdited());
        dto.setEditedAt(review.getEditedAt());
        return dto;
    }
}