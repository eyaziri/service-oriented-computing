package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comparisonEntry", propOrder = {"key", "value"})
public class ComparisonEntry {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String key;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String value;
}