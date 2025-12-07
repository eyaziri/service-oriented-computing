package com.smart_tourism.smart_tourism.soap.repositories;

import com.smart_tourism.smart_tourism.soap.entities.RestorationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestorationHistoryRepository extends JpaRepository<RestorationHistoryEntity, Long> {

    @Query("SELECT r FROM RestorationHistoryEntity r WHERE r.monument.monumentId = :monumentId " +
            "ORDER BY r.restorationDate DESC")
    List<RestorationHistoryEntity> findByMonumentId(@Param("monumentId") String monumentId);

    // Utiliser YEAR() dans JPQL
    @Query("SELECT r FROM RestorationHistoryEntity r WHERE YEAR(r.restorationDate) = :year")
    List<RestorationHistoryEntity> findByRestorationDateYear(@Param("year") Integer year);
}