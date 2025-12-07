package com.smart_tourism.smart_tourism.soap.test;

import com.smart_tourism.smart_tourism.soap.model.*;
import com.smart_tourism.smart_tourism.soap.model.request.*;
import com.smart_tourism.smart_tourism.soap.model.response.*;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;

@Component
public class SoapClient {

    private final WebServiceTemplate webServiceTemplate;
    private static final String NAMESPACE_URI = "http://smarttourism.com/soap";

    public SoapClient() {
        this.webServiceTemplate = new WebServiceTemplate();

        // Configuration du marshaller CORRIGÉE
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        // Créer une liste des classes JAXB
        Class<?>[] jaxbClasses = {
                // Requests
                GetHistoricalDescriptionRequest.class,
                GetAnnualTouristStatsRequest.class,
                CompareHeritageSitesRequest.class,

                // Responses
                GetHistoricalDescriptionResponse.class,
                GetAnnualTouristStatsResponse.class,
                CompareHeritageSitesResponse.class,

                // Types complexes
                HistoricalInfo.class,
                TouristStats.class,
                Monument.class
        };

        marshaller.setClassesToBeBound(jaxbClasses);

        this.webServiceTemplate.setMarshaller(marshaller);
        this.webServiceTemplate.setUnmarshaller(marshaller);
        this.webServiceTemplate.setDefaultUri("http://localhost:8090/ws");

        try {
            marshaller.afterPropertiesSet(); // Important !
            System.out.println("✅ Marshaller initialisé avec succès");

            // DEBUG: Vérifier le contexte JAXB créé
            jakarta.xml.bind.JAXBContext jaxbContext = marshaller.getJaxbContext();
            System.out.println("📦 Contexte JAXB créé pour " + jaxbClasses.length + " classes");

        } catch (Exception e) {
            System.err.println("❌ Erreur d'initialisation du marshaller: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur d'initialisation du marshaller", e);
        }
    }

    // Méthode utilitaire pour déboguer le XML généré
    private void logGeneratedXml(Object request) {
        try {
            // Utilisez le marshaller de JAXB directement
            jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(request.getClass());
            jakarta.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            marshaller.marshal(request, writer);

            System.out.println("\n=== DEBUG: XML généré pour " + request.getClass().getSimpleName() + " ===");
            System.out.println(writer.toString());
            System.out.println("===========================================\n");
        } catch (Exception e) {
            System.err.println("Erreur lors du logging XML: " + e.getMessage());
        }
    }

    public Object callWebService(Object request) {
        // DEBUG: Afficher les infos sur la requête
        System.out.println("\n=== DEBUG: Préparation requête ===");
        System.out.println("Type de requête: " + request.getClass().getName());

        jakarta.xml.bind.annotation.XmlRootElement annotation = request.getClass()
                .getAnnotation(jakarta.xml.bind.annotation.XmlRootElement.class);
        if (annotation != null) {
            System.out.println("Annotation @XmlRootElement: " + annotation.name() + " (namespace: "
                    + annotation.namespace() + ")");
        } else {
            System.out.println("⚠️ PAS d'annotation @XmlRootElement !");
        }

        // Log le XML généré
        logGeneratedXml(request);

        // Déterminer le nom de l'opération
        String operationName = getOperationName(request);

        System.out.println("SOAP Action: " + NAMESPACE_URI + "/" + operationName);

        // Créer le callback avec le SOAP Action approprié
        SoapActionCallback callback = new SoapActionCallback(NAMESPACE_URI + "/" + operationName);

        try {
            return webServiceTemplate.marshalSendAndReceive(request, callback);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'appel SOAP: " + e.getMessage());
            throw e;
        }
    }

    private String getOperationName(Object request) {
        String className = request.getClass().getSimpleName();

        if (className.endsWith("Request")) {
            return className.substring(0, className.length() - "Request".length());
        }

        // Fallback
        if (request instanceof GetHistoricalDescriptionRequest) {
            return "GetHistoricalDescription";
        } else if (request instanceof GetAnnualTouristStatsRequest) {
            return "GetAnnualTouristStats";
        } else if (request instanceof CompareHeritageSitesRequest) {
            return "CompareHeritageSites";
        }

        throw new IllegalArgumentException("Type de requête non supporté: " + className);
    }

    // Méthodes spécifiques avec types de retour corrects
    public GetHistoricalDescriptionResponse getHistoricalDescription(String monumentId) {
        GetHistoricalDescriptionRequest request = new GetHistoricalDescriptionRequest();
        request.setMonumentId(monumentId);

        return (GetHistoricalDescriptionResponse) callWebService(request);
    }

    public GetAnnualTouristStatsResponse getAnnualTouristStats(String region, Integer year) {
        GetAnnualTouristStatsRequest request = new GetAnnualTouristStatsRequest();
        request.setRegion(region);
        request.setYear(year);

        return (GetAnnualTouristStatsResponse) callWebService(request);
    }

    public CompareHeritageSitesResponse compareHeritageSites(String siteAId, String siteBId, String... criteria) {
        CompareHeritageSitesRequest request = new CompareHeritageSitesRequest();
        request.setSiteAId(siteAId);
        request.setSiteBId(siteBId);
        if (criteria != null) {
            request.setComparisonCriteria(java.util.Arrays.asList(criteria));
        }

        return (CompareHeritageSitesResponse) callWebService(request);
    }
}