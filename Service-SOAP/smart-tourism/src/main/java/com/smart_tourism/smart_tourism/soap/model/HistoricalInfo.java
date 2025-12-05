package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "historicalInfo", propOrder = {"monumentId", "description", "historicalSignificance",
        "restorationHistory", "culturalImportance", "officialClassification"})
public class HistoricalInfo {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String monumentId;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String description;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String historicalSignificance;

    // CORRECTION : List<String> pour restorationHistory
    @XmlElement(name = "restorationHistory", namespace = "http://smarttourism.com/soap")
    private List<String> restorationHistory = new ArrayList<>();

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String culturalImportance;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String officialClassification;
}