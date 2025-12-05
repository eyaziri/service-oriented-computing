package com.smart_tourism.smart_tourism.soap.model.response;

import com.smart_tourism.smart_tourism.soap.model.TouristStats;
import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetAnnualTouristStatsResponse", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"touristStats", "status", "message"})
public class GetAnnualTouristStatsResponse {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private TouristStats touristStats;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String status;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String message;
}