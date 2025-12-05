package com.smarttourism.attractions.exception;

public enum ErrorCode {
    // Erreurs générales
    INTERNAL_SERVER_ERROR("ERR-001", "Erreur interne du serveur"),
    VALIDATION_ERROR("ERR-002", "Erreur de validation"),
    
    // Erreurs liées aux attractions
    ATTRACTION_NOT_FOUND("ATTR-001", "Attraction non trouvée"),
    ATTRACTION_ALREADY_EXISTS("ATTR-002", "Attraction déjà existante"),
    ATTRACTION_INACTIVE("ATTR-003", "Attraction inactive"),
    ATTRACTION_FULL("ATTR-004", "Attraction au maximum de sa capacité"),
    ATTRACTION_CLOSED("ATTR-005", "Attraction fermée"),
    
    // Erreurs liées aux réservations
    RESERVATION_NOT_FOUND("RES-001", "Réservation non trouvée"),
    RESERVATION_CONFLICT("RES-002", "Conflit de réservation"),
    RESERVATION_CANCELLED("RES-003", "Réservation déjà annulée"),
    RESERVATION_DATE_PASSED("RES-004", "Date de visite dépassée"),
    RESERVATION_LIMIT_EXCEEDED("RES-005", "Limite de réservation dépassée"),
    RESERVATION_ALREADY_EXISTS("RES-006", "Réservation déjà existante pour cette date"),
    RESERVATION_CODE_INVALID("RES-007", "Code de réservation invalide"),
    
    // Erreurs liées aux avis
    REVIEW_NOT_FOUND("REV-001", "Avis non trouvé"),
    REVIEW_ALREADY_EXISTS("REV-002", "Vous avez déjà posté un avis pour cette attraction"),
    INVALID_RATING("REV-003", "Note invalide. Doit être entre 1 et 5"),
    REVIEW_UPDATE_NOT_ALLOWED("REV-004", "Modification de l'avis non autorisée"),
    
    // Erreurs de validation
    INVALID_DATE("VAL-001", "Date invalide"),
    INVALID_PRICE("VAL-002", "Prix invalide"),
    INVALID_CAPACITY("VAL-003", "Capacité invalide"),
    INVALID_PARAMETERS("VAL-004", "Paramètres invalides"),
    
    // Erreurs d'authentification/autorisation
    UNAUTHORIZED("AUTH-001", "Non autorisé"),
    FORBIDDEN("AUTH-002", "Accès interdit"),
    
    // Erreurs de contraintes
    CONSTRAINT_VIOLATION("CONS-001", "Violation de contrainte"),
    DATA_INTEGRITY_VIOLATION("CONS-002", "Violation d'intégrité des données"),
    
    // Erreurs de disponibilité
    NO_AVAILABILITY("AVAIL-001", "Pas de disponibilité pour cette date"),
    TIME_SLOT_UNAVAILABLE("AVAIL-002", "Créneau horaire indisponible");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}