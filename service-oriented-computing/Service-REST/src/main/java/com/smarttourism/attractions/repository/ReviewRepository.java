package com.smarttourism.attractions.repository;

import com.smarttourism.attractions.Entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByAttractionId(Long attractionId);
    List<Review> findByTouristId(String touristId);
    List<Review> findByRatingGreaterThanEqual(Integer minRating);
    List<Review> findByAttractionIdAndRating(Long attractionId, Integer rating);
    
    Page<Review> findByAttractionId(Long attractionId, Pageable pageable);
    Page<Review> findByTouristId(String touristId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.attraction.id = :attractionId ORDER BY " +
           "CASE WHEN :sortBy = 'helpful' THEN r.helpfulCount END DESC, " +
           "CASE WHEN :sortBy = 'rating' THEN r.rating END DESC, " +
           "r.reviewDate DESC")
    Page<Review> findByAttractionIdSorted(@Param("attractionId") Long attractionId,
                                         @Param("sortBy") String sortBy,
                                         Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.attraction.id = :attractionId")
    Double calculateAverageRating(@Param("attractionId") Long attractionId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.attraction.id = :attractionId")
    Long countByAttractionId(@Param("attractionId") Long attractionId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.attraction.id = :attractionId AND r.rating = :rating")
    Long countByAttractionIdAndRating(@Param("attractionId") Long attractionId,
                                     @Param("rating") Integer rating);
    
    Optional<Review> findByTouristIdAndAttractionId(String touristId, Long attractionId);
    
    boolean existsByTouristIdAndAttractionId(String touristId, Long attractionId);
    
    @Query("SELECT r FROM Review r WHERE r.isVerifiedVisit = true AND r.attraction.id = :attractionId")
    List<Review> findVerifiedReviewsByAttractionId(@Param("attractionId") Long attractionId);
}