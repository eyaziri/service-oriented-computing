package com.smart_tourism.smart_tourism.soap.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "monument")
@Data  // Génère getters, setters, equals, hashCode, toString
@NoArgsConstructor  // Génère constructeur sans paramètres
@AllArgsConstructor  // Génère constructeur avec tous les paramètres
@Builder  // Permet d'utiliser le pattern Builder
public class MonumentEntity {

    @Id
    @Column(name = "monument_id", length = 50)
    private String monumentId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "year_built")
    private Short yearBuilt; // SMALLINT en SQL

    @Column(name = "architectural_style", length = 150)
    private String architecturalStyle;

    @Column(name = "unesco_heritage")
    private Boolean unescoHeritage = false;

    @Column(name = "historical_period", length = 100)
    private String historicalPeriod;

    // Relation OneToOne avec HistoricalInfo
    @OneToOne(mappedBy = "monument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HistoricalInfoEntity historicalInfo;

    // Relation OneToMany avec RestorationHistory
    @OneToMany(mappedBy = "monument", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default  // Important pour initialiser la liste avec Builder
    private List<RestorationHistoryEntity> restorationHistories = new ArrayList<>();

    // NE PAS AJOUTER de getters/setters manuels ici !
    // Lombok les génère automatiquement grâce à @Data
}