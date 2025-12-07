package com.smart_tourism.smart_tourism.soap.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monthly_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "month_name", nullable = false, length = 20)
    private String monthName;

    @Column(name = "visitors")
    private Integer visitors;

    // Relation ManyToOne avec TouristStats - IMPORTANT : @JoinColumn avec columnDefinition
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tourist_stats_id",
            nullable = false,
            columnDefinition = "INT"  // ← Spécifie le type MySQL
    )
    private TouristStatsEntity touristStats;
}