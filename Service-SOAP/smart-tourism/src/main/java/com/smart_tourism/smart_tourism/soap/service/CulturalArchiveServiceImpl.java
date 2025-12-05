package com.smart_tourism.smart_tourism.soap.service;

import com.smart_tourism.smart_tourism.soap.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import com.smart_tourism.smart_tourism.soap.model.*;
import com.smart_tourism.smart_tourism.soap.entities.*;
import com.smart_tourism.smart_tourism.soap.repositories.*;
import com.smart_tourism.smart_tourism.soap.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CulturalArchiveServiceImpl implements CulturalArchiveService {

    @Autowired
    private MonumentRepository monumentRepository;

    @Autowired
    private HistoricalInfoRepository historicalInfoRepository;

    @Autowired
    private TouristStatsRepository touristStatsRepository;

    @Autowired
    private RestorationHistoryRepository restorationHistoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public HistoricalInfo getHistoricalDescription(String monumentId) {
        try {
            // Charger le monument avec ses informations historiques
            Optional<MonumentEntity> monument = monumentRepository
                    .findByIdWithHistoricalInfo(monumentId);

            if (monument.isPresent() && monument.get().getHistoricalInfo() != null) {
                return modelMapper.toHistoricalInfoModel(monument.get().getHistoricalInfo());
            }

            // Fallback: chercher directement dans HistoricalInfo
            Optional<HistoricalInfoEntity> historicalInfo = historicalInfoRepository
                    .findByMonumentMonumentId(monumentId);

            if (historicalInfo.isPresent()) {
                return modelMapper.toHistoricalInfoModel(historicalInfo.get());
            }

            // Retourner des informations par défaut
            return createDefaultHistoricalInfo(monumentId);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }

    @Override
    public TouristStats getAnnualTouristStats(String region, Integer year) {
        try {
            if (year == null) {
                year = Calendar.getInstance().get(Calendar.YEAR) - 1;
            }

            Short yearShort = (short) year.intValue();

            // Charger avec les stats mensuelles
            Optional<TouristStatsEntity> stats = touristStatsRepository
                    .findByRegionAndYearWithMonthlyStats(region, yearShort);

            if (stats.isPresent()) {
                return modelMapper.toTouristStatsModel(stats.get());
            }

            // Fallback: chercher sans les stats mensuelles
            Optional<TouristStatsEntity> simpleStats = touristStatsRepository
                    .findByRegionAndYear(region, yearShort);

            if (simpleStats.isPresent()) {
                return modelMapper.toTouristStatsModel(simpleStats.get());
            }

            // Retourner des stats par défaut
            return createDefaultTouristStats(region, year);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des stats: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> compareHeritageSites(String siteAId, String siteBId, String criteria) {
        Map<String, String> comparison = new HashMap<>();

        try {
            // Récupérer les monuments avec leurs infos
            Optional<MonumentEntity> siteAOpt = monumentRepository.findById(siteAId);
            Optional<MonumentEntity> siteBOpt = monumentRepository.findById(siteBId);

            if (siteAOpt.isEmpty() || siteBOpt.isEmpty()) {
                comparison.put("error", "Un ou plusieurs monuments non trouvés");
                return comparison;
            }

            MonumentEntity siteA = siteAOpt.get();
            MonumentEntity siteB = siteBOpt.get();

            // Informations de base
            comparison.put("comparisonDate", new Date().toString());
            comparison.put("siteA", siteA.getName());
            comparison.put("siteB", siteB.getName());
            comparison.put("siteA_City", siteA.getCity());
            comparison.put("siteB_City", siteB.getCity());

            // Critères de comparaison
            if (criteria == null || criteria.trim().isEmpty()) {
                criteria = "general";
            }

            switch (criteria.toLowerCase()) {
                case "age":
                    compareByAge(siteA, siteB, comparison);
                    break;
                case "unesco":
                    compareByUnesco(siteA, siteB, comparison);
                    break;
                case "city":
                    compareByCity(siteA, siteB, comparison);
                    break;
                default:
                    compareGeneral(siteA, siteB, comparison);
                    break;
            }

            // Ajouter une recommandation
            comparison.put("recommendation", generateRecommendation(siteA, siteB, comparison));

            return comparison;

        } catch (Exception e) {
            comparison.put("error", "Erreur lors de la comparaison: " + e.getMessage());
            return comparison;
        }
    }

    @Override
    public List<Monument> getMonumentsByPeriod(String period) {
        try {
            List<MonumentEntity> monuments = monumentRepository.findByHistoricalPeriod(period);
            return monuments.stream()
                    .map(modelMapper::toMonumentModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean verifyOfficialClassification(String monumentId) {
        try {
            Optional<HistoricalInfoEntity> historicalInfo = historicalInfoRepository
                    .findByMonumentMonumentId(monumentId);

            return historicalInfo.isPresent() &&
                    historicalInfo.get().getOfficialClassification() != null &&
                    !historicalInfo.get().getOfficialClassification().trim().isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de vérification: " + e.getMessage(), e);
        }
    }

    // Méthodes utilitaires privées
    private HistoricalInfo createDefaultHistoricalInfo(String monumentId) {
        HistoricalInfo info = new HistoricalInfo();
        info.setMonumentId(monumentId);
        info.setDescription("Aucune information historique disponible pour ce monument.");
        info.setHistoricalSignificance("Non documenté");
        info.setOfficialClassification("Non classé");
        info.setCulturalImportance("Inconnue");
        info.setRestorationHistory(new ArrayList<>());
        return info;
    }

    private TouristStats createDefaultTouristStats(String region, Integer year) {
        TouristStats stats = new TouristStats();
        stats.setRegion(region);
        stats.setYear(year);
        stats.setTotalVisitors(0);
        stats.setInternationalVisitors(0);
        stats.setGrowthRate(0.0);

        // CORRECTION ICI : Liste vide de MonthlyStatEntry au lieu de HashMap
        stats.setMonthlyStats(new ArrayList<>());

        return stats;
    }

    private void compareByAge(MonumentEntity siteA, MonumentEntity siteB, Map<String, String> comparison) {
        if (siteA.getYearBuilt() != null && siteB.getYearBuilt() != null) {
            int ageDiff = siteA.getYearBuilt() - siteB.getYearBuilt();
            comparison.put("ageA", String.valueOf(siteA.getYearBuilt()));
            comparison.put("ageB", String.valueOf(siteB.getYearBuilt()));

            if (ageDiff > 0) {
                comparison.put("ageComparison", siteA.getName() + " est " + ageDiff + " ans plus récent que " + siteB.getName());
            } else if (ageDiff < 0) {
                comparison.put("ageComparison", siteA.getName() + " est " + (-ageDiff) + " ans plus ancien que " + siteB.getName());
            } else {
                comparison.put("ageComparison", "Les deux monuments ont été construits la même année");
            }
        }
    }

    private void compareByUnesco(MonumentEntity siteA, MonumentEntity siteB, Map<String, String> comparison) {
        comparison.put("unescoA", siteA.getUnescoHeritage() ? "Patrimoine UNESCO" : "Non UNESCO");
        comparison.put("unescoB", siteB.getUnescoHeritage() ? "Patrimoine UNESCO" : "Non UNESCO");

        if (siteA.getUnescoHeritage() && siteB.getUnescoHeritage()) {
            comparison.put("unescoComparison", "Les deux sont classés UNESCO");
        } else if (siteA.getUnescoHeritage()) {
            comparison.put("unescoComparison", siteA.getName() + " seulement est classé UNESCO");
        } else if (siteB.getUnescoHeritage()) {
            comparison.put("unescoComparison", siteB.getName() + " seulement est classé UNESCO");
        } else {
            comparison.put("unescoComparison", "Aucun n'est classé UNESCO");
        }
    }

    private void compareGeneral(MonumentEntity siteA, MonumentEntity siteB, Map<String, String> comparison) {
        compareByAge(siteA, siteB, comparison);
        compareByUnesco(siteA, siteB, comparison);
        compareByCity(siteA, siteB, comparison);
        comparison.put("styleA", siteA.getArchitecturalStyle() != null ? siteA.getArchitecturalStyle() : "Inconnu");
        comparison.put("styleB", siteB.getArchitecturalStyle() != null ? siteB.getArchitecturalStyle() : "Inconnu");
        comparison.put("comparisonType", "Comparaison générale");
    }

    private void compareByCity(MonumentEntity siteA, MonumentEntity siteB, Map<String, String> comparison) {
        if (siteA.getCity() != null && siteB.getCity() != null) {
            if (siteA.getCity().equalsIgnoreCase(siteB.getCity())) {
                comparison.put("cityComparison", "Les deux monuments sont dans la même ville: " + siteA.getCity());
            } else {
                comparison.put("cityComparison", "Villes différentes: " + siteA.getCity() + " vs " + siteB.getCity());
            }
        }
    }

    private String generateRecommendation(MonumentEntity siteA, MonumentEntity siteB, Map<String, String> comparison) {
        StringBuilder recommendation = new StringBuilder();

        // Priorité 1: UNESCO
        if (siteA.getUnescoHeritage() && !siteB.getUnescoHeritage()) {
            recommendation.append("Recommandation: ").append(siteA.getName())
                    .append(" (classé UNESCO)");
        } else if (!siteA.getUnescoHeritage() && siteB.getUnescoHeritage()) {
            recommendation.append("Recommandation: ").append(siteB.getName())
                    .append(" (classé UNESCO)");
        }
        // Priorité 2: Ancienneté
        else if (siteA.getYearBuilt() != null && siteB.getYearBuilt() != null) {
            if (siteA.getYearBuilt() < siteB.getYearBuilt()) {
                recommendation.append("Recommandation: ").append(siteA.getName())
                        .append(" (plus ancien, construit en ").append(siteA.getYearBuilt()).append(")");
            } else {
                recommendation.append("Recommandation: ").append(siteB.getName())
                        .append(" (plus ancien, construit en ").append(siteB.getYearBuilt()).append(")");
            }
        }
        // Priorité 3: Style architectural
        else if (siteA.getArchitecturalStyle() != null && siteB.getArchitecturalStyle() != null) {
            recommendation.append("Choisissez selon le style: ")
                    .append(siteA.getName()).append(" (").append(siteA.getArchitecturalStyle()).append(") ou ")
                    .append(siteB.getName()).append(" (").append(siteB.getArchitecturalStyle()).append(")");
        }
        // Fallback
        else {
            recommendation.append("Les deux monuments sont intéressants. ")
                    .append(siteA.getName()).append(" à ").append(siteA.getCity()).append(" et ")
                    .append(siteB.getName()).append(" à ").append(siteB.getCity());
        }

        return recommendation.toString();
    }

    // Méthode pour initialiser des données de test
    public void initializeSampleData() {
        try {
            if (monumentRepository.count() == 0) {
                System.out.println("🔄 Initialisation des données de test...");

                // 1. Créer des monuments
                MonumentEntity bardo = MonumentEntity.builder()
                        .monumentId("M001")
                        .name("Musée National du Bardo")
                        .city("Tunis")
                        .yearBuilt((short) 1888)
                        .architecturalStyle("Mauresque")
                        .unescoHeritage(true)
                        .historicalPeriod("Ottoman")
                        .build();

                MonumentEntity zitouna = MonumentEntity.builder()
                        .monumentId("M002")
                        .name("Mosquée Zitouna")
                        .city("Tunis")
                        .yearBuilt((short) 698)
                        .architecturalStyle("Islamique")
                        .unescoHeritage(true)
                        .historicalPeriod("Omeyyade")
                        .build();

                monumentRepository.saveAll(Arrays.asList(bardo, zitouna));

                // 2. Ajouter des informations historiques
                HistoricalInfoEntity bardoHistory = HistoricalInfoEntity.builder()
                        .monument(bardo)
                        .description("Plus grand musée archéologique de Tunisie")
                        .historicalSignificance("Ancien palais beylical transformé en musée")
                        .officialClassification("Classé Monument Historique en 1985")
                        .culturalImportance("Patrimoine culturel national")
                        .build();

                historicalInfoRepository.save(bardoHistory);

                // 3. Ajouter des restaurations
                RestorationHistoryEntity restoration1 = RestorationHistoryEntity.builder()
                        .monument(bardo)
                        .seq((short) 1)
                        .note("Première extension du musée")
                        .restorationDate(java.time.LocalDate.of(1900, 1, 1))
                        .build();

                restorationHistoryRepository.save(restoration1);

                // 4. Ajouter des statistiques touristiques
                TouristStatsEntity tunisStats = TouristStatsEntity.builder()
                        .region("Tunis")
                        .year((short) 2023)
                        .totalVisitors(2500000)
                        .internationalVisitors(1500000)
                        .growthRate(12.5)
                        .build();

                // 5. Ajouter des stats mensuelles
                MonthlyStatsEntity janStats = MonthlyStatsEntity.builder()
                        .touristStats(tunisStats)
                        .monthName("Janvier")
                        .visitors(150000)
                        .build();

                MonthlyStatsEntity febStats = MonthlyStatsEntity.builder()
                        .touristStats(tunisStats)
                        .monthName("Février")
                        .visitors(180000)
                        .build();

                tunisStats.setMonthlyStats(Arrays.asList(janStats, febStats));
                touristStatsRepository.save(tunisStats);

                System.out.println("✅ Données de test initialisées avec succès!");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
        }
    }
}