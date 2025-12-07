package com.smart_tourism.smart_tourism.soap.repositories;

import com.smart_tourism.smart_tourism.soap.entities.TouristStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TouristStatsRepository extends JpaRepository<TouristStatsEntity, Long> {

    // Trouver par région et année (utilise l'index uq_region_year)
    Optional<TouristStatsEntity> findByRegionAndYear(String region, Short year);

    // Trouver toutes les stats d'une région (utilise l'index idx_tourist_region)
    List<TouristStatsEntity> findByRegionOrderByYearDesc(String region);

    // Trouver avec les stats mensuelles
    @Query("SELECT ts FROM TouristStatsEntity ts LEFT JOIN FETCH ts.monthlyStats " +
            "WHERE ts.region = :region AND ts.year = :year")
    Optional<TouristStatsEntity> findByRegionAndYearWithMonthlyStats(
            @Param("region") String region,
            @Param("year") Short year);

    // Stats par année
    List<TouristStatsEntity> findByYear(Short year);
}
