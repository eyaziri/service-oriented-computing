package com.smart_tourism.smart_tourism.soap.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "touriststats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INT")  // ← Force INT au lieu de BIGINT
    private Long id;

    @Column(name = "region", nullable = false, length = 150)
    private String region;

    @Column(name = "year", nullable = false)
    private Short year;

    @Column(name = "total_visitors")
    private Integer totalVisitors;

    @Column(name = "international_visitors")
    private Integer internationalVisitors;

    @Column(name = "growth_rate")
    private Double growthRate;

    // Relation OneToMany avec MonthlyStats
    @OneToMany(mappedBy = "touristStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MonthlyStatsEntity> monthlyStats = new ArrayList<>();
}