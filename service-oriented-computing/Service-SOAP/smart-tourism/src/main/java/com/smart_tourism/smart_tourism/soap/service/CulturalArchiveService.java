package com.smart_tourism.smart_tourism.soap.service;

import com.smart_tourism.smart_tourism.soap.model.*;

import java.util.List;
import java.util.Map;

public interface CulturalArchiveService {

    HistoricalInfo getHistoricalDescription(String monumentId);

    TouristStats getAnnualTouristStats(String region, Integer year);

    Map<String, String> compareHeritageSites(String siteAId, String siteBId, String criteria);

    // Méthodes supplémentaires
    List<Monument> getMonumentsByPeriod(String period);

    Boolean verifyOfficialClassification(String monumentId);
}
