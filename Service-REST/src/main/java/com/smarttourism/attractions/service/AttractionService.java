package com.smarttourism.attractions.service;

import com.smarttourism.attractions.dto.AttractionDTO;
import com.smarttourism.attractions.dto.requests.CreateAttractionRequest;
import com.smarttourism.attractions.Entities.Attraction;
import com.smarttourism.attractions.Entities.Category;
import com.smarttourism.attractions.exception.BusinessException;
import com.smarttourism.attractions.exception.ErrorCode;
import com.smarttourism.attractions.exception.ResourceNotFoundException;
import com.smarttourism.attractions.exception.ValidationException;
import com.smarttourism.attractions.repository.AttractionRepository;
import com.smarttourism.attractions.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionService {
    
    private final AttractionRepository attractionRepository;
    private final ReviewRepository reviewRepository;
    
    @Transactional
    public AttractionDTO createAttraction(CreateAttractionRequest request) {
        log.info("Création d'une nouvelle attraction: {}", request.getName());
        
        validateAttractionRequest(request);
        
        Attraction attraction = new Attraction();
        mapRequestToAttraction(request, attraction);
        
        try {
            Attraction savedAttraction = attractionRepository.save(attraction);
            log.info("Attraction créée avec ID: {}", savedAttraction.getId());
            return convertToDTO(savedAttraction);
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'attraction", e);
            throw new BusinessException(ErrorCode.ATTRACTION_ALREADY_EXISTS,
                "Erreur lors de la création de l'attraction: " + e.getMessage());
        }
    }
    
    public AttractionDTO getAttractionById(Long id) {
        log.debug("Récupération de l'attraction avec ID: {}", id);
        
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    id
                ));
        
        if (!attraction.getIsActive()) {
            throw new BusinessException(ErrorCode.ATTRACTION_INACTIVE,
                "L'attraction est actuellement inactive");
        }
        
        return convertToDTO(attraction);
    }
    
    public List<AttractionDTO> getAllAttractions() {
        log.debug("Récupération de toutes les attractions");
        return attractionRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<AttractionDTO> getAllAttractions(Pageable pageable) {
        log.debug("Récupération des attractions avec pagination");
        return attractionRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }
    
    public List<AttractionDTO> getAttractionsByCity(String city) {
        log.debug("Récupération des attractions pour la ville: {}", city);
        List<Attraction> attractions = attractionRepository.findByLocationCity(city);
        return attractions.stream()
                .filter(Attraction::getIsActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<AttractionDTO> getAttractionsByCity(String city, Pageable pageable) {
        log.debug("Récupération des attractions pour la ville {} avec pagination", city);
        return attractionRepository.findByLocationCity(city, pageable)
                .map(this::convertToDTO);
    }
    
    public List<AttractionDTO> getAttractionsByCategory(Category category) {
        log.debug("Récupération des attractions de catégorie: {}", category);
        List<Attraction> attractions = attractionRepository.findByCategory(category);
        return attractions.stream()
                .filter(Attraction::getIsActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<AttractionDTO> searchAttractions(String city, Category category, 
                                                Double minPrice, Double maxPrice,
                                                Double minRating, Pageable pageable) {
        log.debug("Recherche d'attractions avec filtres: city={}, category={}", city, category);
        return attractionRepository.findWithFilters(city, category, minPrice, maxPrice, minRating, pageable)
                .map(this::convertToDTO);
    }
    
    public List<AttractionDTO> getTopRatedAttractions() {
        log.debug("Récupération des attractions les mieux notées");
        return attractionRepository.findTop10ByIsActiveTrueOrderByRatingDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AttractionDTO> getFeaturedAttractions() {
        log.debug("Récupération des attractions en vedette");
        return attractionRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<String> getAllCities() {
        log.debug("Récupération de toutes les villes");
        return attractionRepository.findAllCities();
    }
    
    public List<Category> getAllCategories() {
        log.debug("Récupération de toutes les catégories");
        return attractionRepository.findAllCategories();
    }
    
    @Transactional
    public AttractionDTO updateAttraction(Long id, CreateAttractionRequest request) {
        log.info("Mise à jour de l'attraction avec ID: {}", id);
        
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    id
                ));
        
        validateAttractionRequest(request);
        mapRequestToAttraction(request, attraction);
        
        Attraction updatedAttraction = attractionRepository.save(attraction);
        log.info("Attraction mise à jour avec ID: {}", updatedAttraction.getId());
        return convertToDTO(updatedAttraction);
    }
    
    @Transactional
    public void deleteAttraction(Long id) {
        log.info("Suppression (désactivation) de l'attraction avec ID: {}", id);
        
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    id
                ));
        
        if (!attraction.getIsActive()) {
            throw new BusinessException(ErrorCode.ATTRACTION_INACTIVE,
                "L'attraction est déjà inactive");
        }
        
        attraction.setIsActive(false);
        attractionRepository.save(attraction);
        log.info("Attraction désactivée avec ID: {}", id);
    }
    
    @Transactional
    public void updateCurrentVisitors(Long attractionId, Integer visitorCount) {
        log.debug("Mise à jour des visiteurs pour l'attraction ID {}: {}", attractionId, visitorCount);
        
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorCode.ATTRACTION_NOT_FOUND,
                    "Attraction",
                    "id",
                    attractionId
                ));
        
        if (!attraction.getIsActive()) {
            throw new BusinessException(ErrorCode.ATTRACTION_INACTIVE,
                "Impossible de mettre à jour les visiteurs: attraction inactive");
        }
        
        if (attraction.getMaxCapacity() != null && visitorCount > attraction.getMaxCapacity()) {
            throw new BusinessException(ErrorCode.ATTRACTION_FULL,
                String.format("Nombre de visiteurs (%d) dépasse la capacité maximale (%d)",
                    visitorCount, attraction.getMaxCapacity()));
        }
        
        if (visitorCount < 0) {
            throw new ValidationException(ErrorCode.INVALID_CAPACITY,
                "visitorCount", "Le nombre de visiteurs ne peut pas être négatif");
        }
        
        attraction.setCurrentVisitors(visitorCount);
        attractionRepository.save(attraction);
        log.info("Visiteurs mis à jour pour l'attraction ID {}: {}", attractionId, visitorCount);
    }
    
    public List<AttractionDTO> searchAttractions(String query) {
        log.debug("Recherche d'attractions avec la requête: {}", query);
        return attractionRepository.search(query).stream()
                .filter(Attraction::getIsActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getStatistics() {
        log.debug("Récupération des statistiques des attractions");
        
        Long totalAttractions = attractionRepository.countActiveAttractions();
        List<String> cities = attractionRepository.findAllCities();
        List<Category> categories = attractionRepository.findAllCategories();
        
        return Map.of(
            "totalAttractions", totalAttractions,
            "totalCities", cities.size(),
            "totalCategories", categories.size(),
            "cities", cities,
            "categories", categories.stream()
                .map(Enum::name)
                .collect(Collectors.toList())
        );
    }
    
    private void validateAttractionRequest(CreateAttractionRequest request) {
        if (request.getMaxCapacity() != null && request.getMaxCapacity() < 0) {
            throw new ValidationException(ErrorCode.INVALID_CAPACITY,
                "maxCapacity", "La capacité doit être positive ou nulle");
        }
        
        if (request.getEntryPrice() != null && request.getEntryPrice() < 0) {
            throw new ValidationException(ErrorCode.INVALID_PRICE,
                "entryPrice", "Le prix ne peut pas être négatif");
        }
        
        if (request.getAverageVisitDuration() != null && request.getAverageVisitDuration() <= 0) {
            throw new ValidationException(ErrorCode.INVALID_PARAMETERS,
                "averageVisitDuration", "La durée moyenne doit être positive");
        }
    }
    
    private void mapRequestToAttraction(CreateAttractionRequest request, Attraction attraction) {
        attraction.setName(request.getName());
        // ❌ SUPPRIMER: attraction.setCity(request.getCity());
        // Le city est maintenant dans location
        attraction.setDescription(request.getDescription());
        attraction.setCategory(request.getCategory());
        attraction.setLocation(request.getLocation());
        attraction.setEntryPrice(request.getEntryPrice());
        attraction.setOpeningTime(request.getOpeningTime());
        attraction.setClosingTime(request.getClosingTime());
        attraction.setMaxCapacity(request.getMaxCapacity());
        attraction.setImageUrl(request.getImageUrl());
        attraction.setWebsiteUrl(request.getWebsiteUrl());
        attraction.setPhoneNumber(request.getPhoneNumber());
        attraction.setEmail(request.getEmail());
        attraction.setAverageVisitDuration(request.getAverageVisitDuration());
        attraction.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
    }
    
    private AttractionDTO convertToDTO(Attraction attraction) {
        AttractionDTO dto = new AttractionDTO();
        dto.setId(attraction.getId());
        dto.setName(attraction.getName());
        // ✅ CORRECTION: accéder à city via location
        dto.setCity(attraction.getLocation() != null ? attraction.getLocation().getCity() : null);
        dto.setDescription(attraction.getDescription());
        dto.setCategory(attraction.getCategory());
        dto.setLocation(attraction.getLocation());
        dto.setEntryPrice(attraction.getEntryPrice());
        dto.setOpeningTime(attraction.getOpeningTime());
        dto.setClosingTime(attraction.getClosingTime());
        dto.setMaxCapacity(attraction.getMaxCapacity());
        dto.setCurrentVisitors(attraction.getCurrentVisitors());
        dto.setAvailableSpots(attraction.getAvailableSpots());
        dto.setOccupancyRate(attraction.getOccupancyRate());
        dto.setRating(attraction.getRating());
        dto.setTotalReviews(attraction.getTotalReviews());
        dto.setImageUrl(attraction.getImageUrl());
        dto.setWebsiteUrl(attraction.getWebsiteUrl());
        dto.setPhoneNumber(attraction.getPhoneNumber());
        dto.setEmail(attraction.getEmail());
        dto.setAverageVisitDuration(attraction.getAverageVisitDuration());
        dto.setIsActive(attraction.getIsActive());
        dto.setIsFeatured(attraction.getIsFeatured());
        dto.setIsOpen(attraction.isOpen());
        
        // Calculer la distribution des notes
        if (attraction.getId() != null) {
            Map<Integer, Long> ratingDistribution = calculateRatingDistribution(attraction.getId());
            dto.setRatingDistribution1(ratingDistribution.getOrDefault(1, 0L).intValue());
            dto.setRatingDistribution2(ratingDistribution.getOrDefault(2, 0L).intValue());
            dto.setRatingDistribution3(ratingDistribution.getOrDefault(3, 0L).intValue());
            dto.setRatingDistribution4(ratingDistribution.getOrDefault(4, 0L).intValue());
            dto.setRatingDistribution5(ratingDistribution.getOrDefault(5, 0L).intValue());
        }
        
        return dto;
    }
    
    private Map<Integer, Long> calculateRatingDistribution(Long attractionId) {
        return Map.of(
            1, reviewRepository.countByAttractionIdAndRating(attractionId, 1),
            2, reviewRepository.countByAttractionIdAndRating(attractionId, 2),
            3, reviewRepository.countByAttractionIdAndRating(attractionId, 3),
            4, reviewRepository.countByAttractionIdAndRating(attractionId, 4),
            5, reviewRepository.countByAttractionIdAndRating(attractionId, 5)
        );
    }
}