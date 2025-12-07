package com.smart_tourism.smart_tourism.soap.endpoint;

import com.smart_tourism.smart_tourism.soap.model.*;
import com.smart_tourism.smart_tourism.soap.model.request.*;
import com.smart_tourism.smart_tourism.soap.model.response.*;
import com.smart_tourism.smart_tourism.soap.service.CulturalArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.HashMap;
import java.util.Map;

@Endpoint
public class CulturalArchiveEndpoint {

    private static final String NAMESPACE_URI = "http://smarttourism.com/soap";

    private final CulturalArchiveService culturalArchiveService;

    @Autowired
    public CulturalArchiveEndpoint(CulturalArchiveService culturalArchiveService) {
        this.culturalArchiveService = culturalArchiveService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetHistoricalDescriptionRequest")
    @ResponsePayload
    public GetHistoricalDescriptionResponse getHistoricalDescription(
            @RequestPayload GetHistoricalDescriptionRequest request) {

        GetHistoricalDescriptionResponse response = new GetHistoricalDescriptionResponse();

        try {
            HistoricalInfo info = culturalArchiveService
                    .getHistoricalDescription(request.getMonumentId());

            response.setHistoricalInfo(info);
            response.setStatus("SUCCESS");
            response.setMessage("Informations historiques récupérées avec succès");

        } catch (Exception e) {
            response.setHistoricalInfo(new HistoricalInfo());
            response.setStatus("ERROR");
            response.setMessage("Erreur lors de la récupération: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetAnnualTouristStatsRequest")
    @ResponsePayload
    public GetAnnualTouristStatsResponse getAnnualTouristStats(
            @RequestPayload GetAnnualTouristStatsRequest request) {

        GetAnnualTouristStatsResponse response = new GetAnnualTouristStatsResponse();

        try {
            TouristStats stats = culturalArchiveService
                    .getAnnualTouristStats(request.getRegion(), request.getYear());

            response.setTouristStats(stats);
            response.setStatus("SUCCESS");
            response.setMessage("Statistiques touristiques récupérées avec succès");

        } catch (Exception e) {
            response.setTouristStats(new TouristStats());
            response.setStatus("ERROR");
            response.setMessage("Erreur lors de la récupération: " + e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CompareHeritageSitesRequest")
    @ResponsePayload
    public CompareHeritageSitesResponse compareHeritageSites(
            @RequestPayload CompareHeritageSitesRequest request) {

        CompareHeritageSitesResponse response = new CompareHeritageSitesResponse();

        try {
            // CONVERSION : Liste vers String (en joignant avec des virgules)
            String criteriaString = "";
            if (request.getComparisonCriteria() != null && !request.getComparisonCriteria().isEmpty()) {
                criteriaString = String.join(",", request.getComparisonCriteria());
            }

            // Effectuer la comparaison avec le String
            Map<String, String> comparisons = culturalArchiveService
                    .compareHeritageSites(
                            request.getSiteAId(),
                            request.getSiteBId(),
                            criteriaString  // Maintenant c'est un String
                    );

            response.setComparisons(comparisons);

            // Récupérer les noms des sites
            HistoricalInfo siteAInfo = culturalArchiveService
                    .getHistoricalDescription(request.getSiteAId());
            HistoricalInfo siteBInfo = culturalArchiveService
                    .getHistoricalDescription(request.getSiteBId());

            response.setSiteAName(siteAInfo != null ? "Site A" : "Inconnu");
            response.setSiteBName(siteBInfo != null ? "Site B" : "Inconnu");

            // Générer une recommandation
            if (comparisons.containsKey("result")) {
                response.setRecommendation("Voir comparaison détaillée ci-dessus");
            } else {
                response.setRecommendation("Les deux sites sont comparables selon différents critères");
            }

            response.setStatus("SUCCESS");
            response.setMessage("Comparaison effectuée avec succès");

        } catch (Exception e) {
            response.setSiteAName("Erreur");
            response.setSiteBName("Erreur");
            response.setComparisons(new HashMap<>());
            response.setRecommendation("Impossible de comparer");
            response.setStatus("ERROR");
            response.setMessage("Erreur lors de la comparaison: " + e.getMessage());
        }

        return response;
    }
}