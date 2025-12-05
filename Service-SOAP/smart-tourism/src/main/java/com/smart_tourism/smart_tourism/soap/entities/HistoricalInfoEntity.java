package com.smart_tourism.smart_tourism.soap.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HistoricalInfo")
@Data // Génère getters, setters, equals, hashCode, toString
@NoArgsConstructor // Génère constructeur sans paramètres
@AllArgsConstructor // Génère constructeur avec tous les paramètres
@Builder // Permet d'utiliser le pattern Builder
public class HistoricalInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relation OneToOne avec Monument
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monument_id", referencedColumnName = "monument_id", nullable = false)
    private MonumentEntity monument;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "historical_significance", columnDefinition = "TEXT")
    private String historicalSignificance;

    @Column(name = "official_classification", length = 255)
    private String officialClassification;

    @Lob
    @Column(name = "cultural_importance", columnDefinition = "TEXT")
    private String culturalImportance;

    // Pas besoin de constructeurs ni getters/setters manuels
    // Lombok les génère automatiquement !
}