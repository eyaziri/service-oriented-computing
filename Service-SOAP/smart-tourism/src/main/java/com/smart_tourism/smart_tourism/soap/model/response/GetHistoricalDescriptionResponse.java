package com.smart_tourism.smart_tourism.soap.model.response;

import com.smart_tourism.smart_tourism.soap.model.HistoricalInfo;
import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetHistoricalDescriptionResponse", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"historicalInfo", "status", "message"})
public class GetHistoricalDescriptionResponse {

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private HistoricalInfo historicalInfo;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String status;

    @XmlElement(namespace = "http://smarttourism.com/soap")
    private String message;
}