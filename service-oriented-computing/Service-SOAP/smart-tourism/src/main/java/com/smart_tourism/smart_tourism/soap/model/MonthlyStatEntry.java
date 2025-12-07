package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monthlyStatEntry", propOrder = {"month", "visitors"})
public class MonthlyStatEntry {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String month;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Integer visitors;
}