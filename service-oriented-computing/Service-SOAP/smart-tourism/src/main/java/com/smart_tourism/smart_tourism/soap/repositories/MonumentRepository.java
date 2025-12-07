package com.smart_tourism.smart_tourism.soap.repositories;

import com.smart_tourism.smart_tourism.soap.entities.MonumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonumentRepository extends JpaRepository<MonumentEntity, String> {

    // Trouver par ville (utilise l'index idx_monument_city)
    List<MonumentEntity> findByCity(String city);

    // Trouver par période historique
    List<MonumentEntity> findByHistoricalPeriod(String historicalPeriod);

    // Trouver les monuments UNESCO
    List<MonumentEntity> findByUnescoHeritageTrue();

    // Trouver avec informations historiques (chargement eager)
    @Query("SELECT m FROM MonumentEntity m LEFT JOIN FETCH m.historicalInfo " +
            "WHERE m.monumentId = :id")
    Optional<MonumentEntity> findByIdWithHistoricalInfo(@Param("id") String id);

    // Trouver avec restaurations
    @Query("SELECT m FROM MonumentEntity m LEFT JOIN FETCH m.restorationHistories " +
            "WHERE m.monumentId = :id")
    Optional<MonumentEntity> findByIdWithRestorations(@Param("id") String id);

    // Recherche par nom (insensible à la casse)
    List<MonumentEntity> findByNameContainingIgnoreCase(String name);
}
