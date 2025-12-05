package com.smart_tourism.smart_tourism.soap.model.request;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetAnnualTouristStatsRequest", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"region", "year"})
public class GetAnnualTouristStatsRequest {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String region;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private Integer year;
}