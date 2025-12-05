package com.smart_tourism.smart_tourism.soap.test;

import com.smart_tourism.smart_tourism.soap.model.request.*;
import com.smart_tourism.smart_tourism.soap.model.response.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SoapTestRunner implements CommandLineRunner {

    private final SoapClient soapClient;

    public SoapTestRunner(SoapClient soapClient) {
        this.soapClient = soapClient;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🧪 Début des tests SOAP...");

        // Test avec les vrais IDs de votre base (minuscules)
        testGetHistoricalDescription("m001");  // Musée National du Bardo
        testGetHistoricalDescription("m002");  // Amphithéâtre d'EI Jem
        testGetHistoricalDescription("m003");  // Médina de Tunis

        // Test 2: Récupérer les statistiques
        testGetTouristStats();

        // Test 3: Comparer des sites (utilisez les vrais IDs)
        testCompareSites("m001", "m002");

        System.out.println("\n🧪 Tous les tests sont terminés !");
    }

    private void testGetHistoricalDescription(String monumentId) {
        System.out.println("\n📚 Test: GetHistoricalDescription pour " + monumentId);

        try {
            // Utilisez la méthode spécifique du client
            GetHistoricalDescriptionResponse response = soapClient.getHistoricalDescription(monumentId);

            System.out.println("📊 Status: " + response.getStatus());
            System.out.println("💬 Message: " + response.getMessage());

            // Vérifier si historicalInfo n'est pas null
            if (response.getHistoricalInfo() != null) {
                com.smart_tourism.smart_tourism.soap.model.HistoricalInfo info = response.getHistoricalInfo();

                System.out.println("✅ INFORMATIONS TROUVÉES:");
                System.out.println("   🏛 Monument ID: " + info.getMonumentId());
                System.out.println("   📋 Description: " + info.getDescription());

                if (info.getHistoricalSignificance() != null) {
                    System.out.println("   🎖 Importance historique: " + info.getHistoricalSignificance());
                }

                if (info.getCulturalImportance() != null) {
                    System.out.println("   🎨 Importance culturelle: " + info.getCulturalImportance());
                }

                if (info.getOfficialClassification() != null) {
                    System.out.println("   🏷 Classification: " + info.getOfficialClassification());
                }

                if (info.getRestorationHistory() != null && !info.getRestorationHistory().isEmpty()) {
                    System.out.println("   🔧 Historique des restaurations:");
                    info.getRestorationHistory().forEach(restoration ->
                            System.out.println("      • " + restoration));
                }

            } else {
                System.out.println("⚠️ Aucune information disponible pour ce monument");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testGetTouristStats() {
        System.out.println("\n📊 Test: GetAnnualTouristStats");

        try {
            // Utilisez la méthode spécifique du client
            GetAnnualTouristStatsResponse response = soapClient.getAnnualTouristStats("Tunis", 2023);

            System.out.println("📊 Status: " + response.getStatus());
            System.out.println("💬 Message: " + response.getMessage());

            if (response.getTouristStats() != null) {
                com.smart_tourism.smart_tourism.soap.model.TouristStats stats = response.getTouristStats();
                System.out.println("✅ STATISTIQUES TROUVÉES:");
                System.out.println("   🌍 Région: " + stats.getRegion());
                System.out.println("   📅 Année: " + stats.getYear());
                System.out.println("   👥 Total visiteurs: " + stats.getTotalVisitors());
                System.out.println("   🌐 Visiteurs internationaux: " + stats.getInternationalVisitors());
                System.out.println("   📈 Taux de croissance: " + stats.getGrowthRate() + "%");

                // Afficher les statistiques mensuelles si disponibles
                if (stats.getMonthlyStats() != null && !stats.getMonthlyStats().isEmpty()) {
                    System.out.println("   📊 Statistiques mensuelles:");
                    stats.getMonthlyStats().forEach(monthlyStat ->
                            System.out.println("      • " + monthlyStat.getMonth() + ": " + monthlyStat.getVisitors() + " visiteurs"));
                }
            } else {
                System.out.println("⚠️ Aucune statistique disponible");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors du test GetAnnualTouristStats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testCompareSites(String siteAId, String siteBId) {
        System.out.println("\n⚖️ Test: CompareHeritageSites");

        try {
            // Utilisez la méthode spécifique du client
            CompareHeritageSitesResponse response = soapClient.compareHeritageSites(siteAId, siteBId, "historical_significance");

            System.out.println("📊 Status: " + response.getStatus());
            System.out.println("💬 Message: " + response.getMessage());

            if ("SUCCESS".equals(response.getStatus())) {
                System.out.println("✅ COMPARAISON RÉUSSIE:");
                System.out.println("   🏛 Site A: " + response.getSiteAName());
                System.out.println("   🏛 Site B: " + response.getSiteBName());
                System.out.println("   💡 Recommandation: " + response.getRecommendation());

                // Afficher les détails de la comparaison
                if (response.getComparisons() != null && !response.getComparisons().isEmpty()) {
                    System.out.println("   📋 Détails de la comparaison:");
                    response.getComparisons().forEach((key, value) ->
                            System.out.println("      • " + key + ": " + value));
                }
            } else {
                System.out.println("⚠️ Comparaison non disponible");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors du test CompareHeritageSites: " + e.getMessage());
            e.printStackTrace();
        }
    }
}