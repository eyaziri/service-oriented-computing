package com.smart_tourism.smart_tourism.soap.repositories;

import com.smart_tourism.smart_tourism.soap.entities.HistoricalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoricalInfoRepository extends JpaRepository<HistoricalInfoEntity, Long> {

    // Trouver par ID de monument
    Optional<HistoricalInfoEntity> findByMonumentMonumentId(String monumentId);

    // Trouver par classification officielle
    java.util.List<HistoricalInfoEntity> findByOfficialClassification(String classification);
}
