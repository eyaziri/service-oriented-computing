package com.smarttourism.attractions.repository;

import com.smarttourism.attractions.Entities.Attraction;
import com.smarttourism.attractions.Entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    
    // ✅ NOUVELLES MÉTHODES - accès via location.city
    List<Attraction> findByLocationCity(String city);
    Page<Attraction> findByLocationCity(String city, Pageable pageable);
    List<Attraction> findByLocationCityAndCategory(String city, Category category);
    
    // Méthodes par catégorie
    List<Attraction> findByCategory(Category category);
    Page<Attraction> findByCategory(Category category, Pageable pageable);
    
    // Recherche par nom
    List<Attraction> findByNameContainingIgnoreCase(String name);
    
    // Attractions actives
    List<Attraction> findByIsActiveTrue();
    Page<Attraction> findByIsActiveTrue(Pageable pageable);
    Optional<Attraction> findByIdAndIsActiveTrue(Long id);
    
    // Attractions en vedette
    List<Attraction> findByIsFeaturedTrueAndIsActiveTrue();
    
    // Toutes les attractions
    Page<Attraction> findAll(Pageable pageable);
    
    // Top rated
    List<Attraction> findByIsActiveTrueOrderByRatingDesc();
    List<Attraction> findTop10ByIsActiveTrueOrderByRatingDesc();
    
    // ✅ REQUÊTE CORRIGÉE - utilise location.city
    @Query("SELECT a FROM Attraction a WHERE " +
           "(:city IS NULL OR LOWER(a.location.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:minPrice IS NULL OR a.entryPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.entryPrice <= :maxPrice) AND " +
           "(:minRating IS NULL OR a.rating >= :minRating) AND " +
           "a.isActive = true")
    Page<Attraction> findWithFilters(@Param("city") String city,
                                     @Param("category") Category category,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice,
                                     @Param("minRating") Double minRating,
                                     Pageable pageable);
    
    // ✅ REQUÊTE CORRIGÉE - utilise location.city
    @Query("SELECT DISTINCT a.location.city FROM Attraction a WHERE a.isActive = true AND a.location.city IS NOT NULL ORDER BY a.location.city")
    List<String> findAllCities();
    
    @Query("SELECT DISTINCT a.category FROM Attraction a WHERE a.isActive = true")
    List<Category> findAllCategories();
    
    // ✅ REQUÊTE CORRIGÉE - utilise location.city
    @Query("SELECT a FROM Attraction a WHERE a.isActive = true AND " +
           "(LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.location.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Attraction> search(@Param("query") String query);
    
    @Query("SELECT COUNT(a) FROM Attraction a WHERE a.isActive = true")
    Long countActiveAttractions();
}