package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comparisonMap")
public class ComparisonMap {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Map<String, String> entries = new HashMap<>();
}