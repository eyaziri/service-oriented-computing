package com.smart_tourism.smart_tourism.soap.mapper;

import com.smart_tourism.smart_tourism.soap.entities.*;
import com.smart_tourism.smart_tourism.soap.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ModelMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // MonumentEntity -> Monument
    public Monument toMonumentModel(MonumentEntity entity) {
        if (entity == null) return null;

        Monument model = new Monument();
        model.setMonumentId(entity.getMonumentId());
        model.setName(entity.getName());
        model.setCity(entity.getCity());
        model.setYearBuilt(entity.getYearBuilt() != null ? entity.getYearBuilt().intValue() : null);
        model.setArchitecturalStyle(entity.getArchitecturalStyle());
        model.setUnescoHeritage(entity.getUnescoHeritage());
        model.setHistoricalPeriod(entity.getHistoricalPeriod());

        return model;
    }

    // HistoricalInfoEntity -> HistoricalInfo
    public HistoricalInfo toHistoricalInfoModel(HistoricalInfoEntity entity) {
        if (entity == null) return null;

        HistoricalInfo model = new HistoricalInfo();

        if (entity.getMonument() != null) {
            model.setMonumentId(entity.getMonument().getMonumentId());
        } else {
            model.setMonumentId("Inconnu");
        }

        model.setDescription(entity.getDescription());
        model.setHistoricalSignificance(entity.getHistoricalSignificance());
        model.setOfficialClassification(entity.getOfficialClassification());
        model.setCulturalImportance(entity.getCulturalImportance());

        // Récupérer les restaurations
        if (entity.getMonument() != null &&
                entity.getMonument().getRestorationHistories() != null) {

            List<String> restorations = entity.getMonument().getRestorationHistories().stream()
                    .map(r -> {
                        String year = r.getRestorationDate() != null
                                ? String.valueOf(r.getRestorationDate().getYear())
                                : "Année inconnue";
                        return year + ": " + (r.getNote() != null ? r.getNote() : "");
                    })
                    .collect(Collectors.toList());
            model.setRestorationHistory(restorations);
        } else {
            model.setRestorationHistory(new ArrayList<>());
        }

        return model;
    }

    public TouristStats toTouristStatsModel(TouristStatsEntity entity) {
        if (entity == null) return null;

        TouristStats model = new TouristStats();
        model.setRegion(entity.getRegion());
        model.setYear(entity.getYear() != null ? entity.getYear().intValue() : null);
        model.setTotalVisitors(entity.getTotalVisitors());
        model.setInternationalVisitors(entity.getInternationalVisitors());
        model.setGrowthRate(entity.getGrowthRate());

        // CORRECTION ICI : Créer une List<MonthlyStatEntry> au lieu de Map<String, Integer>
        if (entity.getMonthlyStats() != null && !entity.getMonthlyStats().isEmpty()) {
            List<MonthlyStatEntry> monthlyEntries = new ArrayList<>();
            for (MonthlyStatsEntity monthly : entity.getMonthlyStats()) {
                MonthlyStatEntry entry = new MonthlyStatEntry();
                entry.setMonth(monthly.getMonthName());
                entry.setVisitors(monthly.getVisitors());
                monthlyEntries.add(entry);
            }
            model.setMonthlyStats(monthlyEntries);
        } else {
            model.setMonthlyStats(new ArrayList<>()); // Liste vide au lieu de Map vide
        }

        return model;
    }

    // Monument -> MonumentEntity
    public MonumentEntity toMonumentEntity(Monument model) {
        if (model == null) return null;

        return MonumentEntity.builder()
                .monumentId(model.getMonumentId())
                .name(model.getName())
                .city(model.getCity())
                .yearBuilt(model.getYearBuilt() != null ? model.getYearBuilt().shortValue() : null)
                .architecturalStyle(model.getArchitecturalStyle())
                .unescoHeritage(model.getUnescoHeritage())
                .historicalPeriod(model.getHistoricalPeriod())
                .build();
    }
}