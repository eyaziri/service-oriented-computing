package com.smart_tourism.smart_tourism.soap.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "restoration_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestorationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seq")
    private Short seq;

    @Lob
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "restoration_date")
    private LocalDate restorationDate;

    // Relation ManyToOne avec Monument
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monument_id", nullable = false)
    private MonumentEntity monument;
}