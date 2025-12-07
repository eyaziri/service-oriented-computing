package com.smart_tourism.smart_tourism.soap.model.response;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "CompareHeritageSitesResponse", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"siteAName", "siteBName", "comparisons", "recommendation", "status", "message"})
public class CompareHeritageSitesResponse {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String siteAName;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String siteBName;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Map<String, String> comparisons;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String recommendation;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String status;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String message;
}