package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "touristStats", propOrder = {"region", "year", "totalVisitors",
        "internationalVisitors", "monthlyStats", "growthRate"})
public class TouristStats {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String region;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Integer year;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Integer totalVisitors;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Integer internationalVisitors;

    // CORRECTION : List<MonthlyStatEntry> au lieu de Map
    @XmlElementWrapper(name = "monthlyStats", namespace = "http://smarttourism.com/soap")
    @XmlElement(name = "monthlyStat", namespace = "http://smarttourism.com/soap")
    private List<MonthlyStatEntry> monthlyStats = new ArrayList<>();

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Double growthRate;
}