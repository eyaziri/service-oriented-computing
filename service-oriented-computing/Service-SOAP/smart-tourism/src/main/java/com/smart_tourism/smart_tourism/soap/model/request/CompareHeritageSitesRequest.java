package com.smart_tourism.smart_tourism.soap.model.request;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "CompareHeritageSitesRequest", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"siteAId", "siteBId", "comparisonCriteria"})
public class CompareHeritageSitesRequest {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String siteAId;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String siteBId;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private List<String> comparisonCriteria = new ArrayList<>();
}